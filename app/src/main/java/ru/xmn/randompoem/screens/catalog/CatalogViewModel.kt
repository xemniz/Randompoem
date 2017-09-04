package ru.xmn.randompoem.screens.catalog

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.functions.Consumer
import ru.xmn.randompoem.application.App
import ru.xmn.randompoem.model.PoemsRepository
import ru.xmn.randompoem.model.Poet
import javax.inject.Inject

class CatalogViewModel : ViewModel() {

    val poets: MutableLiveData<CatalogState> = MutableLiveData()
    @Inject
    lateinit var poemsRepository: PoemsRepository

    init {
        App.component.inject(this)
        poemsRepository.poets()
                .map<CatalogState> { CatalogState.Idle(it) }
                .toFlowable()
                .startWith(CatalogState.Loading())
                .subscribe({
                    poets.value = it
                }, { CatalogState.Error(it) })
    }

}

sealed class CatalogState {
    class Idle(items: List<Poet>) : CatalogState() {
        val poetViewItems: List<PoetViewItem> = items.toPoetViewItems()
    }

    class Loading() : CatalogState()

    class Error(error: Throwable) : CatalogState() {
        init {
            error.printStackTrace()
        }
    }
}

fun List<Poet>.toPoetViewItems(): List<PoetViewItem> {
    val sorted = this.sortedBy { it.century }
    return sorted.foldIndexed(ArrayList<PoetViewItem>(), { index, acc, poet ->
        when {
            index == 0 -> acc.apply {
                add(PoetViewItem.HeaderPoetViewItem(poet.century ?: 0))
                add(PoetViewItem.CommonPoetViewItem(poet.id, poet.name, poet.century ?: 0))
            }
            sorted[index - 1].century ?: 0 != (poet.century ?: 0) -> acc.apply {
                add(PoetViewItem.HeaderPoetViewItem(poet.century ?: 0))
                add(PoetViewItem.CommonPoetViewItem(poet.id, poet.name, poet.century ?: 0))
            }
            else -> acc.apply { add(PoetViewItem.CommonPoetViewItem(poet.id, poet.name, poet.century ?: 0)) }
        }
    })
}

sealed class PoetViewItem {
    data class HeaderPoetViewItem(val name: Int) : PoetViewItem()
    data class CommonPoetViewItem(val id: String, val name: String, val century: Int) : PoetViewItem()
}