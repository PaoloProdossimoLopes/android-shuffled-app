package com.programou.shuffled.authenticated.profile

class Costumer(val name: String, val email: String, val profileURL: String) {
    fun isValid(): Boolean {
        val nameIsValid = name.isNotEmpty()
        val emailIsValid = email.isNotEmpty()
        return nameIsValid && emailIsValid
    }
}