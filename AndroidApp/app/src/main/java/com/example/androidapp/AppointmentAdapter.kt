package com.example.androidapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppointmentAdapter(private val appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }

    override fun getItemCount(): Int = appointments.size

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val patientNameTextView: TextView = itemView.findViewById(R.id.textViewPatientName)
        private val timeTextView: TextView = itemView.findViewById(R.id.textViewTime)
        private val symptomsTextView: TextView = itemView.findViewById(R.id.textViewSymptoms)

        fun bind(appointment: Appointment) {
            patientNameTextView.text = appointment.patientName
            timeTextView.text = appointment.time
            symptomsTextView.text = appointment.symptoms
        }
    }
}

data class Appointment(
    val patientName: String,
    val time: String,
    val symptoms: String
)
