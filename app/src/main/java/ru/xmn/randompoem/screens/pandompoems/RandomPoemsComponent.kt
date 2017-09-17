package ru.xmn.randompoem.screens.pandompoems

import dagger.Subcomponent
import ru.xmn.randompoem.application.di.scopes.ActivityScope

@ActivityScope
@Subcomponent(modules = arrayOf(RandomPoemsModule::class))
interface RandomPoemsComponent {
    fun inject(randomPoemsViewModel: RandomPoemsViewModel)

    @Subcomponent.Builder
    interface Builder {
        fun build(): RandomPoemsComponent
        fun provideModule(r: RandomPoemsModule): Builder
    }
}