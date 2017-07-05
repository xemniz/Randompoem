package ru.xmn.filmfilmfilm.common.extensions

import mu.KLogger

fun<T> T.logError(logger: KLogger, context: String, error: Throwable) = logger.debug { "context: $context error: ${error.message}" }
fun<T> T.log(logger: KLogger, context: String, message: String) = logger.debug { "context: $context message: ${message}" }
