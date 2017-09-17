package ru.xmn.randompoem.screens.catalog

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.xmn.randompoem.application.App
import javax.inject.Inject

class CatalogViewModel : ViewModel() {

    val poets: MutableLiveData<CatalogState> = MutableLiveData()
    @Inject
    lateinit var catalogInteractor: CatalogInteractor

    init {
        App.component.catalogComponent().provideModule(CatalogModule()).build().inject(this)
        load()
    }

    private fun load() {
        catalogInteractor.getPoetsWithMarker()
                .map<CatalogState> { CatalogState.Idle(it) }
                .toFlowable()
                .startWith(CatalogState.Loading())
                .subscribe({
                    poets.value = it
                }, { CatalogState.Error(it) })
    }

    fun unignorePoet(it: String) {
        catalogInteractor.unignorePoet(it)
        load()
    }
    fun ignorePoet(it: String) {
        catalogInteractor.ignorePoet(it)
        load()
    }

    fun unignoreAllPoets() {
        catalogInteractor.unignoreAllPoets()
        load()
    }

}

sealed class CatalogState {
    class Idle(items: List<SelectablePoet>) : CatalogState() {
        val poetViewItems: List<SelectablePoet> = items
    }

    class Loading() : CatalogState()

    class Error(error: Throwable) : CatalogState() {
        init {
            error.printStackTrace()
        }
    }
}

