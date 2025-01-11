package com.example.androidapp.models

data class Symptom(
    val id: Int,
    val name: String,
    val categoryId: Int,
    var isSelected: Boolean = false
)
