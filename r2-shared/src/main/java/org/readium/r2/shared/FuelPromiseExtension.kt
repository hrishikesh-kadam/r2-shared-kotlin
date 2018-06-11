package org.readium.r2.shared

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.task

fun Request.promise(): Promise<Triple<Request, Response, ByteArray>, Exception> {
    val deferred = deferred<Triple<Request, Response, ByteArray>, Exception>()
    task { response() } success {
        val (request, response, result) = it
        when(result) {
            is Result.Success -> deferred.resolve(Triple(request, response, result.value))
            is Result.Failure -> deferred.reject(result.error)
        }
    } fail {
        deferred.reject(it)
    }
    return deferred.promise
}

val Response.contentTypeEncoding: String
    get() = contentTypeEncoding()

fun Response.contentTypeEncoding(default: String = "utf-8"): String {
    val contentType: String = httpResponseHeaders["Content-Type"]?.first() ?: return default
    return contentType.substringAfterLast("charset=", default).substringAfter(' ', default)
}
