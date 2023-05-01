package com.kitclub.kiteventqrscanner.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.kitclub.kiteventqrscanner.R
import com.kitclub.kiteventqrscanner.broadcastreceiver.InternetConnectionChangeReceiver
import com.kitclub.kiteventqrscanner.database.RealmHelper
import com.kitclub.kiteventqrscanner.firebase.FirebaseHelper
import com.kitclub.kiteventqrscanner.qrhelper.QRHelper
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSION_CAMERA_REQUEST = 1
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    private lateinit var previewView: androidx.camera.view.PreviewView
    private lateinit var resultTV: TextView
    private lateinit var connectionWarningTV: TextView
    private lateinit var flashToggleButton: ImageButton

    private lateinit var actionReceiver: InternetConnectionChangeReceiver

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null

    private var camera: Camera? = null

    private var flashState = false

    private val screenAspectRatio: Int
        get() {
            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics().also { previewView.display?.getRealMetrics(it) }
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    private var recentCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        resultTV = findViewById(R.id.scan_result_tv)
        previewView = findViewById(R.id.camera_preview)
        connectionWarningTV = findViewById(R.id.connection_loss_warning_tv)
        flashToggleButton = findViewById(R.id.flash_toggle_btn)


        setupCamera()
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        Log.d("KIT", "ready to use ${System.currentTimeMillis()}")

    }

    fun setConnectionWarning(visible: Boolean) {
        if (visible) connectionWarningTV.visibility = View.VISIBLE
        else connectionWarningTV.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            R.id.check_in_history_menu -> {
                val intent = Intent(this, CheckinHistoryActivity::class.java)
                startActivity(intent)
            }

            R.id.manual_check_in_menu -> {
                val intent = Intent(this, ManualCheckinActivity::class.java)
                startActivity(intent)
            }
        }
        //reset recentCode
        recentCode = ""
        resetResultNoticeText()
        return super.onOptionsItemSelected(item)
    }


    private fun processAttendee(content: String) {
        val attendee = QRHelper.getAttendee(content)
        if (attendee != null) {
            showResult(true)
            FirebaseHelper.sendToFirebase(attendee)
            RealmHelper.save(attendee)
        } else {
            showResult(false)
        }
    }

    private fun showResult(valid: Boolean) {
        if (valid) {
            resultTV.text = getString(R.string.valid_attendee)
            resultTV.setTextColor(resources.getColor(R.color.kit_blue))
        } else {
            resultTV.text = getString(R.string.not_a_valid_attendee)
            resultTV.setTextColor(resources.getColor(R.color.kit_red))
        }
    }

    private fun resetResultNoticeText() {
        resultTV.text = getString(R.string.kit_welcome)
        resultTV.setTextColor(resources.getColor(R.color.black))
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    fun toggleFlashLight(v: View) {
        if (!flashState) {
            camera?.cameraControl?.enableTorch(true)
            flashState = true
            flashToggleButton.setImageResource(R.drawable.ic_flash_on)
        } else {
            camera?.cameraControl?.enableTorch(false)
            flashState = false
            flashToggleButton.setImageResource(R.drawable.ic_flash_off)
        }
    }

    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                try {
                    cameraProvider = cameraProviderFuture.get()


                    if (isCameraPermissionGranted()) {
                        bindCameraUseCases()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                arrayOf("android.permission.CAMERA"), PERMISSION_CAMERA_REQUEST
                            )
                        }
                    }


                } catch (e: ExecutionException) {
                    // Handle any errors (including cancellation) here.
                    Log.e("QrScanViewModel", "Unhandled exception", e)
                } catch (e: InterruptedException) {
                    Log.e("QrScanViewModel", "Unhandled exception", e)
                }
            }, ContextCompat.getMainExecutor(this)
        )
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }


    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider?.unbind(previewUseCase)
        }

        var builder = Preview.Builder().setTargetAspectRatio(screenAspectRatio)
        if (previewView.display != null) builder =
            builder.setTargetRotation(previewView.display.rotation)

        previewUseCase = builder.build()

        previewUseCase?.setSurfaceProvider(previewView.surfaceProvider)

        try {
            camera = cameraSelector?.let {
                cameraProvider?.bindToLifecycle(this, it, previewUseCase)
            }
        } catch (_: IllegalStateException) {
        } catch (_: IllegalArgumentException) {
        }
    }

    private fun bindAnalyseUseCase() {
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider?.unbind(analysisUseCase)
        }

        var builder = ImageAnalysis.Builder().setTargetAspectRatio(screenAspectRatio)

        if (previewView.display != null) builder =
            builder.setTargetRotation(previewView.display.rotation)

        analysisUseCase = builder.build()

        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        }

        try {
            camera = cameraSelector?.let {
                cameraProvider?.bindToLifecycle(/* lifecycleOwner = */this, it, analysisUseCase
                )
            }
        } catch (_: IllegalStateException) {
        } catch (_: IllegalArgumentException) {
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner, imageProxy: ImageProxy
    ) {
        if (imageProxy.image == null) return
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage).addOnSuccessListener { barcodes ->
            val barcode = barcodes.getOrNull(0)
            barcode?.rawValue?.let { code ->

                Log.d("KIT", "code: $code")
                Log.d("KIT", "recent: $recentCode")

                if (recentCode.isEmpty()) {
                    recentCode = code
                    processAttendee(code)
                } else {
                    if (recentCode != code) {
                        processAttendee(code)
                        Log.d("KIT", code)
                        recentCode = code
                    }
                }


            }
        }.addOnFailureListener {

        }.addOnCompleteListener {
            imageProxy.close()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                setupCamera()
            } else {
                Log.d("KIT", "Permission Request code wrong")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun isCameraPermissionGranted(): Boolean = this.let {
        ContextCompat.checkSelfPermission(it, "android.permission.CAMERA")
    } == PackageManager.PERMISSION_GRANTED


    private var doubleBackPress = false

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackPress) {
            super.onBackPressed()
            return
        }

        doubleBackPress = true
        Toast.makeText(this, "\nClick Back again to exit!\n", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackPress = false
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        actionReceiver = InternetConnectionChangeReceiver(this)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(actionReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(actionReceiver)
    }
}