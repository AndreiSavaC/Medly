package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.androidapp.api.RetrofitClient
import com.example.androidapp.api.CategoryResponse
import com.example.androidapp.api.SymptomResponse
import com.example.androidapp.models.Category
import com.example.androidapp.models.Symptom
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SymptomsActivity : AppCompatActivity() {

    private lateinit var confirmButton: Button
    private lateinit var layout: LinearLayout
    private val categories = mutableListOf<Category>()
    private val symptoms = mutableListOf<Symptom>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptoms)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        layout = findViewById(R.id.categoriesLayout)
        confirmButton = findViewById(R.id.confirmButton)

        val selectedDate = intent.getStringExtra("selectedDate")
        val selectedHour = intent.getStringExtra("selectedHour")

        loadCategoriesAndSymptoms(selectedDate, selectedHour)
    }

    private fun loadCategoriesAndSymptoms(selectedDate: String?, selectedHour: String?) {
        RetrofitClient.symptomService.getCategories().enqueue(object : Callback<List<CategoryResponse>> {
            override fun onResponse(
                call: Call<List<CategoryResponse>>,
                response: Response<List<CategoryResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d("Categories", "Fetched categories successfully: ${response.body()}")
                    categories.clear()
                    categories.addAll(response.body()?.map {
                        Category(it.id, it.name)
                    } ?: emptyList())
                    loadSymptoms(selectedDate, selectedHour)
                } else {
                    Log.d("Categories", "Failed to fetch categories. Response code: ${response.code()}, message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<CategoryResponse>>, t: Throwable) {
                Log.e("Categories", "Error fetching categories: ${t.message}", t)
            }
        })
    }

    private fun loadSymptoms(selectedDate: String?, selectedHour: String?) {
        RetrofitClient.symptomService.getSymptoms().enqueue(object : Callback<List<SymptomResponse>> {
            override fun onResponse(
                call: Call<List<SymptomResponse>>,
                response: Response<List<SymptomResponse>>
            ) {
                if (response.isSuccessful) {
                    symptoms.clear()
                    symptoms.addAll(response.body()?.map {
                        Symptom(it.id, it.name, it.categoryId)
                    } ?: emptyList())
                    populateCategoriesAndSymptoms(selectedDate, selectedHour)
                } else {
                    showError("Failed to fetch symptoms")
                }
            }

            override fun onFailure(call: Call<List<SymptomResponse>>, t: Throwable) {
                showError("Error: ${t.message}")
            }
        })
    }

    private fun populateCategoriesAndSymptoms(selectedDate: String?, selectedHour: String?) {
        val inflater = LayoutInflater.from(this)
        val colorStateList = resources.getColorStateList(R.color.checkbox_color, theme)

        categories.forEach { category ->
            val categoryView = inflater.inflate(R.layout.item_category, layout, false)

            val categoryTitle = categoryView.findViewById<TextView>(R.id.categoryTitle)
            categoryTitle.text = category.name

            val symptomLayout = categoryView.findViewById<LinearLayout>(R.id.symptomLayout)
            symptoms.filter { it.categoryId == category.id }.forEach { symptom ->
                val symptomCheckBox = CheckBox(this)
                symptomCheckBox.text = symptom.name
                symptomCheckBox.buttonTintList = colorStateList
                symptomCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    symptom.isSelected = isChecked
                    updateConfirmButtonState()
                }
                symptomLayout.addView(symptomCheckBox)
            }

            layout.addView(categoryView)
        }

        confirmButton.setOnClickListener {

            val selectedSymptoms = symptoms.filter { it.isSelected }.map { it.name }
            val selectedSymptomsByCategory = categories.associate { category ->

                val selectedSymptomsInCategory = symptoms.filter { it.isSelected && it.categoryId == category.id }
                    .map { it.name }
                category.name to selectedSymptomsInCategory
            }.filter { it.value.isNotEmpty() }

            val categorySymptomsList = ArrayList<String>()
            for ((categoryName, symptomNames) in selectedSymptomsByCategory) {

                val joinedSymptoms = symptomNames.joinToString(",")
                categorySymptomsList.add("$categoryName:$joinedSymptoms")
            }

            val intent = Intent(this, ConfirmAppointmentActivity::class.java)

            intent.putExtra("selectedDate", selectedDate)
            intent.putExtra("selectedHour", selectedHour)
            intent.putStringArrayListExtra("selectedSymptoms", ArrayList(selectedSymptoms))
            intent.putStringArrayListExtra("categorySymptoms", categorySymptomsList)

            startActivity(intent)
        }
    }

    private fun updateConfirmButtonState() {
        val isAnySymptomSelected = symptoms.any { it.isSelected }
        confirmButton.isEnabled = isAnySymptomSelected
        confirmButton.backgroundTintList = resources.getColorStateList(
            if (isAnySymptomSelected) R.color.primaryColor else R.color.gray_200,
            theme
        )
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
