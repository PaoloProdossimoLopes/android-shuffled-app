package com.programou.shuffled.enter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class EnterViewModel(private val useCase: EnterAccount) {
    
    private val errorMessageMutableData = MutableLiveData<ErrorViewData>()
    val errorMessage: LiveData<ErrorViewData> = errorMessageMutableData

    private val userMutableData = MutableLiveData<User>()
    val user: LiveData<User> = userMutableData

    fun enter(viewData: UserViewData) {
        val enter = Enter(viewData.email, viewData.password)
        val result = useCase.enter(enter)

        result.exceptionOrNull()?.let { receivedError ->
            val errorViewData = ErrorViewData(receivedError.message!!)
            return errorMessageMutableData.postValue(errorViewData)
        }

        result.getOrNull()?.let { user ->
            userMutableData.postValue(user)
        }
    }
}