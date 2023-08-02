package com.programou.shuffled.authenticated.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.programou.shuffled.BuildConfig
import com.programou.shuffled.FirebaseAuthClientProviderAdapter
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentProfileBinding
import com.programou.shuffled.unauthenticated.UnauthenticatedActivity

class ProfileFragment: Fragment(R.layout.fragment_profile), View.OnClickListener {

    private val auth = FirebaseAuthClientProviderAdapter.shared
    private lateinit var binding: FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)

        setupLayout()
        setupProfileImage()
        setupClickListener()
    }

    override fun onClick(view: View) {
        when (view) {
            binding.backArrowImageIndicatorInProfileFragment -> findNavController().popBackStack()
            binding.closeButtonInProfileFragment -> closeProfileScreen()
        }
    }

    private fun closeProfileScreen() {
        auth.logout()

        val intent = Intent(requireContext(), UnauthenticatedActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setupProfileImage() {
        val requestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.color.gray_100)

        Glide.with(binding.root.context)
            .load(auth.getUserPhotoURI())
            .apply(requestOptions)
            .into(binding.profileImageViewInProfileFragment)
    }

    private fun setupClickListener() {
        binding.backArrowImageIndicatorInProfileFragment.setOnClickListener(this)
        binding.closeButtonInProfileFragment.setOnClickListener(this)
    }

    private fun setupLayout() {
        binding.profileUsernameInProfileFragment.text = auth.getUsername()

        val appVersion = BuildConfig.VERSION_NAME
        val versionLiteral = getString(R.string.appVersionPrefixLiteralText) + appVersion
        binding.appVersionTextViewInProfileFragment.text = versionLiteral
    }
}