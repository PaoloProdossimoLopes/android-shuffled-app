package com.programou.shuffled.unauthenticated.enter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class EnterViewModel(private val useCase: com.programou.shuffled.unauthenticated.enter.EnterAccount) {

    private val errorMessageMutableData = MutableLiveData<ErrorViewData>()
    val errorMessage: LiveData<ErrorViewData> = errorMessageMutableData

    private val userMutableData = MutableLiveData<User>()
    val user: LiveData<User> = userMutableData

    fun enter(viewData: UserViewData) {
        val enter =
            com.programou.shuffled.unauthenticated.enter.Enter(viewData.email, viewData.password)
        useCase.enter(enter) { result ->

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