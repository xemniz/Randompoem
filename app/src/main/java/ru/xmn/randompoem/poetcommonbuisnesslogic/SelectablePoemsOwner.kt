package ru.xmn.randompoem.poetcommonbuisnesslogic

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.xmn.randompoem.model.CatalogRepository
import ru.xmn.randompoem.model.PoemsRepository
import ru.xmn.randompoem.model.Poet
import ru.xmn.randompoem.screens.catalog.SelectablePoet

interface SelectablePoemsOwner {
    fun getSelectablePoems(poemsRepository: PoemsRepository, catalogRepository: CatalogRepository): Single<List<SelectablePoet>> {
        return Single.zip(
                poemsRepository.poets(),
                catalogRepository.getCurrentIgnoredPoetIdsList(),
                BiFunction<List<Poet>, List<String>, Pair<List<Poet>, List<String>>>
                { t1, t2 -> Pair(t1, t2) })
                .map { pair -> pair.first.map { poet -> SelectablePoet(poet, pair.second.contains(poet.id)) } }
    }
}