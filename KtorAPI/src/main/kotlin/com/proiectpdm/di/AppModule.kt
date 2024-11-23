package com.proiectpdm.di

import com.proiectpdm.db.*
import org.koin.dsl.module

val appModule= module {
    single<DoctorService> {
        DoctorServiceImpl()
    }
    single<PacientService> {
        PacientServiceImpl()
    }
    single <AppointmentService> {
        AppointmentServiceImpl()
    }
}