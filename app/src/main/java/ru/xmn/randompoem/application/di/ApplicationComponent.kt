package ru.xmn.randompoem.application.di

import dagger.Component
import ru.xmn.randompoem.model.CatalogRepositoryModule
import ru.xmn.randompoem.model.PoemsNetworkModule
import ru.xmn.randompoem.screens.catalog.CatalogComponent
import ru.xmn.randompoem.screens.pandompoems.RandomPoemsComponent
import ru.xmn.randompoem.screens.catalog.CatalogViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        ApplicationModule::class,
        NetworkModule::class,
        PoemsNetworkModule::class,
        CatalogRepositoryModule::class
))
interface ApplicationComponent{
    fun randomPoemsComponent(): RandomPoemsComponent.Builder
    fun catalogComponent(): CatalogComponent.Builder
}

