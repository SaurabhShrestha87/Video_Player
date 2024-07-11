package org.videolan.resources.util

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import org.videolan.tools.isConnected
import java.io.IOException

class ConnectivityInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!context.isConnected()) throw NoConnectivityException()

        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}

class NoConnectivityException : IOException() {

    override val message: String?
        get() = "No connectivity exception"
}