package ru.xmn.randompoem.screens

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import ru.xmn.randompoem.R
import ru.xmn.randompoem.common.extensions.inflate
import ru.xmn.randompoem.model.Poem
import kotlinx.android.synthetic.main.item_poem.view.*
import android.view.ViewConfiguration
import android.support.animation.DynamicAnimation
import android.support.animation.FlingAnimation


class PoemsLayout : LinearLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    val itemCount: Int = 3
    private var onSwipe: (() -> Unit)? = null

    private fun init() {
        orientation = VERTICAL

        fun bounceBack(): Boolean {
            for (i in 0..childCount - 1) {
                val view = getChildAt(i)
                view.animate().translationX(0f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
            }
            return true
        }

        fun performSwipe(velocity: Float = 0f): Boolean {
            val duration = if (velocity > 50) (width / velocity * 100).toLong() else 200

            for (i in 0..childCount - 1) {
                val view = getChildAt(i)
                val fling = FlingAnimation(view, DynamicAnimation.TRANSLATION_X)
                val minVel = 5000f
                val velocityNormalized = when {
                    velocity < minVel -> minVel
                    else -> velocity
                }
                fling.setStartVelocity(-velocityNormalized)
                        .setMinValue(Float.NEGATIVE_INFINITY)
                        .setMaxValue(0f)
                        .setFriction(.9f)
                        .start();
            }
            return true
        }

        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            private val swipe_Min_Distance = 100
            private val swipe_Max_Distance get() = width
            private val swipe_Min_Velocity = 100

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                if (distanceX < 0) return false
                val firstView = getChildAt(0)
                firstView.translationX -= distanceX

                for (i in 1..childCount - 1) {
                    val view = getChildAt(i)
                    val shiftCoef = .1
                    val translationTmp = firstView.translationX - distanceX + view.width * shiftCoef * i
                    val translation = if (translationTmp > 0) 0f else translationTmp.toFloat()
                    view.translationX = translation
                }
                return false
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                val xDistance = Math.abs(e1.getX() - e2.getX())
                val yDistance = Math.abs(e1.getY() - e2.getY())

                if (xDistance > this.swipe_Max_Distance || yDistance > this.swipe_Max_Distance)
                    return false

                val velX = Math.abs(velocityX)
                var result = false

                if (velX > this.swipe_Min_Velocity && xDistance > this.swipe_Min_Distance) {
                    if (e1.getX() > e2.getX())
                    // right to left
                    {
                        onSwipe?.invoke()
                        performSwipe(velX)
                    }
                    result = true
                }

                return result
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return bounceBack()
            }
        }

        val gestureDetector = object : GestureDetector(context, gestureListener) {
            override fun onTouchEvent(ev: MotionEvent): Boolean {
                if (ev.action == MotionEvent.ACTION_UP) {
                    bounceBack()
                }
                return super.onTouchEvent(ev)
            }
        }

        setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                bounceBack()
            }
            gestureDetector.onTouchEvent(event)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        return true
    }

    fun setPoems(poems: List<Poem>) {
        poems.take(itemCount)
                .withIndex()
                .forEach { (index, poem) ->
                    val view = inflate(R.layout.item_poem)
                    view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, height / itemCount)
                    view.translationX = (height * 1.2).toFloat()
                    addView(view)
                    bind(view, poem)
                    view.animate().translationX(0f).setStartDelay((index * 80).toLong()).setDuration(300).setInterpolator(DecelerateInterpolator(1.5f)).start()
                }
    }

    private fun bind(view: View, poem: Poem) {
        view.itemPoemText.text = poem.text
    }

}


