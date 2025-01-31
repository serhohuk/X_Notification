package com.serhohuk.days.xnotification

import android.media.AudioManager
import android.media.MediaPlayer
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class XNotificationListener : NotificationListenerService() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val packageName = sbn?.packageName

        val notificationTitle = sbn?.notification?.extras?.getString("android.title")
        val notificationText = sbn?.notification?.extras?.getString("android.text")
        if (listOfAvailableApps.contains(packageName)
            && checkAuthor(notificationTitle?:"")
            && checkIfCryptoPost(notificationText?:"")
        ) {
            setVolumeToMax()
            startAudio()
            Log.d(this::class.java.simpleName, "packageName $packageName")
            Log.d(this::class.java.simpleName, "notificationTitle $notificationTitle")
            Log.d(this::class.java.simpleName, "notificationText $notificationText")
        }
    }

    private fun startAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        mediaPlayer = MediaPlayer.create(this, R.raw.money)
        mediaPlayer?.start()
    }

    private fun setVolumeToMax() {
        try {
            val audioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_SHOW_UI
            )
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, "VOLUME EXCEPTION")
        }
    }

    private fun checkIfCryptoPost(text: String): Boolean {
        if (text.contains("ICO") || text.contains("IDO")) return true

        val solanaAddressPattern = "\\b[1-9A-HJ-NP-Za-km-z]{32,44}\\b".toRegex()
        val defaultCryptoAddress = "\\b0x[a-fA-F0-9]{40}\\b".toRegex()
        val solanaMatches = solanaAddressPattern.findAll(text).map { it.value }.toList()
        val defaultMatches = defaultCryptoAddress.findAll(text).map { it.value }.toList()

        if (solanaMatches.isNotEmpty() && defaultMatches.isNotEmpty()) {
            return true
        }

        val lowCaseText = text.lowercase()
        return listOfKeyWords.contains(lowCaseText)
    }

    private fun checkAuthor(text: String): Boolean {
        if (keyAuthors.isEmpty()) return true

        return keyAuthors.contains(text)
    }

}

private const val TELEGRAM_PACKAGE = "org.telegram.messenger" // TO TEST
private const val TWITTER_PACKAGE = "com.twitter.android"

private val listOfAvailableApps = listOf(
    TELEGRAM_PACKAGE,
    TWITTER_PACKAGE
)

private val keyAuthors = listOf<String>(

)

private val listOfKeyWords = listOf(
    "coin",
    "meme",
    "token",
    "buy now",
    "buy",
    "launch",
    "presale"
)