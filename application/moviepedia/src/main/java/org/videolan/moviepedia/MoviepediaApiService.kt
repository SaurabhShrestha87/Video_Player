package org.videolan.moviepedia

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

private const val USER_AGENT = "VLC-Android"

private fun buildClient(): IMoviepediaApiService =
        Retrofit.Builder()
                .baseUrl(BuildConfig.MOVIEPEDIA_API_URL)
                .client(OkHttpClient.Builder()
                        .addInterceptor(UserAgentInterceptor(USER_AGENT))
//                        .addInterceptor(ConnectivityInterceptor(context))
                        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                        .readTimeout(10, TimeUnit.SECONDS)
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()))
                .build()
                .create(IMoviepediaApiService::class.java)

private class UserAgentInterceptor(val userAgent: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val userAgentRequest: Request = request.newBuilder()
                .header("User-Agent", userAgent)
                .header("Client", "vlc-android")
                .header("Client-Version", BuildConfig.VLC_VERSION_CODE.toString())
                .header("Client-Type", BuildConfig.BUILD_TYPE)
                .build()
        return chain.proceed(userAgentRequest)
    }
}

interface MoviepediaApiClient {

    companion object {
        val instance = buildClient()
    }
}
