package com.programou.shuffled

import android.util.Log
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.programou.shuffled.enter.Credential
import com.programou.shuffled.enter.CredentialProvider
import com.programou.shuffled.enter.EnterClientProvider
import com.programou.shuffled.enter.EnterRequest
import com.programou.shuffled.enter.User
import com.programou.shuffled.enter.UserResponse
import com.programou.shuffled.register.RegisterClientProvider
import com.programou.shuffled.register.RegisterRequest

class FirebaseAuthClientProviderAdapter : EnterClientProvider, CredentialProvider,
    RegisterClientProvider {

    companion object {
        val shared: FirebaseAuthClientProviderAdapter by lazy {
            FirebaseAuthClientProviderAdapter()
        }
    }

    private val auth = Firebase.auth

    fun logout() {
        auth.signOut()
    }

    override fun enter(request: EnterRequest, callback: (UserResponse) -> Unit) {
        val email = request.body["email"] ?: String()
        val password = request.body["password"] ?: String()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userName = result.user?.displayName ?: String()
                val userEmail = result.user?.email ?: String()

                callback(UserResponse(200, UserResponse.User(userName, userEmail)))
            }
            .addOnFailureListener {
                callback(UserResponse(500, null))
            }
    }

    fun auth(credential: Credential, callback: (Result<User>) -> Unit) {
        val credentials = GoogleAuthProvider.getCredential(credential.id, null)
        auth.signInWithCredential(credentials)
            .addOnSuccessListener { authResult ->
                val name = authResult.user?.displayName ?: "No Name"
                val email = authResult.user?.email ?: "No Email"
                callback(Result.success(User(name, email)))
            }
            .addOnFailureListener { e ->
                callback(Result.failure(Error(e.message)))
            }
    }

    override fun register(request: RegisterRequest, callback: (UserResponse) -> Unit) {
        val email = request.body["email"] ?: String()
        val password = request.body["password"] ?: String()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userName = result.user?.displayName ?: String()
                val userEmail = result.user?.email ?: String()

                callback(UserResponse(200, UserResponse.User(userName, userEmail)))
            }
            .addOnFailureListener {
                callback(UserResponse(500, null))
            }
    }
}