package com.example.androidapp.models

data class Symptom(val name: String, var isSelected: Boolean = false)

data class Category(val title: String, val symptoms: List<Symptom>)