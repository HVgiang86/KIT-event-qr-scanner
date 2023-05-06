package com.kitclub.kiteventqrscanner.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kitclub.kiteventqrscanner.R
import com.kitclub.kiteventqrscanner.model.models.settings.QRParam

class AttendeePropertyAdapter(
    private var list: HashMap<String, String>,
    var paramList: MutableList<QRParam>,
    var context: Context
) : RecyclerView.Adapter<AttendeePropertyAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById(R.id.name_tv)
        val valueTV: TextView = itemView.findViewById(R.id.value_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.attendee_history_item,
                parent,
                false
            )
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return paramList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTV.text = paramList[position].name
        holder.nameTV.setOnClickListener {
            Log.d("KIT", "clicked")
        }
        holder.valueTV.text = list[paramList[position].name]
    }
}