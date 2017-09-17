package ru.xmn.randompoem.screens.pandompoems

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.support.animation.DynamicAnimation
import android.support.animation.FlingAnimation
import android.support.v4.widget.NestedScrollView
import android.transition.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_random_poems.view.*
import kotlinx.android.synthetic.main.item_poem.view.*
import mu.KLogging
import ru.xmn.randompoem.R
import ru.xmn.randompoem.common.extensions.*
import ru.xmn.randompoem.poetcommonbuisnesslogic.isEyeCrossed


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
    var ignorePoet: (String, Boolean) -> Unit = { _, _ -> }
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
        val anim = shimmerLayout.background as AnimationDrawable
        anim.setEnterFadeDuration(1000)
        anim.setExitFadeDuration(1000)
        anim.start()
    }

    private fun setupPoemsLayout() {
        linearLayout = poemsListLayout
    }

    private fun setupGestureDetection() {
        fun bounceBack(): Boolean {
            (0..linearLayout.childCount - 1).forEach { i ->
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
                        .addEndListener { _, _, _, _ ->
                            if (i == linearLayout.childCount - 1) {
                            }
                        }
                        .start()
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
                val xDistance = Math.abs(e1.x - e2.x)
                val yDistance = Math.abs(e1.y - e2.y)

                if (xDistance > this.swipe_Max_Distance || yDistance > this.swipe_Max_Distance)
                    return false

                val velX = Math.abs(velocityX)
                var result = false

                if (velX > this.swipe_Min_Velocity && xDistance > this.swipe_Min_Distance) {
                    if (e1.x > e2.x)
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

        setOnTouchListener { _, event ->
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
        return when (extendedItem) {
            SHOW_ALL -> true
            else -> {
                false
            }
        }
    }

    private fun requestNewItems() {
        onSwipe?.invoke()
        shimmerLayout.startAnim()
    }

    fun setPoems(poems: List<SelectablePoetWithPoem>) {
        linearLayout.removeAllViews()
        when {
            poems.isEmpty() -> shimmerLayout.startAnim()
            else -> {
                shimmerLayout.stopAnim()
                poems.take(itemCount)
                        .withIndex()
                        .forEach { (index, poem) -> animateItemAppearing(poem, index) }
            }
        }

    }

    private fun animateItemAppearing(poet: SelectablePoetWithPoem, index: Int) {
        val view = inflate(R.layout.item_poem)
        val param = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                1f
        )
        view.layoutParams = param
        view.translationX = (width * 1.2).toFloat()
        linearLayout.addView(view)
        bind(view, poet, ignorePoet)
        view.animate().translationX(0f).setStartDelay((index * 80).toLong()).setDuration(300).setInterpolator(DecelerateInterpolator(1.5f)).start()
    }

    private fun bind(view: View, selectablePoetWithPoem: SelectablePoetWithPoem, ignorePoet: (String, Boolean) -> Unit) {
        val (poem, selectablePoet) = selectablePoetWithPoem
        val (poet, isIgnored) = selectablePoet
        view.itemPoemText.text = poem.text
        view.itemPoemTitle.text = poem.title
        view.itemPoemContent.setOnClickListener {
            collapseAllItems()
        }
        poet.apply {
            view.itemPoemAuthorName.text = name
            val itemPoemAuthorIgnore = view.itemPoemAuthorIgnore
            itemPoemAuthorIgnore.isEyeCrossed = true
            println(itemPoemAuthorIgnore.isEyeCrossed)
            itemPoemAuthorIgnore.isEyeCrossed = !itemPoemAuthorIgnore.isEyeCrossed
            println(itemPoemAuthorIgnore.isEyeCrossed)
            assert(itemPoemAuthorIgnore.isEyeCrossed == false)
            view.itemPoemAuthor.setOnClickListener {
                ignorePoet(id, !itemPoemAuthorIgnore.isEyeCrossed)
                itemPoemAuthorIgnore.isEyeCrossed = !itemPoemAuthorIgnore.isEyeCrossed
                Log.d("", "${itemPoemAuthorIgnore.isEyeCrossed}, ${!itemPoemAuthorIgnore.isEyeCrossed}")
            }
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

        finalSet.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(p0: Transition?) {
            }

            override fun onTransitionResume(p0: Transition?) {
            }

            override fun onTransitionPause(p0: Transition?) {
            }

            override fun onTransitionCancel(p0: Transition?) {
            }

            override fun onTransitionStart(p0: Transition?) {
                linearLayout.views.forEach { view -> scrollTop(view.itemPoemScroll) }
            }
        })

        TransitionManager.beginDelayedTransition(this, finalSet)

        this.linearLayout.views.forEach { view ->

            view.itemPoemCard.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

            if (view.visibility == View.VISIBLE) {
                view.itemPoemAuthor.gone()
                view.itemPoemTitle.gone()
            } else {
                view.visible()
            }
        }
    }

    private fun scrollTop(view: NestedScrollView) {
        ValueAnimator.ofInt(view.scrollY, 0).apply {
            addUpdateListener { view.scrollY = it.getAnimatedValue() as Int }
            setDuration(300)
            interpolator = AccelerateInterpolator(1.5f)
            start()
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
                    it.value.itemPoemCard.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    it.value.itemPoemAuthor.visible()
                    it.value.itemPoemTitle.visible()
                }
            }
        }
    }

    private fun provideSlideAndBoundsTransition(index: Int): TransitionSet {
        val slideAndBounds = this.linearLayout.views.withIndex()
                .fold(TransitionSet(), { s: TransitionSet, (i, value) ->
                    when {
                        i < index -> {
                            s.addTransition(Slide().apply {
                                slideEdge = Gravity.TOP
                                addTarget(value)
                            })
                        }
                        i > index -> {
                            s.addTransition(Slide().apply {
                                slideEdge = Gravity.BOTTOM
                                addTarget(value)
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


