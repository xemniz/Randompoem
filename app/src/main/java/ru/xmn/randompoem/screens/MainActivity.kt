package ru.xmn.randompoem.screens

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.activity_main.*
import mu.KLogger
import mu.KLogging
import ru.xmn.filmfilmfilm.common.extensions.log
import ru.xmn.randompoem.R
import ru.xmn.randompoem.model.Poem

class MainActivity : LifecycleActivity() {

    companion object : KLogging()

    lateinit private var gestureListener: GestureDetector.SimpleOnGestureListener
    lateinit private var gestureDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupGestureListener()
        setupPoetList()
        setupViewModel()
    }

    private fun setupGestureListener() {
        gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                if (distanceX < 0) return false
                val firstView = listRandomPoems.layoutManager.getChildAt(0)
                firstView.translationX -= distanceX

                for (i in 1..listRandomPoems.layoutManager.childCount - 1) {
                    val view = listRandomPoems.layoutManager.getChildAt(i)
                    val shiftCoef = .2
                    val translationTmp = firstView.translationX - distanceX + view.width * shiftCoef * i
                    val translation = if (translationTmp > 0) 0f else translationTmp.toFloat()
                    view.translationX = translation
                }
                return false
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                return bounceBack()
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return bounceBack()
            }

            private fun bounceBack(): Boolean {
                for (i in 0..listRandomPoems.layoutManager.childCount - 1) {
                    val view = listRandomPoems.layoutManager.getChildAt(i)
                    view.animate().translationX(0f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
                }
                return true
            }
        }
        gestureDetector = GestureDetectorCompat(this, gestureListener)
    }

    private fun setupViewModel() {
        val model = ViewModelProviders.of(this).get(RandomPoemsViewModel::class.java)
        model.randomPoems.observe(this, Observer { bindUi(it) })
    }

    fun bindUi(it: List<Poem>?) {
        (listRandomPoems.adapter as AdapterRandomPoems).items = it ?: emptyList()
    }

    private fun setupPoetList() {
        listRandomPoems.layoutManager = LinearLayoutManager(this)
        listRandomPoems.adapter = AdapterRandomPoems()
        listRandomPoems.setOnTouchListener { v, event -> gestureDetector.onTouchEvent(event) }
    }
}

