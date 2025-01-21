package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.androidapp.api.RetrofitClient
import com.example.androidapp.models.Insurance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAccountActivity : AppCompatActivity() {

    @Deprecated("This is a temporary solution.")
    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun validateInsuranceCode(insuranceCode: Double, callback: (Insurance?) -> Unit) {
        val call = RetrofitClient.insuranceService.getInsuranceByCode(insuranceCode)

        call.enqueue(object : Callback<Insurance> {

            override fun onResponse(call: Call<Insurance>, response: Response<Insurance>) {
                if (response.isSuccessful) {
                    val insurance = response.body()
                    callback(insurance)
                } else {
                    callback(null)
                    Toast.makeText(
                        this@CreateAccountActivity,
                        "Codul nu este valid sau serverul a returnat o eroare.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Insurance>, t: Throwable) {
                Toast.makeText(
                    this@CreateAccountActivity, "Eroare de rețea: ${t.message}", Toast.LENGTH_LONG
                ).show()
                callback(null)
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.request_account_view)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        WindowInsetsControllerCompat(window, findViewById(R.id.main)).isAppearanceLightStatusBars =
            true

        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val insuranceCodeEntry = findViewById<EditText>(R.id.insuranceCodeInput)
        val createAccountButton = findViewById<Button>(R.id.nextButton)
        val noInsuranceCodeButton = findViewById<Button>(R.id.noInsuranceCodeButton)

        createAccountButton.setOnClickListener {
            val insuranceCodeString = insuranceCodeEntry.text.toString()

            if (insuranceCodeString.isNotEmpty() && (insuranceCodeString.length == 3 || insuranceCodeString.length == 20)) {
                val insuranceCode = insuranceCodeString.toDoubleOrNull()
                if (insuranceCode != null) {
                    validateInsuranceCode(insuranceCode) { insurance ->
                        if (insurance != null) {

                            runOnUiThread {
                                titleTextView.visibility = View.GONE
                                insuranceCodeEntry.visibility = View.GONE
                                createAccountButton.visibility = View.GONE
                                noInsuranceCodeButton.visibility = View.GONE

                                val fragment = VerifyInfoFragment.newInstance(insurance)
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.framgment_container, fragment).commit()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this, "Codul de asigurat nu este valid.", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this, "Codul de asigurat nu este un număr valid.", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Codul de asigurat nu a fost introdus sau nu este corect.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        noInsuranceCodeButton.setOnClickListener {
            Toast.makeText(
                this,
                "Ne pare rau, momentan utilizatorii care nu sunt asigurati nu isi pot crea cont.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
