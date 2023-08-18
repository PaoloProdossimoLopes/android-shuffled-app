package com.programou.shuffled.authenticated.profile

import android.net.Uri
import androidx.core.net.toUri

data class CostumerViewData(val name: String, val email: String, val profileURL: String) {
    constructor(costumer: Costumer): this(costumer.name, costumer.email, costumer.profileURL)
}