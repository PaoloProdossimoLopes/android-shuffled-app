package com.programou.shuffled

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class GoogleAuthenticatorProvider(private val fragment: Fragment) {

    private val launcher = configureLauncher(fragment)
    private var onCredentialsResult: ((Result<com.programou.shuffled.unauthenticated.enter.Credential>) -> Unit)? = null
    private var signInRequest: GoogleSignInClient = run {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(fragment.requireActivity().getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(fragment.requireActivity(), gso)
    }

    fun presentAuthentication(callback: (Result<com.programou.shuffled.unauthenticated.enter.Credential>) -> Unit) {
        onCredentialsResult = callback

        val intent = signInRequest.signInIntent
        launcher.launch(intent)
    }

    fun logout() {
        signInRequest.signOut()
    }

    private fun configureLauncher(fragment: Fragment): ActivityResultLauncher<Intent> {
        return fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account = task.result
                    val id = account.idToken
                    if (id != null) {
                        val credential = com.programou.shuffled.unauthenticated.enter.Credential(id)
                        onCredentialsResult?.let { it(Result.success(credential)) }
                    } else {
                        onCredentialsResult?.let { it(Result.failure(Error())) }
                    }
                } else {
                    onCredentialsResult?.let { it(Result.failure(Error())) }
                }
            } else {
                onCredentialsResult?.let { it(Result.failure(Error())) }
            }
        }
    }
}