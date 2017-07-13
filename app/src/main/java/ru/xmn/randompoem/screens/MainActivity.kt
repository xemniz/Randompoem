package ru.xmn.randompoem.screens

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.activity_main.*
import mu.KLogging
import ru.xmn.randompoem.R
import ru.xmn.randompoem.model.Poem

class MainActivity : LifecycleActivity() {
    private lateinit var randomPoemsViewModel: RandomPoemsViewModel;

    companion object : KLogging()
    lateinit private var gestureListener: GestureDetector.SimpleOnGestureListener

    lateinit private var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
    }

//    }

    private fun setupViewModel() {
        randomPoemsViewModel = ViewModelProviders.of(this).get(RandomPoemsViewModel::class.java);
        randomPoemsViewModel.randomPoems.observe(this, Observer { bindUi(it) })
    }

    fun bindUi(it: List<Poem>?) {
//        (listRandomPoems.adapter as AdapterRandomPoems).items = it ?: emptyList()
        listRandomPoems.setPoems(it ?: emptyList())
        listRandomPoems.onSwipe = {randomPoemsViewModel.requestNewPoems()}
    }

//    private fun setupPoetList() {
//        listRandomPoems.layoutManager = LinearLayoutManager(this)
//        listRandomPoems.adapter = AdapterRandomPoems()
//        listRandomPoems.setOnTouchListener { v, event -> gestureDetector.onTouchEvent(event) }
//    }
}

