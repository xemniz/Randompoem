package ru.xmn.randompoem.common.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadUrl(url: String) {
    Glide.with(context)
            .load(url)
            .into(this)
}

var ImageView.isChecked: Boolean
    get() {
        return this.drawable.state.contains(android.R.attr.state_checked)
    }
    set(checked) {
        val stateSet = intArrayOf(android.R.attr.state_checked * if (checked) 1 else -1)
        this.setImageState(stateSet, true)
        assert(this.isChecked == checked)
    }
