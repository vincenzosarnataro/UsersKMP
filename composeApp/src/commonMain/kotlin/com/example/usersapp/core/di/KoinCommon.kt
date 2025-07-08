package com.example.usersapp.core.di

import com.example.usersapp.core.di.modules.NetworkModule
import com.example.usersapp.core.di.modules.RepositoryModule
import com.example.usersapp.core.di.modules.UseCaseModule
import com.example.usersapp.core.di.modules.ViewModelModule
import org.koin.core.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.module

fun initApplication(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
    return startKoin {
        appDeclaration()
        modules(
            AppModule().module,
        )
    }
}

@Module(
    includes = [
        NetworkModule::class,
        RepositoryModule::class,
        UseCaseModule::class,
        ViewModelModule::class
    ]
)
class AppModule

@Suppress("unused") //using in iOS
fun initKoin() = initApplication {}