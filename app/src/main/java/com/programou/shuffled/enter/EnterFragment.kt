package com.programou.shuffled.enter

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.programou.shuffled.R
import com.programou.shuffled.SplashScreen
import com.programou.shuffled.databinding.FragmentEnterBinding
import com.programou.shuffled.utils.hideKeyboard
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class EnterFragment : Fragment(R.layout.fragment_enter) {

    private lateinit var binding: FragmentEnterBinding
    private lateinit var signInRequest: GoogleSignInClient
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEnterBinding.bind(view)

        configureActions()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        signInRequest = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun configureActions() {
        with(binding) {
            buttonEnter.setOnClickListener { onEnterActionHandler(it) }
            cardGoogle.setOnClickListener {
                lifecycleScope.launch {
                    onGoogleActionHandler()
                }
            }
            buttonDontHaveAccount.setOnClickListener { onCreateAccountActionHandler() }
            binding.root.setOnClickListener { hideKeyboard() }
        }
    }

    private fun onEnterActionHandler(view: View) {
        hideKeyboard()
    }

    private fun onGoogleActionHandler() {
        val intent = signInRequest.signInIntent
        launcher.launch(intent)
    }

    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account = task.result
            if (account != null) {
                val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credentials)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(requireContext(), SplashScreen::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                task.exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        } else {
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onCreateAccountActionHandler() {
        val action = EnterFragmentDirections.actionEnterFragmentToRegisterFragment()
        findNavController().navigate(action)
    }
}
