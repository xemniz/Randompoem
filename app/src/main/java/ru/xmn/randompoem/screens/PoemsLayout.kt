package ru.xmn.randompoem.screens

import android.content.Context
import android.graphics.Color
import android.provider.CalendarContract
import android.support.animation.DynamicAnimation
import android.support.animation.FlingAnimation
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.item_poem.view.*
import ru.xmn.randompoem.R
import ru.xmn.randompoem.common.extensions.inflate
import ru.xmn.randompoem.model.Poem


class PoemsLayout : FrameLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    val linearLayout = LinearLayout(context)
    val shimmerLayout = ShimmerFrameLayout(context)
    val itemCount: Int = 3
    var onSwipe: (() -> Unit)? = null

    private fun init() {
        shimmerLayout.addView(ImageView(context).apply {
            setImageResource(android.R.drawable.ic_media_next)
//            setBackgroundColor(Color.LTGRAY)
            layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        })
        shimmerLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        shimmerLayout.setBackgroundColor(Color.WHITE)
        shimmerLayout.baseAlpha = 0f
        addView(shimmerLayout)
        shimmerLayout.startShimmerAnimation()

        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(linearLayout)

        fun bounceBack(): Boolean {
            for (i in 0..linearLayout.childCount - 1) {
                val view = linearLayout.getChildAt(i)
                view.animate().translationX(0f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
            }
            return true
        }

        fun performSwipe(velocity: Float = 0f): Boolean {
            for (i in 0..linearLayout.childCount - 1) {
                val view = linearLayout.getChildAt(i)
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
                        .addEndListener(object : DynamicAnimation.OnAnimationEndListener {
                            override fun onAnimationEnd(animation: DynamicAnimation<out DynamicAnimation<*>>?, canceled: Boolean, value: Float, velocity: Float) {
                                if (i == linearLayout.childCount - 1) {
                                }
                            }
                        })
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
                val firstView = linearLayout.getChildAt(0)
                firstView.translationX -= distanceX

                for (i in 1..linearLayout.childCount - 1) {
                    val view = linearLayout.getChildAt(i)
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
                    {                                    requestNewItems()

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

    private fun requestNewItems() {
        onSwipe?.invoke()
        shimmerLayout.visible()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        return true
    }

    fun setPoems(poems: List<Poem>) {
        linearLayout.removeAllViews()
        if (poems.isEmpty()) {
            shimmerLayout.visible()
        } else {
            shimmerLayout.invisible()
            poems.take(itemCount)
                    .withIndex()
                    .forEach { (index, poem) ->
                        val view = inflate(R.layout.item_poem)
                        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, height / itemCount)
                        view.translationX = (height * 1.2).toFloat()
                        linearLayout.addView(view)
                        bind(view, poem)
                        view.animate().translationX(0f).setStartDelay((index * 80).toLong()).setDuration(300).setInterpolator(DecelerateInterpolator(1.5f)).start()
                    }
        }

    }

    private fun bind(view: View, poem: Poem) {
        view.itemPoemText.text = poem.text
    }

}

private fun View.visible() {
    this.visibility = View.VISIBLE
}
private fun View.invisible() {
    this.visibility = View.INVISIBLE
}
private fun View.gone() {
    this.visibility = View.GONE
}


