package com.example.usersapp.core.di.modules

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.example.usersapp.domain.usecase")
class UseCaseModule