package ru.xmn.randompoem.screens.pandompoems

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_random_poems.*
import mu.KLogging
import org.jetbrains.anko.startActivity
import ru.xmn.randompoem.R
import ru.xmn.randompoem.common.utils.ToolbarUtils.statusBarHeight
import ru.xmn.randompoem.model.Poem
import ru.xmn.randompoem.screens.catalog.CatalogActivity

class RandomPoemsActivity : AppCompatActivity(), LifecycleRegistryOwner {
    private val registry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return registry
    }

    private lateinit var randomPoemsViewModel: RandomPoemsViewModel;

    companion object : KLogging()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_poems)
        setupToolbar()
        setupViewModel()
        filterButton.setOnClickListener{startActivity<CatalogActivity>()}
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Random poem"
    }

    private fun setupViewModel() {
        randomPoemsViewModel = ViewModelProviders.of(this).get(RandomPoemsViewModel::class.java);
        randomPoemsViewModel.randomPoems.observe(this, Observer { bindUi(it) })
    }

    fun bindUi(it: List<SelectablePoetWithPoem>?) {
        listRandomPoems.setPoems(it ?: emptyList())
        listRandomPoems.onSwipe = { randomPoemsViewModel.requestNewPoems() }
        listRandomPoems.ignorePoet = { poetId, ignore -> randomPoemsViewModel.ignorePoet(poetId, ignore) }
    }
}