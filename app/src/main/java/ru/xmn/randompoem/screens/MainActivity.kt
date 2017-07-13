package ru.xmn.randompoem.screens

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import mu.KLogging
import ru.xmn.randompoem.R
import ru.xmn.randompoem.common.utils.ToolbarUtils.statusBarHeight
import ru.xmn.randompoem.model.Poem

class MainActivity : LifecycleActivity() {
    private lateinit var randomPoemsViewModel: RandomPoemsViewModel;

    companion object : KLogging()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBar(toolbar)
        actionBar.title = "Random poem"
        toolbar.setPadding(0, statusBarHeight(this), 0, 0);
        setupViewModel()
    }

    private fun setupViewModel() {
        randomPoemsViewModel = ViewModelProviders.of(this).get(RandomPoemsViewModel::class.java);
        randomPoemsViewModel.randomPoems.observe(this, Observer { bindUi(it) })
    }

    fun bindUi(it: List<Poem>?) {
        listRandomPoems.setPoems(it ?: emptyList())
        listRandomPoems.onSwipe = {randomPoemsViewModel.requestNewPoems()}
    }
}

