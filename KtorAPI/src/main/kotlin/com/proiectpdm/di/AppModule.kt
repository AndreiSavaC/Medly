package com.proiectpdm.di

import com.proiectpdm.services.appointment.AppointmentService
import com.proiectpdm.services.appointment.AppointmentServiceImpl
import com.proiectpdm.services.diseaseCategories.DiseaseCategoriesService
import com.proiectpdm.services.diseaseCategories.DiseaseCategoriesServiceImpl
import com.proiectpdm.services.diseaseSymptoms.DiseaseSymptomsService
import com.proiectpdm.services.diseaseSymptoms.DiseaseSymptomsServiceImpl
import com.proiectpdm.services.insurance.InsuranceService
import com.proiectpdm.services.insurance.InsuranceServiceImpl
import com.proiectpdm.services.user.UserService
import com.proiectpdm.services.user.UserServiceImpl
import org.koin.dsl.module

val appModule = module {
    single<UserService> {
        UserServiceImpl()
    }

    single<AppointmentService> {
        AppointmentServiceImpl()
    }

    single<DiseaseCategoriesService> {
        DiseaseCategoriesServiceImpl()
    }

    single<DiseaseSymptomsService> {
        DiseaseSymptomsServiceImpl()
    }

    single<InsuranceService> {
        InsuranceServiceImpl()
    }
}