package com.programou.shuffled.unauthenticated

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.programou.shuffled.FirebaseAuthClientProviderAdapter
import com.programou.shuffled.R

class UnauthenticatedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unauthenticated)
    }
}