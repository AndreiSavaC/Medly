package com.example.androidapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidapp.models.UserInfo
import java.time.LocalDate
import java.util.Date
import java.util.Random

class UserInfoView : AppCompatActivity() {

    var infos:UserInfo? =  null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AurasPaltanea 22/10
        //Begin
        infos = LoadUserInfo();
        //End
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_user_info_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var editInfoBtn = findViewById<TextView>(R.id.editInfoButton)
        editInfoBtn.setOnClickListener{
            infos = LoadUserInfo();
            ShowUserInfo()
        }
        ShowUserInfo()
    }

    private fun ShowUserInfo() {
        findViewById<TextView>(R.id.fNameLabel).setText(infos!!.firstName)
        findViewById<TextView>(R.id.lNameLabel).setText(infos!!.lastName)
        findViewById<TextView>(R.id.genderLabel).setText(infos!!.gender)
        findViewById<TextView>(R.id.heightLabel).setText(infos!!.height.toString())
        findViewById<TextView>(R.id.weightLabel).setText(infos!!.weight.toString())
        findViewById<TextView>(R.id.ageLabel).setText(infos!!.GetAge().toString())

        findViewById<TextView>(R.id.fullnameLabel).setText(infos!!.firstName + " " + infos!!.lastName)
        findViewById<TextView>(R.id.emailLabel).setText(infos!!.email)
    }

    private fun LoadUserInfo():UserInfo{
        var info = UserInfo();
        info.bDay = LocalDate.of((1990 until 2014).random(),(1 until 13).random(),(1 until 28).random())
        info.gender = "Fluid"
        info.height = 1.84f;
        info.weight = 98f;
        info.email = "hatz.dorian.john@gmail.com";
        info.firstName = "Dorian";
        info.lastName = "Popa";
        return info;
    }
}