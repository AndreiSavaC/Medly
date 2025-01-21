package com.example.androidapp.models

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val reply: String?,
    val error: String?
)