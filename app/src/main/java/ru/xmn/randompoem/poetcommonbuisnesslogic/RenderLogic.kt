package ru.xmn.randompoem.poetcommonbuisnesslogic

import android.widget.ImageView
import ru.xmn.randompoem.common.extensions.isChecked

var ImageView.isEyeCrossed: Boolean
    get() {
        return !this.isChecked
    }
    set(value) {
        this.isChecked = !value
    }