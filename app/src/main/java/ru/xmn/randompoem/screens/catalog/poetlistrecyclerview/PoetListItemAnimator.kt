package ru.xmn.randompoem.screens.catalog.poetlistrecyclerview

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView

class PoetListItemAnimator : DefaultItemAnimator() {
    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        //checkbox will be animate when item is rebinded
        return true
    }
}