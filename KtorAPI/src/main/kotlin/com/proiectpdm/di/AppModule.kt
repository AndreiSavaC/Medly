package com.proiectpdm.di

import com.proiectpdm.db.DoctorService
import com.proiectpdm.db.DoctorServiceImpl
import com.proiectpdm.db.PacientService
import com.proiectpdm.db.PacientServiceImpl
import org.koin.dsl.module

val appModule= module {
    single<DoctorService> {
        DoctorServiceImpl()
    }
    single<PacientService> {
        PacientServiceImpl()
    }
}