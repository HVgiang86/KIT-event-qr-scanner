package com.kitclub.kiteventqrscanner.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kitclub.kiteventqrscanner.R
import com.kitclub.kiteventqrscanner.activities.ManualCheckinActivity
import com.kitclub.kiteventqrscanner.model.Attendee
import com.kitclub.kiteventqrscanner.settings.QRParam

class ManualCheckinAdapter(var attendee: Attendee, var paramList: MutableList<QRParam>, var activity: ManualCheckinActivity) :
    RecyclerView.Adapter<ManualCheckinAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val propertyTV:TextView = itemView.findViewById(R.id.property_tv)
        val propertyEdt: EditText = itemView.findViewById(R.id.property_edt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.attendee_property_item,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        var s = paramList[position].name
        s += if (paramList[position].required)
            " *"
        else
            ""
        holder.propertyTV.text = s
        holder.propertyEdt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val s1 = s.toString().trim()
                if (s1.isNotEmpty()) {
                    if (paramList[position].required) {
                        activity.filledRequiredField[paramList[position].name] = true
                    }
                } else {
                    if (paramList[position].required) {
                        activity.filledRequiredField[paramList[position].name] = false
                    }
                }

                attendee.paramList[paramList[position].name] = s1
            }
        })

    }

    override fun getItemCount(): Int {
        return paramList.size
    }

}