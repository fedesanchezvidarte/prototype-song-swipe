package org.ilerna.song_swipe_frontend

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Song Swipe.
 * 
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation,
 * including a base class for the application that serves as the
 * application-level dependency container.
 */
@HiltAndroidApp
class SongSwipeApplication : Application()
