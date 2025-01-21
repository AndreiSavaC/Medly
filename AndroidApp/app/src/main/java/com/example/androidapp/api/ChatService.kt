package com.example.androidapp.api

import com.example.androidapp.models.ChatRequest
import com.example.androidapp.models.ChatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatService {
    @POST("/chat")
    fun sendChatMessage(@Body request: ChatRequest): Call<ChatResponse>
}
