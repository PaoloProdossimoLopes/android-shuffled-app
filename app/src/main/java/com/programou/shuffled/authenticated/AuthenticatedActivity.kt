package com.programou.shuffled.authenticated

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.programou.shuffled.FirebaseAuthClientProviderAdapter
import com.programou.shuffled.R
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class AuthenticatedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticated)
//        FirebaseAuthClientProviderAdapter.shared.logout()
    }

    override fun onBackPressed() {
        finish()
    }
}
