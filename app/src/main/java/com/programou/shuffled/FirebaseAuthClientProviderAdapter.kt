package com.programou.shuffled

import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.programou.shuffled.unauthenticated.enter.EnterClientProvider
import com.programou.shuffled.unauthenticated.enter.EnterRequest
import com.programou.shuffled.unauthenticated.enter.User
import com.programou.shuffled.unauthenticated.enter.UserResponse
import com.programou.shuffled.unauthenticated.register.RegisterClientProvider
import com.programou.shuffled.unauthenticated.register.RegisterRequest

class FirebaseAuthClientProviderAdapter : EnterClientProvider,
    com.programou.shuffled.unauthenticated.enter.CredentialProvider,
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

    fun isAuthenticated() = auth.currentUser != null

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

    fun auth(credential: com.programou.shuffled.unauthenticated.enter.Credential, callback: (Result<User>) -> Unit) {
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