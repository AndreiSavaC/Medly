package com.example.androidapp

import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var scrollViewReport: ScrollView
    private lateinit var textViewReport: TextView
    private lateinit var buttonDownloadPdf: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        progressBar = findViewById(R.id.progressBar)
        scrollViewReport = findViewById(R.id.scrollViewReport)
        textViewReport = findViewById(R.id.textViewReport)
        buttonDownloadPdf = findViewById(R.id.btnDownloadPdf)

        progressBar.visibility = View.VISIBLE
        scrollViewReport.visibility = View.GONE
        buttonDownloadPdf.visibility = View.GONE

        val symptoms = intent.getStringArrayListExtra("symptoms") ?: emptyList<String>()
        val patientName =
            intent.getStringExtra("patientName")?.replace("\\s".toRegex(), "-")?.lowercase()

        if (symptoms.isNotEmpty()) {
            generateDoctorReport(symptoms)
        } else {
            Toast.makeText(this, "Nu există simptome de trimis.", Toast.LENGTH_SHORT).show()
            finish()
        }

        buttonDownloadPdf.setOnClickListener {
            downloadReportAsPdf(patientName)
        }
    }

    private fun generateDoctorReport(symptoms: List<String>) {
        val request = ReportRequest(symptoms = symptoms)

        RetrofitClient.reportService.generateDoctorReport(request)
            .enqueue(object : Callback<ReportResponse> {
                override fun onResponse(
                    call: Call<ReportResponse>, response: Response<ReportResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val reportResponse = response.body()
                        if (reportResponse != null) {
                            if (reportResponse.report != null) {
                                textViewReport.text =
                                    Html.fromHtml(formatReport(reportResponse.report))
                                scrollViewReport.visibility = View.VISIBLE
                                buttonDownloadPdf.visibility = View.VISIBLE
                            } else {
                                Toast.makeText(
                                    this@ReportActivity,
                                    "Eroare la generarea raportului: ${reportResponse.error}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@ReportActivity, "Raportul este gol.", Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ReportActivity,
                            "Eroare la generarea raportului: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ReportActivity, "Eroare de rețea: ${t.message}", Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun formatReport(report: String): String {
        return report.replace("\n", "<br>").replace("**", "<b>").replace("**", "</b>")
            .replace("1. ", "<br><b>1. </b>").replace("2. ", "<br><b>2. </b>")
            .replace("3. ", "<br><b>3. </b>").replace("4. ", "<br><b>4. </b>")
            .replace("- ", "<br>- ")
    }

    private fun downloadReportAsPdf(patientName: String?) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)

        val content = textViewReport.text.toString()
        val canvas = page.canvas
        val paint = android.graphics.Paint()
        val x = 10
        var y = 25

        for (line in content.split("\n")) {
            canvas.drawText(line, x.toFloat(), y.toFloat(), paint)
            y += (paint.descent() - paint.ascent()).toInt()
        }

        document.finishPage(page)

        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDateTime = dateFormat.format(Date())
        val directoryPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString()
        val file = File(directoryPath, "${patientName}_$currentDateTime.pdf")

        try {
            document.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF downloaded to $directoryPath", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e("ReportActivity", "Error writing PDF", e)
            Toast.makeText(this, "Error writing PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }

        document.close()
    }
}