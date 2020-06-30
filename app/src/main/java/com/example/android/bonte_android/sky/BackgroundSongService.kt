package com.example.android.bonte_android.sky

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.example.android.bonte_android.R

class BackgroundSongService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private var length = 0

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.bonte_song)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(20f, 20f)
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer.start()
        Log.d("test", "test")
        return startId
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