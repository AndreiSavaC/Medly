package com.example.androidapp.models

data class ReportRequest(
    val symptoms: List<String>
)
data class ReportResponse(
    val report: String?,
    val error: String?
)