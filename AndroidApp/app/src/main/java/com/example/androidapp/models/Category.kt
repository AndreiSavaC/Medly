package com.example.androidapp.models

data class Category(
    val id: Int,
    val name: String,
    val symptoms: List<Symptom> = emptyList()
)
