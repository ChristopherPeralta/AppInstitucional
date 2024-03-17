package com.example.appinstitucional.ui

import DatabaseHelper
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.appinstitucional.R
import com.example.appinstitucional.ui.Login.adaptador.IntroductionPagerAdapter

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = IntroductionPagerAdapter(this)

    }
}