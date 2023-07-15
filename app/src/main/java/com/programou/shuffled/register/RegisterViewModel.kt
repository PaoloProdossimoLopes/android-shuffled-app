package com.programou.shuffled.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.programou.shuffled.enter.ErrorViewData
import com.programou.shuffled.enter.User
import com.programou.shuffled.enter.UserViewData

class RegisterViewModel(private val useCase: RegisterAccount) {

    private val errorMessageMutableData = MutableLiveData<ErrorViewData>()
    val errorMessage: LiveData<ErrorViewData> = errorMessageMutableData

    private val userMutableData = MutableLiveData<User>()
    val user: LiveData<User> = userMutableData

    fun register(viewData: UserViewData) {
        val register = Register(viewData.email, viewData.password)
        useCase.register(register) { result ->

            result.exceptionOrNull()?.let { receivedError ->
                val errorViewData = ErrorViewData(receivedError.message!!)
                errorMessageMutableData.postValue(errorViewData)
            }

            result.getOrNull()?.let { user ->
                userMutableData.postValue(user)
            }
        }
    }
}