package ru.xmn.randompoem.screens

import android.arch.lifecycle.LifecycleActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import mu.KLogging
import org.jetbrains.anko.startActivity
import ru.xmn.randompoem.R
import ru.xmn.randompoem.common.utils.ToolbarUtils.statusBarHeight
import ru.xmn.randompoem.screens.catalog.CatalogActivity

class MainActivity : LifecycleActivity() {

    companion object : KLogging()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_poems)
        setupToolbar()
        setupButtons()
    }

    private fun setupButtons() {
        random_poems_button.setOnClickListener { startActivity<RandomPoemsActivity>() }
        catalog_button.setOnClickListener { startActivity<CatalogActivity>() }
    }

    private fun setupToolbar() {
        setActionBar(toolbar)
        actionBar.title = "Random poem"
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        toolbar.setPadding(0, statusBarHeight(this), 0, 0);
    }

}
