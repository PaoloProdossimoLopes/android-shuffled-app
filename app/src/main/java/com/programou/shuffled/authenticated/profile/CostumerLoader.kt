package com.programou.shuffled.authenticated.profile

interface CostumerLoader {
    fun loadCostumer(callback: (Costumer) -> Unit)
    fun logout()
}