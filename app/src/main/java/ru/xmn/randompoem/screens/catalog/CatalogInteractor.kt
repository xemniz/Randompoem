package ru.xmn.randompoem.screens.catalog

import io.reactivex.Single
import ru.xmn.randompoem.application.di.scopes.ActivityScope
import ru.xmn.randompoem.model.CatalogRepository
import ru.xmn.randompoem.model.PoemsRepository
import ru.xmn.randompoem.model.Poet
import ru.xmn.randompoem.poetcommonbuisnesslogic.SelectablePoemsOwner
import javax.inject.Inject

@ActivityScope
class CatalogInteractor @Inject constructor(
        val poemsRepository: PoemsRepository,
        val catalogRepository: CatalogRepository): SelectablePoemsOwner {
    fun unignorePoet(id: String) {
        catalogRepository.unignorePoet(id)
    }

    fun ignorePoet(id: String) {
        catalogRepository.ignorePoet(id)
    }

    fun getPoetsWithMarker(): Single<List<SelectablePoet>> {
        return getSelectablePoems(poemsRepository, catalogRepository)
    }

    fun unignoreAllPoets() {
        catalogRepository.unignoreAllPoets()
    }
}

data class SelectablePoet(val poet: Poet, val ignored: Boolean)