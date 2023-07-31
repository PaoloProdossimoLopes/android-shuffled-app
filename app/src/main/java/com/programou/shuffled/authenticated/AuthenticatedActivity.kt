package com.programou.shuffled.authenticated

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.programou.shuffled.FirebaseAuthClientProviderAdapter
import com.programou.shuffled.R
import com.programou.shuffled.unauthenticated.UnauthenticatedActivity
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class AuthenticatedActivity : AppCompatActivity() {
    private val auth = FirebaseAuthClientProviderAdapter.shared

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticated)
    }

    override fun onBackPressed() {
        finish()
    }
}
