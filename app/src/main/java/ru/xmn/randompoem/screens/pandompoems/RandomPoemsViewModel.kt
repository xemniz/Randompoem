package ru.xmn.randompoem.screens.pandompoems

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import mu.KLogging
import ru.xmn.randompoem.application.App
import ru.xmn.randompoem.model.Poem
import javax.inject.Inject

class RandomPoemsViewModel : ViewModel() {

    companion object : KLogging()

    @Inject
    lateinit var poemsInteractor: RandomPoemsInteractor
    val randomPoems: MutableLiveData<List<SelectablePoetWithPoem>> = MutableLiveData()

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

    fun ignorePoet(poetId: String, ignore: Boolean) {
        poemsInteractor.ignorePoet(poetId, ignore)
    }

}