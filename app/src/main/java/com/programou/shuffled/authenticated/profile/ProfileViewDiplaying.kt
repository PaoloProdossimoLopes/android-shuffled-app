package com.programou.shuffled.authenticated.profile

interface ProfileViewDiplaying {
    fun displayLoader()
    fun hideLoader()
    fun displayCostumer(costumerViewData: CostumerViewData)
    fun displayAlert(alertViewData: ProfileAlertViewData)
}