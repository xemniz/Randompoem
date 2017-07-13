package ru.xmn.randompoem.screens

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import dagger.Module
import dagger.Subcomponent
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import mu.KLogging
import ru.xmn.randompoem.application.App
import ru.xmn.randompoem.application.di.scopes.ActivityScope
import ru.xmn.randompoem.model.Poem
import ru.xmn.randompoem.model.PoemsRepository
import java.util.*
import javax.inject.Inject

class RandomPoemsViewModel : ViewModel() {

    companion object : KLogging()

    @Inject
    lateinit var poemsInteractor: RandomPoemsInteractor
    val randomPoems: MutableLiveData<List<Poem>> = MutableLiveData()

    init {
        App.component.randomPoemsComponent().provideModule(RandomPoemsModule()).build().inject(this)
        randomPoems.value = emptyList()
        requestNewPoems()
    }

    fun requestNewPoems() {
        poemsInteractor.getRandomPoems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { randomPoems.value = it })
    }

}

@ActivityScope
class RandomPoemsInteractor @Inject constructor(val poemsRepository: PoemsRepository) {
    fun getRandomPoems(): Single<List<Poem>> {
        return poemsRepository.poets()
                .flatMap { poets ->
                    Single.just((0 until poets.size).map { poets.randomItem() })
                }
                .toFlowable()
                .flatMap { Flowable.fromIterable(it) }
                .take(3)
                .flatMap {
                    poemsRepository.poetWithPoems(it).toFlowable()
                            .subscribeOn(Schedulers.io())
                }
                .filter { it.poems != null }
                .filter { it.poems!!.isNotEmpty() }
                .map { it.poems!!.randomItem() }
                .toList()
    }

}

public fun <T> List<T>.randomItem(): T {
    val r = Random()
    return if (this.size - 1 == 0) this[r.nextInt(this.size - 1)] else this[0]
}

@Module
class RandomPoemsModule

@ActivityScope
@Subcomponent(modules = arrayOf(RandomPoemsModule::class))
interface RandomPoemsComponent {
    fun inject(randomPoemsViewModel: RandomPoemsViewModel)

    @Subcomponent.Builder
    interface Builder {
        fun build(): RandomPoemsComponent
        fun provideModule(r: RandomPoemsModule): RandomPoemsComponent.Builder
    }
}