package com.infiniwaresolutions.thehelpingfriendlyapp.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleCollection {

    private const val BASE_URL: String = "https://api.phish.net/v5/"

    @Provides
    @Singleton
    @Named("AuthInterceptor")
    fun provideAuthInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder().build()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    @Named("ConnectivityInterceptor")
    fun provideConnectivityInterceptor(@ApplicationContext context: Context): Interceptor =
        Interceptor { chain ->
            if (!isNetworkAvailable(context)) {
                throw NoConnectivityException("No internet connection")
            }
            chain.proceed(chain.request())
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("AuthInterceptor") authInterceptor: Interceptor,
        @Named("ConnectivityInterceptor") connectivityInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(connectivityInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .retryOnConnectionFailure(true) // Automatically retry on network failures
            .connectTimeout(30, TimeUnit.SECONDS) // Customize timeouts as needed
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): DotNetApiService {
        return retrofit.create(DotNetApiService::class.java)
    }
}

// Helper function to check network availability
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// Custom exception class for network connectivity issues
class NoConnectivityException(message: String) : IOException(message)
