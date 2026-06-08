package com.example.secureauthapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secureauthapp.data.model.Resource
import com.example.secureauthapp.data.model.UserInfo
import com.example.secureauthapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    private val _userInfo = MutableLiveData<Resource<UserInfo>>()
    val userInfo: LiveData<Resource<UserInfo>> = _userInfo
    
    fun fetchUserInfo(accessToken: String) {
        _userInfo.value = Resource.Loading()
        
        viewModelScope.launch {
            val result = userRepository.getUserInfo(accessToken)
            _userInfo.value = result
        }
    }
}
