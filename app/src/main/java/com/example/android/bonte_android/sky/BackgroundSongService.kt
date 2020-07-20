package com.example.android.bonte_android.sky

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.android.bonte_android.R

class BackgroundSongService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private var length = 0
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): BackgroundSongService = this@BackgroundSongService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.bonte_song)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.2f, 0.2f)
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    fun startSong() {
        mediaPlayer.start()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            length = mediaPlayer.currentPosition
        }
    }

    fun resumeMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(length)
            mediaPlayer.start()
        }
    }

    fun stopMusic() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

}