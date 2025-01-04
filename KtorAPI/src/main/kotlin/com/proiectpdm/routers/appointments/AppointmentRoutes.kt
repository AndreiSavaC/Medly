package com.proiectpdm.routers.appointments

import com.proiectpdm.services.appointment.AppointmentService
import com.proiectpdm.models.Appointment
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun Routing.appointmentRoutes(
    appointmentService: AppointmentService,
) {
    route("/appointments") {
        get {
            val appointments = appointmentService.getAppointments()
            if (appointments.isNotEmpty()) call.respond(HttpStatusCode.OK, appointments)
            call.respond(HttpStatusCode.NotFound, "Appointments not found")
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val appointment = appointmentService.getAppointmentById(id)
            if (appointment != null) call.respond(HttpStatusCode.OK, appointment)
            call.respond(HttpStatusCode.NotFound, "Appointment not found")
        }

        get("/patient/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val appointment = appointmentService.getAppointmentsByPatientId(id)
            if (appointment.isNotEmpty()) call.respond(HttpStatusCode.OK, appointment)
            call.respond(HttpStatusCode.NotFound, "Appointment not found")
        }

        get("/doctor/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val date = call.request.queryParameters["date"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            if (date.isNullOrBlank()) {
                val appointments = appointmentService.getAppointmentsByDoctorId(id)
                if (appointments.isNotEmpty()) call.respond(HttpStatusCode.OK, appointments)
                call.respond(HttpStatusCode.NotFound, "Appointments not found")
            } else {
                val appointments = appointmentService.getAppointmentsByDoctorIdAndDate(id, date)
                if (appointments.isNotEmpty()) call.respond(HttpStatusCode.OK, appointments)
                call.respond(HttpStatusCode.NotFound, "Appointments not found")
            }
        }

        post {
            val appointment = call.receive<Appointment>()
            // Parse and validate the date
            val adjustedDate = try {
                val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                LocalDate.parse(appointment.date, dateFormatter)
            } catch (e: DateTimeParseException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid date format. Please use dd-MM-yyyy.")
                return@post
            }
            // Ensure the appointment is not on a weekend
            val dayOfWeek = adjustedDate.dayOfWeek
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                call.respond(HttpStatusCode.BadRequest, "Appointments cannot be created on Saturdays or Sundays.")
                return@post
            }
            // Validate time
            if (!isDateTimeValid(appointment.date, appointment.time)) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid time: Appointments must be between 08:00 and 14:00, starting on the hour or half-hour."
                )
                return@post
            }
            // Format date and create a copy of the appointment
            val updatedAppointment = appointment.copy(
                date = adjustedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            )
            // Check for conflicting appointments
            val existingAppointments = appointmentService.getAppointmentsByDoctorIdAndDate(
                updatedAppointment.doctorId, updatedAppointment.date
            )
            if (existingAppointments.any { it.time == updatedAppointment.time }) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Conflict: Appointment already exists at ${updatedAppointment.date} ${updatedAppointment.time} for doctor ${updatedAppointment.doctorId}."
                )
                return@post
            }
            // Add the appointment
            appointmentService.addAppointment(updatedAppointment)?.let {
                call.respond(HttpStatusCode.Created, it)
            } ?: call.respond(HttpStatusCode.BadRequest, "Error adding appointment.")
        }




        put("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                val updatedAppointment = call.receive<Appointment>().copy(id = id)
                appointmentService.updateAppointment(updatedAppointment)?.let {
                    call.respond(HttpStatusCode.OK, it)
                } ?: call.respond(HttpStatusCode.NotFound, "Error Updating appointment")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Id can not be null")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                if (appointmentService.deleteAppointment(id)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Appointment not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Id can not be null")
            }
        }

    }
}

fun isDateTimeValid(date: String, time: String): Boolean {
    val currentDate = LocalDateTime.now()


    if (!time.endsWith(":00") && !time.endsWith(":30")) {
        return false
    }



    try {

        val parsedDate = LocalDateTime.parse(
            "$date $time", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        )

        if (parsedDate.isBefore(currentDate)) {
            return false
        }

        if (parsedDate.hour < 9 || parsedDate.hour > 21) {
            return false
        }


    } catch (e: Exception) {
        println(e)
        return false
    }
    return true
}


//            val startTime = LocalTime.of(8, 0)
//            val endTime = LocalTime.of(14, 0)
//
//            val existingAppointments = appointmentService.getAppointmentByDate(dateParam).map {
//                LocalTime.parse(it.time, DateTimeFormatter.ofPattern("HH:mm"))
//            }
//
//            val allSlots = mutableListOf<LocalTime>()
//            var currentTime = startTime
//            while (currentTime.isBefore(endTime)) {
//                allSlots.add(currentTime)
//                currentTime = currentTime.plusMinutes(30)
//            }
//
//            val availableSlots = allSlots.filter { slot ->
//                existingAppointments.none { it == slot }
//            }
//
//            call.respond(HttpStatusCode.OK, availableSlots.map { it.format(DateTimeFormatter.ofPattern("HH:mm")) })