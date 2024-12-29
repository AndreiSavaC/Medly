package com.proiectpdm.routes

import com.proiectpdm.db.AppointmentService
import com.proiectpdm.db.DoctorService
import com.proiectpdm.db.PacientService
import com.proiectpdm.model.Appointment
import com.proiectpdm.model.Appointments
import com.proiectpdm.model.Doctors
import com.proiectpdm.model.Pacients
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun Routing.appointmentRoute(appointmentService: AppointmentService,pacientService: PacientService,doctorService: DoctorService) {
    route("/appointment") {
        get{
            val appointments = appointmentService.getAllAppointments()
            call.respond(HttpStatusCode.OK,appointments)
        }

        post {
            val appointment = call.receive<Appointment>()
            val validPacient = pacientService.getPacientById(appointment.pacientId) != null
            val validDoctor = doctorService.getDoctorById(appointment.doctorId) != null

            val adjustedDate = try {
                val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                LocalDate.parse(appointment.date, dateFormatter)
            } catch (e: DateTimeParseException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid date format. Please use dd-MM-yyyy.")
                return@post
            }

            val isAppointmentDateValid = isDateTimeValid(appointment.date,appointment.time)
            if (!validPacient) {
                call.respond(HttpStatusCode.BadRequest, "Could not find pacient")
            } else if (!validDoctor) {
                call.respond(HttpStatusCode.BadRequest, "Could not find doctor")
            }
            else if(!isAppointmentDateValid){
                call.respond(HttpStatusCode.BadRequest, "Cannot create appointment in the past or incorect time\nEach slot should begin either from :00 or :30 for ${appointment.date} and ${appointment.time}")
            }
            else {
                val updatedAppointment = appointment.copy(date = adjustedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                val existingAppointments = appointmentService.getAppointmentsByDoctorIdAndDate(updatedAppointment.doctorId,updatedAppointment.date)
                println(existingAppointments)
                if (existingAppointments.isEmpty()) {
                    appointmentService.addAppointment(updatedAppointment)?.let {
                        call.respond(HttpStatusCode.Created, it)
                    } ?: call.respond(HttpStatusCode.BadRequest, "Error adding appointment")
                }else{
                    for (existingAppointment in existingAppointments) {
                        if (existingAppointment.time.equals(updatedAppointment.time, ignoreCase = true)) {
                            println("reached")
                            call.respond(HttpStatusCode.BadRequest,"Duplicate appointment at date:${updatedAppointment.date} and time:${updatedAppointment.time} for doctor:${updatedAppointment.doctorId}")
                        }else{
                            println("or reached")
                            appointmentService.addAppointment(updatedAppointment)?.let {
                                call.respond(HttpStatusCode.Created, it)
                            } ?: call.respond(HttpStatusCode.BadRequest, "Error adding appointment")
                        }
                    }
                }
            }
        }

        get("/{doctorId}/{date}"){
            val doctorId = call.parameters["doctorId"]?.toIntOrNull()
            val dateParam = call.parameters["date"]
            if (doctorId == null || dateParam == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid doctorId or date")
                return@get
            }

            val date = try {
                LocalDate.parse(dateParam, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            } catch (e: DateTimeParseException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid date format. Use dd-MM-yyyy.")
                return@get
            }

            val startTime = LocalTime.of(9, 0)
            val endTime = LocalTime.of(22, 0)

            val existingAppointments = appointmentService.getAppointmentByDate(dateParam).map {
                    LocalTime.parse(it.time, DateTimeFormatter.ofPattern("HH:mm"))
                }

            val allSlots = mutableListOf<LocalTime>()
            var currentTime = startTime
            while (currentTime.isBefore(endTime)) {
                allSlots.add(currentTime)
                currentTime = currentTime.plusMinutes(30)
            }

            val availableSlots = allSlots.filter { slot ->
                existingAppointments.none { it == slot }
            }

            call.respond(HttpStatusCode.OK, availableSlots.map { it.format(DateTimeFormatter.ofPattern("HH:mm")) })
        }

        get("/{id}"){
            val id = call.parameters["id"]?.toInt()
            if (id != null){
                appointmentService.getAppointmentById(id)?.let {
                    call.respond(HttpStatusCode.OK, it)
                }?: call.respond(HttpStatusCode.NotFound, "Appointment not found")
            }else{
                call.respond(HttpStatusCode.BadRequest, "Id can not be null")
            }
        }

        put("/{id}"){
            val id = call.parameters["id"]?.toInt()
            if (id != null){
                val updatedAppointment = call.receive<Appointment>().copy(id=id)
                appointmentService.updateAppointment(updatedAppointment)?.let {
                    call.respond(HttpStatusCode.OK,it)
                }?: call.respond(HttpStatusCode.NotFound, "Error Updating appointment")
            }else{
                call.respond(HttpStatusCode.BadRequest, "Id can not be null")
            }
        }

        delete("/{id}"){
            val id = call.parameters["id"]?.toInt()
            if (id != null){
                if(appointmentService.deleteAppointment(id)){
                    call.respond(HttpStatusCode.NoContent)
                }else{
                    call.respond(HttpStatusCode.NotFound,"Appointment not found")
                }
            }else{
                call.respond(HttpStatusCode.BadRequest,"Id can not be null")
            }
        }

    }
}

fun isDateTimeValid(date:String,time:String): Boolean {
    val currentDate = LocalDateTime.now()

    if(time.endsWith(":00") || time.endsWith(":30")) {

    }else{
        return false
    }

    try {

        val parsedDate = LocalDateTime.parse("$date $time",
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

        if (parsedDate.isBefore(currentDate)) {
            return false
        }

    }catch (e:Exception){
        println(e)
        return false
    }
    return true
}
