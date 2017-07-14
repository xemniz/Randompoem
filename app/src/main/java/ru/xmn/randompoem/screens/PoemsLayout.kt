package ru.xmn.randompoem.screens

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.support.animation.DynamicAnimation
import android.support.animation.FlingAnimation
import android.transition.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_poem.view.*
import mu.KLogging
import ru.xmn.randompoem.R
import ru.xmn.randompoem.common.extensions.*
import ru.xmn.randompoem.model.Poem


class PoemsLayout : FrameLayout {
    companion object : KLogging()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    lateinit var linearLayout: LinearLayout
    lateinit var shimmerLayout: FrameLayout
    val itemCount: Int = 3
    var onSwipe: (() -> Unit)? = null
    private val SHOW_ALL: Int = -1
    var extendedItem = SHOW_ALL

    private fun init() {
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupLoadingLayout()
        setupPoemsLayout()
        setupGestureDetection()
    }

    private fun setupLoadingLayout() {
        shimmerLayout = poemsLoadingLayout
        shimmerLayout.background = context.getDrawable(R.drawable.animation_list)
        val anim = shimmerLayout.getBackground() as AnimationDrawable
        anim.setEnterFadeDuration(600)
        anim.setExitFadeDuration(600)
        anim.start()
    }

    private fun setupPoemsLayout() {
        linearLayout = poemsListLayout
    }

    private fun setupGestureDetection() {
        fun bounceBack(): Boolean {
            for (i in 0..linearLayout.childCount - 1) {
                val view = linearLayout.getChildAt(i)
                view.animate().translationX(0f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
            }
            return false
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
                        .setMaxValue(Float.POSITIVE_INFINITY)
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
                firstView?.let {
                    it.translationX -= distanceX
                }

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
                    {
                        requestNewItems()

                        performSwipe(velX)
                    }
                    result = true
                }

                return result
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val clickedView = this@PoemsLayout.linearLayout.views.withIndex().filter { it.value.isInBounds(e.x.toInt(), e.y.toInt()) }.firstOrNull()
                clickedView?.let {
                    onChildClick(clickedView.index)
                }
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

    private fun onChildClick(index: Int) {
        expandItem(index)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return when {
            extendedItem == SHOW_ALL -> true
            else -> {
                linearLayout.getChildAt(extendedItem).dispatchTouchEvent(ev)
                false
            }
        }
    }

    private fun requestNewItems() {
        onSwipe?.invoke()
        shimmerLayout.startAnim()
    }

    fun setPoems(poems: List<Poem>) {
        linearLayout.removeAllViews()
        if (poems.isEmpty()) {
            shimmerLayout.startAnim()
        } else {
            shimmerLayout.stopAnim()
            poems.take(itemCount)
                    .withIndex()
                    .forEach { (index, poem) ->
                        val view = inflate(R.layout.item_poem)
                        val param = LinearLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                1f
                        )
                        view.layoutParams = param
                        view.translationX = (width * 1.2).toFloat()
                        linearLayout.addView(view)
                        bind(view, poem)
                        view.animate().translationX(0f).setStartDelay((index * 80).toLong()).setDuration(300).setInterpolator(DecelerateInterpolator(1.5f)).start()
                    }
        }

    }

    private fun bind(view: View, poem: Poem) {
        view.itemPoemText.text = poem.text
        view.itemPoemTitle.text = poem.title
        view.itemPoemAuthor.text = poem.poet?.name
        view.itemPoemContent.setOnClickListener {
            collapseAllItems()
        }
    }

    private fun collapseAllItems() {
        extendedItem = SHOW_ALL

        val index = linearLayout.views.withIndex().filter { it.value.visibility == View.VISIBLE }.map { it.index }.first()

        val slideAndBounds = provideSlideAndBoundsTransition(index)

        val finalSet = TransitionSet()
                .addTransition(Fade())
                .addTransition(slideAndBounds)
                .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)

        TransitionManager.beginDelayedTransition(this, finalSet)
        this.linearLayout.views.forEach {
            if (it.visibility == View.VISIBLE) {
                it.itemPoemAuthor.gone()
                it.itemPoemTitle.gone()
            } else
                it.visible()
        }
    }

    private fun expandItem(index: Int) {
        if (extendedItem == SHOW_ALL) {
            extendedItem = index

            val slideAndBounds = provideSlideAndBoundsTransition(index)

            val finalSet = TransitionSet().addTransition(slideAndBounds).addTransition(Fade()).setOrdering(TransitionSet.ORDERING_SEQUENTIAL)

            TransitionManager.beginDelayedTransition(this, finalSet)
            this.linearLayout.views.withIndex().forEach {
                if (it.index != index)
                    it.value.gone()
                else {
                    it.value.itemPoemAuthor.visible()
                    it.value.itemPoemTitle.visible()
                }
            }
            itemPoemScroll.scrollY = 0
            itemPoemScroll.scrollTo(0, 0)
        }
    }

    private fun provideSlideAndBoundsTransition(index: Int): TransitionSet {
        val slideAndBounds = this.linearLayout.views.withIndex()
                .fold(TransitionSet(), {
                    s: TransitionSet, i: IndexedValue<View> ->
                    when {
                        i.index < index -> {
                            s.addTransition(Slide().apply {
                                slideEdge = Gravity.TOP
                                addTarget(i.value)
                            })
                        }
                        i.index > index -> {
                            s.addTransition(Slide().apply {
                                slideEdge = Gravity.BOTTOM
                                addTarget(i.value)
                            })
                        }
                        else -> {
                        }
                    }
                    s
                })
        slideAndBounds.addTransition(ChangeBounds())
        return slideAndBounds
    }

}

private fun View.startAnim() {
    this.visibility = View.VISIBLE
    val anim = this.background as AnimationDrawable
    anim.start()

}

private fun View.stopAnim() {
    val anim = this.background as AnimationDrawable
    anim.stop()
}


