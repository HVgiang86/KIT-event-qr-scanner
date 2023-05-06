package com.kitclub.kiteventqrscanner.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kitclub.kiteventqrscanner.R
import com.kitclub.kiteventqrscanner.model.models.attendee.Attendee
import com.kitclub.kiteventqrscanner.model.models.settings.QRParam

class CheckinHistoryAdapter(
    private var attendeeList: MutableList<Attendee>,
    var paramList: MutableList<QRParam>,
    var context: Context
) : RecyclerView.Adapter<CheckinHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTV: TextView = itemView.findViewById(R.id.history_id_tv)
        val recycler: RecyclerView = itemView.findViewById(R.id.recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.checkin_history_item,
                parent,
                false
            )
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return attendeeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.idTV.text = "ID: " + attendeeList[position].id
        val adapter = AttendeePropertyAdapter(attendeeList[position].paramList, paramList, context)
        holder.recycler.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}