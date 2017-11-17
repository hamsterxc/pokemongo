package com.lonebytesoft.hamster.pokemongo.js

import org.w3c.fetch.Response
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine
import kotlin.coroutines.experimental.suspendCoroutine
import kotlin.js.JSON
import kotlin.js.Promise

internal suspend fun <T> Promise<T>.await(): T = suspendCoroutine {
    then(it::resume).catch(it::resumeWithException)
}

internal fun <T> async(block: suspend () -> T) = Promise<T> { resolve, reject ->
    block.startCoroutine(object : Continuation<T> {
        override val context: CoroutineContext = EmptyCoroutineContext

        override fun resume(value: T) {
            resolve(value)
        }

        override fun resumeWithException(exception: Throwable) {
            reject(exception)
        }
    })
}

internal suspend fun <T> parse(promise: Promise<Response>): T {
    val response = promise.await()
    val responseText = response.text().await()
    return JSON.parse(responseText)
}
