package com.example.ui.util

import android.content.Context
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.View

object SoundManager {

    fun playKeyClick(view: View? = null, enabled: Boolean = true) {
        if (!enabled || view == null) return
        try {
            // Provide tactile haptic vibration
            view.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )

            // Play system key click audio
            val audioManager = view.context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            if (audioManager != null) {
                audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.4f)
            } else {
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }
        } catch (e: Exception) {
            // Ignore sound/haptic exceptions safely
        }
    }
}


