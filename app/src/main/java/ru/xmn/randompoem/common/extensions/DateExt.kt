package ru.xmn.randompoem.common.extensions

import okhttp3.OkHttpClient
import java.util.*

fun Date.stampInSeconds(): Long = this.time / 1000


