package org.ilerna.song_swipe_frontend.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.ilerna.song_swipe_frontend.data.repository.impl.CategoryRepositoryImpl
import org.ilerna.song_swipe_frontend.data.repository.impl.PlaylistRepositoryImpl
import org.ilerna.song_swipe_frontend.data.repository.impl.SpotifyRepositoryImpl
import org.ilerna.song_swipe_frontend.data.repository.impl.SupabaseAuthRepository
import org.ilerna.song_swipe_frontend.domain.repository.AuthRepository
import org.ilerna.song_swipe_frontend.domain.repository.CategoryRepository
import org.ilerna.song_swipe_frontend.domain.repository.PlaylistRepository
import org.ilerna.song_swipe_frontend.domain.repository.SpotifyRepository
import javax.inject.Singleton

/**
 * Hilt module for repository bindings.
 * 
 * Uses @Binds to map repository interfaces to their implementations.
 * This allows the domain layer to depend on abstractions while
 * Hilt provides the concrete implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds AuthRepository interface to SupabaseAuthRepository implementation.
     * Handles OAuth authentication via Supabase.
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: SupabaseAuthRepository
    ): AuthRepository

    /**
     * Binds SpotifyRepository interface to SpotifyRepositoryImpl implementation.
     * Handles Spotify user profile operations.
     */
    @Binds
    @Singleton
    abstract fun bindSpotifyRepository(
        impl: SpotifyRepositoryImpl
    ): SpotifyRepository

    /**
     * Binds PlaylistRepository interface to PlaylistRepositoryImpl implementation.
     * Handles Spotify playlist operations.
     */
    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(
        impl: PlaylistRepositoryImpl
    ): PlaylistRepository

    /**
     * Binds CategoryRepository interface to CategoryRepositoryImpl implementation.
     * Handles music category operations.
     */
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository
}
