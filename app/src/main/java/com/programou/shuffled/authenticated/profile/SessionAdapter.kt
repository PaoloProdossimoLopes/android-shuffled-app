package com.programou.shuffled.authenticated.profile

import com.programou.shuffled.FirebaseAuthClientProviderAdapter

class SessionAdapter(private val auth: FirebaseAuthClientProviderAdapter = FirebaseAuthClientProviderAdapter.shared): Session {
    override fun getCostumer() = Costumer(
        auth.getUsername() ?: "",
        auth.getEmail() ?: "",
                auth.getUserPhotoURI().toString()
        )

    override fun logout() {
        auth.logout()
    }
}