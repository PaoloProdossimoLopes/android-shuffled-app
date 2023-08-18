package com.programou.shuffled.authenticated.profile

import androidx.lifecycle.ViewModel

class ProfileViewModel(private val view: ProfileViewDiplaying, private val costumerLoader: CostumerLoader): ViewModel() {
    fun onResume() {
        view.displayLoader()
        try {
            costumerLoader.loadCostumer { costumer ->
                view.displayCostumer(CostumerViewData(costumer))
                view.hideLoader()
            }
        } catch (e: InvalidCostumerInformationError) {
            val alertViewData = ProfileAlertViewData("Ops, something wrong occurs", "try again later")
            view.displayAlert(alertViewData)
        }
    }

    fun logout() {
        costumerLoader.logout()
    }
}