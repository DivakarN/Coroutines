package com.sysaxiom.coroutines.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitHandler {

    companion object{

        operator fun invoke( networkConnectionInterceptor: NetworkConnectionInterceptor) : NetworkApis {

            val okHttpclient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://spotrush.net/api/v1/")
                .client(okHttpclient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NetworkApis::class.java)

        }
    }
}


class NetworkConnectionInterceptor(context: Context) : Interceptor {

    private val applicationContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable())
            throw NoInternetException("No Internet")
        return chain.proceed(chain.request())
    }

    @Suppress("DEPRECATION")
    private fun isInternetAvailable(): Boolean {
        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT > 22) {
            val an = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(an) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val a = cm.activeNetworkInfo ?: return false
            a.isConnected && (a.type == ConnectivityManager.TYPE_WIFI || a.type == ConnectivityManager.TYPE_MOBILE)
        }
    }
}
