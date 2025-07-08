package com.example.usersapp

import android.app.Application
import com.example.usersapp.core.di.initApplication
import org.koin.android.ext.koin.androidContext

class AppUser : Application() {
    override fun onCreate() {
        super.onCreate()
        initApplication {
            androidContext(this@AppUser)
        }
    }
}