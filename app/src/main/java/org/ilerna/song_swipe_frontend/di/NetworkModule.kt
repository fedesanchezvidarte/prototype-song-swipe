package org.ilerna.song_swipe_frontend.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.ilerna.song_swipe_frontend.core.network.interceptors.SpotifyAuthInterceptor
import org.ilerna.song_swipe_frontend.data.datasource.remote.api.SpotifyApi
import org.ilerna.song_swipe_frontend.domain.repository.AuthRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for network-related dependencies.
 * 
 * Provides OkHttpClient, Retrofit, and API interface instances
 * configured for Spotify Web API communication.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val SPOTIFY_BASE_URL = "https://api.spotify.com/"

    /**
     * Provides the HttpLoggingInterceptor for debugging HTTP requests.
     * Logs request/response bodies in debug builds.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Provides the SpotifyAuthInterceptor.
     * Automatically injects Bearer token and handles 401 token refresh.
     */
    @Provides
    @Singleton
    fun provideSpotifyAuthInterceptor(
        authRepository: AuthRepository
    ): SpotifyAuthInterceptor {
        return SpotifyAuthInterceptor(authRepository)
    }

    /**
     * Provides the configured OkHttpClient for Spotify API.
     * Includes auth interceptor and logging interceptor.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        spotifyAuthInterceptor: SpotifyAuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(spotifyAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provides the Retrofit instance configured for Spotify API.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SPOTIFY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides the SpotifyApi interface implementation.
     * Created by Retrofit from the interface definition.
     */
    @Provides
    @Singleton
    fun provideSpotifyApi(retrofit: Retrofit): SpotifyApi {
        return retrofit.create(SpotifyApi::class.java)
    }
}
