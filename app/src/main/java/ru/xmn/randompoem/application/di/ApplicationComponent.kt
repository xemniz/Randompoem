package ru.xmn.randompoem.application.di

import dagger.Component
import ru.xmn.randompoem.model.PoemsNetworkModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        ApplicationModule::class,
        NetworkModule::class,
        PoemsNetworkModule::class
))
interface ApplicationComponent{
}

