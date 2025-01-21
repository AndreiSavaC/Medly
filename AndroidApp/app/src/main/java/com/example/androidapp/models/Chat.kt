package com.example.androidapp.models

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val message: String?,
    val error: String?
)