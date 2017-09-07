package ru.xmn.randompoem.screens.catalog

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_catalog.*
import kotlinx.android.synthetic.main.item_poet.view.*
import ru.xmn.filmfilmfilm.common.ui.adapter.AutoUpdatableAdapter
import ru.xmn.randompoem.R
import ru.xmn.randompoem.common.extensions.inflate
import ru.xmn.randompoem.common.utils.ToolbarUtils
import kotlin.properties.Delegates

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
    }

    private fun setupRecyclerView() {
        poetRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CatalogActivity)
            adapter = PoetListAdapter()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.poets_list_title)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        container.setPadding(0, ToolbarUtils.statusBarHeight(this), 0, 0)
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

    private fun bindList(poetViewItems: List<PoetViewItem>) {
        (poetRecyclerView.adapter as PoetListAdapter).poets = poetViewItems
    }
}

class PoetListAdapter : RecyclerView.Adapter<PoetListAdapter.ViewHolder>(), AutoUpdatableAdapter {
    var poets by Delegates.observable<List<PoetViewItem>>(emptyList())
    { property, oldValue, newValue ->
        autoNotify(oldValue, newValue)
        { a, b -> a == b }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.create(parent)

    override fun getItemCount(): Int = poets.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(poets[position])


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun create(parent: ViewGroup) = ViewHolder(parent.inflate(R.layout.item_poet))
        }

        fun bind(poetViewItem: PoetViewItem) {
            itemView.poetName.text = (poetViewItem as PoetViewItem.CommonPoetViewItem).name
        }
    }

}
