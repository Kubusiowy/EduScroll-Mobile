package com.example.eduscroll.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel(){


    private val _navigatetoLogin = MutableStateFlow(false)
    val navigateToLogin = _navigatetoLogin

    init{
        viewModelScope.launch {
            delay(2500)
            _navigatetoLogin.value = true
        }
    }
}