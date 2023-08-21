package com.programou.shuffled

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    private val authProvider: FirebaseAuthClientProviderAdapter by lazy {
        FirebaseAuthClientProviderAdapter.shared
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val threeSeconds = 3000L
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, threeSeconds)
    }

//    private fun navigateBasedOnAuthentication() {
//        if (authProvider.isAuthenticated()) return navigateToAuthenticated()
//
//        navigateToUnauthenticated()
//    }
//
//    private fun navigateToAuthenticated() {
//        val intent = Intent(this, AuthenticatedActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    private fun navigateToUnauthenticated() {
//        val intent = Intent(this, UnauthenticatedActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
}