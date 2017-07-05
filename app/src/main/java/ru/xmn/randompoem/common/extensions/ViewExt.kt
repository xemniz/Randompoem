package ru.xmn.randompoem.common.extensions

import android.support.v4.view.ViewCompat
import android.view.View

fun View.pairSharedTransition(): android.support.v4.util.Pair<View, String> {
    return android.support.v4.util.Pair<View, String>(this, ViewCompat.getTransitionName(this))
}