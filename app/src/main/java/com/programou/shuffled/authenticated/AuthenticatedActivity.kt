package com.programou.shuffled.authenticated

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.programou.shuffled.FirebaseAuthClientProviderAdapter
import com.programou.shuffled.R

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
