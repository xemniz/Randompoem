package ru.xmn.randompoem.screens.catalog

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import ru.xmn.randompoem.R
import ru.xmn.randompoem.common.utils.ToolbarUtils
import ru.xmn.randompoem.model.Poet

class CatalogActivity : LifecycleActivity() {
    private lateinit var catalogViewModel: CatalogViewModel;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)
        setupToolbar()
        setupViewModel()
    }

    private fun setupToolbar() {
        setActionBar(toolbar)
        actionBar.title = "Random poem"
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        toolbar.setPadding(0, ToolbarUtils.statusBarHeight(this), 0, 0);
    }

    private fun setupViewModel() {
        catalogViewModel = ViewModelProviders.of(this).get(CatalogViewModel::class.java);
        catalogViewModel.poets.observe(this, Observer { bindUi(it!!) })
    }

    private fun bindUi(catalogState: CatalogState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
