package com.example.androidapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppointmentAdapter(
    private val appointments: List<Appointment>,
    private val onItemClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewPatientName: TextView = itemView.findViewById(R.id.textViewPatientName)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)

        fun bind(appointment: Appointment) {
            textViewPatientName.text = appointment.patientName
            textViewDate.text = appointment.date
            textViewTime.text = appointment.time
            itemView.setOnClickListener { onItemClick(appointment) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount() = appointments.size
}


data class Appointment(
    val patientName: String,
    val date: String,
    val time: String,
    val symptoms: String
)
