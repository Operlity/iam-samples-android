package com.example.secureauthapp

import android.app.Application

class AuthApplication : Application() {
    
    lateinit var authManager: AuthManager
        private set

    override fun onCreate() {
        super.onCreate()
        authManager = AuthManager(this)
    }
}
