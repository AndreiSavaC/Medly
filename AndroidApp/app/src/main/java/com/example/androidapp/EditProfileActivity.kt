package com.example.androidapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.api.RetrofitClient
import com.example.androidapp.models.UserRequest
import com.example.androidapp.models.UserResponse
import com.example.androidapp.models.UserUpdateRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextGender: EditText
    private lateinit var editTextBirthday: EditText
    private lateinit var editTextHeight: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize views
        editTextFirstName = findViewById(R.id.editTextName)
        editTextLastName = findViewById(R.id.editTextSurname)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextGender = findViewById(R.id.editTextGender)
        editTextBirthday = findViewById(R.id.editTextBirthDate)
        editTextHeight = findViewById(R.id.editTextHeight)
        editTextWeight = findViewById(R.id.editTextWeight)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        val sharedPreferences = getSharedPreferences("authPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("PATIENT_ID", -1)

        loadUserData()

        btnSave.setOnClickListener { saveUserChanges() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun loadUserData() {
        RetrofitClient.userService.getUserById(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        editTextFirstName.setText(user.firstName)
                        editTextLastName.setText(user.lastName)
                        editTextEmail.setText(user.email)
                        editTextGender.setText(user.gender)
                        editTextBirthday.setText(user.birthday)
                        editTextHeight.setText(user.height.toString())
                        editTextWeight.setText(user.weight.toString())
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "Failed to load user data.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@EditProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserChanges() {
        val updatedUser = UserUpdateRequest(
            id = userId,
            firstName = editTextFirstName.text.toString(),
            lastName = editTextLastName.text.toString(),
            email = editTextEmail.text.toString(),
            gender = editTextGender.text.toString(),
            birthday = editTextBirthday.text.toString(),
            height = editTextHeight.text.toString().toFloatOrNull() ?: 0f,
            weight = editTextWeight.text.toString().toFloatOrNull() ?: 0f,
            doctorId = null,
            isDoctor = false,
            isAdmin = false
        )

        RetrofitClient.userService.updateUser(userId, updatedUser)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditProfileActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Failed to update profile."+response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@EditProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}