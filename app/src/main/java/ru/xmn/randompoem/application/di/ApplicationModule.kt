package ru.xmn.randompoem.application.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.xmn.randompoem.application.App
import ru.xmn.randompoem.screens.RandomPoemsComponent
import javax.inject.Singleton


@Module(subcomponents = arrayOf(RandomPoemsComponent::class))
class ApplicationModule (private val app: App){
    @Provides @Singleton
    fun provideApplicationContext(): Context = app
}
