package ru.xmn.randompoem.screens.catalog

import dagger.Subcomponent
import ru.xmn.randompoem.application.di.scopes.ActivityScope

@ActivityScope
@Subcomponent(modules = arrayOf(CatalogModule::class))
interface CatalogComponent {
    fun inject(randomPoemsViewModel: CatalogViewModel)

    @Subcomponent.Builder
    interface Builder {
        fun build(): CatalogComponent
        fun provideModule(r: CatalogModule): Builder
    }
}