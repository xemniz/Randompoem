package ru.xmn.randompoem.screens.catalog

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_catalog.*
import ru.xmn.randompoem.R
import ru.xmn.randompoem.screens.catalog.poetlistrecyclerview.PoetListAdapter
import ru.xmn.randompoem.screens.catalog.poetlistrecyclerview.PoetListItemAnimator

class CatalogActivity : AppCompatActivity(), LifecycleRegistryOwner {
    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return registry
    }

    private lateinit var catalogViewModel: CatalogViewModel;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)
        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        selectAllButton.setOnClickListener{
            catalogViewModel.unignoreAllPoets()
        }
    }

    private fun setupRecyclerView() {
        poetRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CatalogActivity)
            adapter = PoetListAdapter(catalogViewModel::unignorePoet, catalogViewModel::ignorePoet)
            itemAnimator = PoetListItemAnimator()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            title = getString(R.string.poets_list_title)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setupViewModel() {
        catalogViewModel = ViewModelProviders.of(this).get(CatalogViewModel::class.java);
        catalogViewModel.poets.observe(this, Observer { bindUi(it!!) })
    }

    private fun bindUi(catalogState: CatalogState) {
        when (catalogState) {
            is CatalogState.Idle -> bindList(catalogState.poetViewItems)
            is CatalogState.Error -> {
            }
            is CatalogState.Loading -> {
            }
        }
    }

    private fun bindList(poetViewItems: List<SelectablePoet>) {
        (poetRecyclerView.adapter as PoetListAdapter).poets = poetViewItems
    }
}

