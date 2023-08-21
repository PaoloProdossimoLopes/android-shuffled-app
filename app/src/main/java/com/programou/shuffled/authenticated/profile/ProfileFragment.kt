package com.programou.shuffled.authenticated.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.programou.shuffled.BuildConfig
import com.programou.shuffled.R
import com.programou.shuffled.databinding.FragmentProfileBinding
import com.programou.shuffled.unauthenticated.UnauthenticatedActivity

class ProfileFragment: Fragment(R.layout.fragment_profile), View.OnClickListener, ProfileViewDiplaying {
    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by lazy {
        val sessionAdapter = SessionAdapter()
        val costumer = LoadCostumer(sessionAdapter)
        ProfileViewModel(this, costumer)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)

        setupLayout()
        setupClickListener()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onClick(view: View) {
        when (view) {
            binding.backArrowImageIndicatorInProfileFragment -> findNavController().popBackStack()
            binding.closeButtonInProfileFragment -> closeProfileScreen()
        }
    }

    private fun closeProfileScreen() {
        viewModel.logout()

        val intent = Intent(requireContext(), UnauthenticatedActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setupClickListener() {
        binding.backArrowImageIndicatorInProfileFragment.setOnClickListener(this)
        binding.closeButtonInProfileFragment.setOnClickListener(this)
    }

    private fun setupLayout() {
        val appVersion = BuildConfig.VERSION_NAME
        val versionLiteral = getString(R.string.appVersionPrefixLiteralText) + appVersion
        binding.appVersionTextViewInProfileFragment.text = versionLiteral
    }

    override fun displayLoader() = lifecycleScope.run {

    }

    override fun hideLoader() = lifecycleScope.run {

    }

    override fun displayCostumer(costumerViewData: CostumerViewData) = lifecycleScope.run {
        val requestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.color.gray_100)

        Glide.with(binding.root.context)
            .load(costumerViewData.profileURL)
            .apply(requestOptions)
            .into(binding.profileImageViewInProfileFragment)

        binding.profileUsernameInProfileFragment.text = costumerViewData.name
    }

    override fun displayAlert(alertViewData: ProfileAlertViewData) {
        lifecycleScope.run {
            AlertDialog.Builder(requireContext())
                .setTitle(alertViewData.tile)
                .setMessage(alertViewData.description)
                .setNeutralButton("ok", null)
                .show()
        }
    }
}