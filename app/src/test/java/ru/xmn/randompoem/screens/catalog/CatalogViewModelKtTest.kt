package ru.xmn.randompoem.screens.catalog

import org.junit.Assert
import org.junit.Test

import ru.xmn.randompoem.model.Poet

/**
 * Created by xmn on 04.09.2017.
 */
class CatalogViewModelKtTest {
    @Test
    fun toPoetViewItems() {
        val items = listOf<Poet>(
                Poet("1","14",14, emptyList()),
                Poet("3","15",15, emptyList()),
                Poet("2","14",14, emptyList()),
                Poet("4","15",15, emptyList()),
                Poet("5","18",18, emptyList()),
                Poet("6","18",18, emptyList()),
                Poet("7","18",18, emptyList())
        )

        val expected = listOf<PoetViewItem>(
                PoetViewItem.HeaderPoetViewItem(14),
                PoetViewItem.CommonPoetViewItem("1", "14", 14),
                PoetViewItem.CommonPoetViewItem("2", "14", 14),
                PoetViewItem.HeaderPoetViewItem(15),
                PoetViewItem.CommonPoetViewItem("3", "15", 15),
                PoetViewItem.CommonPoetViewItem("4", "15", 15),
                PoetViewItem.HeaderPoetViewItem(18),
                PoetViewItem.CommonPoetViewItem("5", "18", 18),
                PoetViewItem.CommonPoetViewItem("6", "18", 18),
                PoetViewItem.CommonPoetViewItem("7", "18", 18))

        Assert.assertEquals(expected, items.toPoetViewItems())
    }

}