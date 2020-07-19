package net.dejanjokic.arsmovies

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.dejanjokic.arsmovies.util.log.HyperlinkTimberTree
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(HyperlinkTimberTree())
    }
}