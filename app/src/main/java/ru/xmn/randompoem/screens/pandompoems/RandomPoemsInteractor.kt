package ru.xmn.randompoem.screens.pandompoems

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.xmn.randompoem.application.di.scopes.ActivityScope
import ru.xmn.randompoem.common.extensions.randomItem
import ru.xmn.randompoem.common.extensions.randomize
import ru.xmn.randompoem.model.CatalogRepository
import ru.xmn.randompoem.model.Poem
import ru.xmn.randompoem.model.PoemsRepository
import ru.xmn.randompoem.poetcommonbuisnesslogic.SelectablePoemsOwner
import ru.xmn.randompoem.screens.catalog.SelectablePoet
import javax.inject.Inject

@ActivityScope
class RandomPoemsInteractor
@Inject constructor(val poemsRepository: PoemsRepository, val catalogRepository: CatalogRepository) : SelectablePoemsOwner {
    fun getRandomPoems(): Single<List<SelectablePoetWithPoem>> {
        val selectablePoets = getSelectablePoems(poemsRepository, catalogRepository)

        return selectablePoets
                .toFlowable()
                .map { it.filter { !it.ignored } }
                .map { it.randomize() }
                .flatMap { Flowable.fromIterable(it) }
                .take(3)
                .flatMap { selectablePoet ->
                    poemsRepository
                            .poetWithPoems(selectablePoet.poet)
                            .toFlowable()
                            .filter { it.poems != null }
                            .filter { it.poems!!.isNotEmpty() }
                            .map { SelectablePoetWithPoem(it.poems!!.randomItem(), selectablePoet) }
                            .subscribeOn(Schedulers.io())
                }
                .toList()
    }

    fun ignorePoet(poetId: String, ignore: Boolean) {
        when (ignore) {
            true -> catalogRepository.ignorePoet(poetId)
            false -> catalogRepository.unignorePoet(poetId)
        }
    }

}

data class SelectablePoetWithPoem(val poem: Poem, val poet: SelectablePoet)
