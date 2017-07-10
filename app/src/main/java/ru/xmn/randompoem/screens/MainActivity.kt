package ru.xmn.randompoem.screens

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import ru.xmn.randompoem.R
import ru.xmn.randompoem.model.Poem

class MainActivity : LifecycleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPoetList()
        setupViewModel()
    }

    private fun setupViewModel() {
        val model = ViewModelProviders.of(this).get(RandomPoemsViewModel::class.java)
        model.randomPoems.observe(this, Observer { bindUi(it) })
    }

    fun bindUi(it: List<Poem>?){
        (listRandomPoems.adapter as AdapterRandomPoems).items = it?: emptyList()
    }

    private fun setupPoetList() {
        listRandomPoems.layoutManager = LinearLayoutManager(this)
        listRandomPoems.adapter = AdapterRandomPoems()
        listRandomPoems.itemAnimator = CustomItemAnimator()
    }
}

class CustomItemAnimator : DefaultItemAnimator() {

}

