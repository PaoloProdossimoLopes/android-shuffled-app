package com.programou.shuffled.authenticated.profile

data class CostumerViewData(val name: String, val email: String, val profileURL: String) {
    constructor(costumer: Costumer): this(costumer.name, costumer.email, costumer.profileURL)
}