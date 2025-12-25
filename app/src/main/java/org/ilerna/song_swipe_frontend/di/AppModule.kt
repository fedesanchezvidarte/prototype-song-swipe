package org.ilerna.song_swipe_frontend.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import org.ilerna.song_swipe_frontend.core.config.SupabaseConfig
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISettingsDataStore
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISpotifyTokenDataStore
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.SettingsDataStore
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.SpotifyTokenDataStore
import javax.inject.Singleton

/**
 * Hilt module for application-level dependencies.
 * 
 * Provides Context and DataStore instances that need to be singletons
 * throughout the application lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the SupabaseClient singleton.
     * Used for Supabase Auth and other Supabase services.
     */
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return SupabaseConfig.client
    }

    /**
     * Provides the SettingsDataStore as a singleton.
     * Manages app settings like theme mode.
     */
    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): ISettingsDataStore {
        return SettingsDataStore(context)
    }

    /**
     * Provides the SpotifyTokenDataStore as a singleton.
     * Manages Spotify OAuth token persistence.
     */
    @Provides
    @Singleton
    fun provideSpotifyTokenDataStore(
        @ApplicationContext context: Context
    ): ISpotifyTokenDataStore {
        return SpotifyTokenDataStore(context)
    }
}
