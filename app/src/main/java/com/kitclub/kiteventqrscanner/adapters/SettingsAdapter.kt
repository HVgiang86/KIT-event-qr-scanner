package com.kitclub.kiteventqrscanner.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kitclub.kiteventqrscanner.R
import com.kitclub.kiteventqrscanner.activities.SettingsActivity
import com.kitclub.kiteventqrscanner.settings.QRParam

class SettingsAdapter(list1: MutableList<QRParam>, var settingsActivity: SettingsActivity) :
    RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {


    private var list: MutableList<QRParam> = ArrayList()

    init {
        for (param in list1) {
            list.add(QRParam(param.name,param.required))
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById(R.id.param_name_tv)
        val requiredCheckbox: CheckBox = itemView.findViewById(R.id.required_checkbox)
        val deleteBtn:ImageButton = itemView.findViewById(R.id.delete_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =
            settingsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.qr_param_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.nameTV.text = list[position].name
        holder.requiredCheckbox.isChecked = list[position].required

        holder.nameTV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val s1 = s.toString().trim()
                if (s1 != list[position].name) {
                    settingsActivity.settingsChanged = true
                    list[position].name = s1
                }

            }
        })

        holder.requiredCheckbox.setOnCheckedChangeListener { _, isChecked ->
            settingsActivity.settingsChanged = true
            list[position].required = isChecked
        }

        holder.deleteBtn.setOnClickListener {
            list.removeAt(position)
            notifyItemRemoved(position)
            settingsActivity.settingsChanged = true
        }
    }

    fun getAfterChangedList(): MutableList<QRParam> {
        return list
    }

    fun addParam() {
        val newParam = QRParam("new QR parameter",false)
        list.add(newParam)
        notifyItemInserted(list.size-1)
    }

}