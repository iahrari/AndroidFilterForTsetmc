package com.example.tsetmc.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tsetmc.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }
}