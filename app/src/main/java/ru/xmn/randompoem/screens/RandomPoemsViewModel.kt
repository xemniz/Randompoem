package ru.xmn.randompoem.screens

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import dagger.Module
import dagger.Subcomponent
import io.reactivex.Single
import io.reactivex.functions.Consumer
import mu.KLogging
import ru.xmn.randompoem.application.App
import ru.xmn.randompoem.application.di.scopes.ActivityScope
import ru.xmn.randompoem.model.Poem
import ru.xmn.randompoem.model.PoemsRepository
import ru.xmn.randompoem.model.Poet
import java.util.*
import javax.inject.Inject

class RandomPoemsViewModel : ViewModel(){

    companion object: KLogging()

    @Inject
    lateinit var filmsProvider: RandomPoemsInteractor
    val randomPoems: MutableLiveData<List<Poem>> = MutableLiveData()
    init {
        App.component.randomPoemsComponent().provideModule(RandomPoemsModule()).build().inject(this)
        filmsProvider.getRandomPoets()
                .subscribe(Consumer { randomPoems.value = it.poems })
    }

}

@ActivityScope
class RandomPoemsInteractor @Inject constructor(val poemsRepository: PoemsRepository){
    fun getRandomPoets(): Single<Poet> {
        return poemsRepository.poets()
                .map { it.randomItem<Poet>() }
                .flatMap { poemsRepository.poetWithPoems(it) }
    }

}

public fun<T> List<T>.randomItem(): T {
    val r = Random()
    return this[r.nextInt(this.size-1)]
}

@Module
class RandomPoemsModule

@ActivityScope
@Subcomponent(modules = arrayOf(RandomPoemsModule::class))
interface RandomPoemsComponent{
    fun inject(randomPoemsViewModel: RandomPoemsViewModel)

    @Subcomponent.Builder
    interface Builder {
        fun build(): RandomPoemsComponent
        fun provideModule(r: RandomPoemsModule): RandomPoemsComponent.Builder
    }
}