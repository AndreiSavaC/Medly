package com.example.androidapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.api.RetrofitClient
import com.example.androidapp.models.ReportRequest
import com.example.androidapp.models.ReportResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var scrollViewReport: ScrollView
    private lateinit var textViewReport: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        progressBar = findViewById(R.id.progressBar)
        scrollViewReport = findViewById(R.id.scrollViewReport)
        textViewReport = findViewById(R.id.textViewReport)

        // Arată ProgressBar și ascunde ScrollView-ul
        progressBar.visibility = View.VISIBLE
        scrollViewReport.visibility = View.GONE

        // Obține simptomele din Intent
        val symptoms = intent.getStringArrayListExtra("symptoms") ?: emptyList<String>()

        if (symptoms.isNotEmpty()) {
            generateDoctorReport(symptoms)
        } else {
            Toast.makeText(this, "Nu există simptome de trimis.", Toast.LENGTH_SHORT).show()
            finish() // Închide activitatea dacă nu sunt simptome
        }
    }

    private fun generateDoctorReport(symptoms: List<String>) {
        val request = ReportRequest(symptoms = symptoms)

        RetrofitClient.reportService.generateDoctorReport(request)
            .enqueue(object : Callback<ReportResponse> {
                override fun onResponse(
                    call: Call<ReportResponse>,
                    response: Response<ReportResponse>
                ) {
                    // Ascunde ProgressBar
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val reportResponse = response.body()
                        if (reportResponse != null) {
                            if (reportResponse.report != null) {
                                textViewReport.text = reportResponse.report
                                Log.d("ReportActivityLOG", "Raport generat: ${reportResponse.report}")
                                // Arată ScrollView-ul care conține TextView-ul
                                scrollViewReport.visibility = View.VISIBLE
                                Log.d("ReportActivityLOG", "ScrollView-ul este acum vizibil.")
                            } else {
                                Toast.makeText(
                                    this@ReportActivity,
                                    "Eroare la generarea raportului: ${reportResponse.error}",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.e("ReportActivityLOG", "Eroare la generarea raportului: ${reportResponse.error}")
                            }
                        } else {
                            Toast.makeText(
                                this@ReportActivity,
                                "Raportul este gol.",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("ReportActivityLOG", "Raportul este gol.")
                        }
                    } else {
                        Toast.makeText(
                            this@ReportActivity,
                            "Eroare la generarea raportului: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("ReportActivityLOG", "Eroare la generarea raportului: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                    // Ascunde ProgressBar
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ReportActivity,
                        "Eroare de rețea: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("ReportActivityLOG", "Eroare de rețea: ${t.message}")
                }
            })
    }
}
