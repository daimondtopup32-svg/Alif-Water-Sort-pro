package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    var isSoundEnabled: Boolean = true
    var isVibrationEnabled: Boolean = true
    // Master volume set to soft, gentle level (0.22f) as requested ("স্পিকারও কম হবে")
    var masterVolume: Float = 0.22f

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private fun playPcmTone(sampleRate: Int = 22050, durationMs: Int, generator: (timeSec: Double) -> Double) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    // Apply masterVolume for soft, soothing audio output
                    val sample = (generator(time).coerceIn(-1.0, 1.0) * masterVolume * Short.MAX_VALUE).toInt()
                    buffer[i] = sample.toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                // Auto release after duration
                kotlinx.coroutines.delay(durationMs.toLong() + 100)
                track.stop()
                track.release()
            } catch (_: Exception) {
                // Ignore audio error gracefully
            }
        }
    }

    fun playTap() {
        triggerHaptic(HapticType.LIGHT)
        // Gentle, soft crystal glass tap ping (1100 Hz with fast smooth decay)
        playPcmTone(durationMs = 80) { t ->
            val freq = 1100.0
            val decay = (1.0 - t / 0.08).coerceAtLeast(0.0)
            sin(2.0 * PI * freq * t) * (decay * decay) * 0.25
        }
    }

    fun playPour() {
        triggerHaptic(HapticType.MEDIUM)
        // Gentle soothing water stream bubbling frequency wobble
        playPcmTone(durationMs = 400) { t ->
            val wobble = sin(2.0 * PI * 14.0 * t) * 40.0
            val freq = 380.0 + wobble + (t * 120.0)
            val envelope = (1.0 - (t / 0.40)).coerceAtLeast(0.0)
            val bubble = sin(2.0 * PI * 55.0 * t) * 0.15
            (sin(2.0 * PI * freq * t) + bubble) * envelope * 0.22
        }
    }

    fun playCompleteTube() {
        triggerHaptic(HapticType.SUCCESS)
        // Soft ascending sparkle chime (C6, E6, G6)
        playPcmTone(durationMs = 380) { t ->
            val note = when {
                t < 0.12 -> 1046.50 // C6
                t < 0.24 -> 1318.51 // E6
                else -> 1567.98     // G6
            }
            val localT = when {
                t < 0.12 -> t
                t < 0.24 -> t - 0.12
                else -> t - 0.24
            }
            val decay = (1.0 - (localT / 0.16)).coerceIn(0.0, 1.0)
            sin(2.0 * PI * note * t) * (decay * decay) * 0.28
        }
    }

    fun playLevelWin() {
        triggerHaptic(HapticType.SUCCESS)
        // Gentle harmonious fanfare chord
        playPcmTone(durationMs = 600) { t ->
            val note = when {
                t < 0.13 -> 523.25 // C5
                t < 0.26 -> 659.25 // E5
                t < 0.39 -> 783.99 // G5
                else -> 1046.50    // C6
            }
            val decay = (1.0 - (t / 0.60)).coerceIn(0.0, 1.0)
            sin(2.0 * PI * note * t) * decay * 0.30
        }
    }

    fun playError() {
        triggerHaptic(HapticType.ERROR)
        // Soft low double-thud
        playPcmTone(durationMs = 110) { t ->
            val freq = 160.0
            val decay = (1.0 - t / 0.11).coerceAtLeast(0.0)
            sin(2.0 * PI * freq * t) * decay * 0.20
        }
    }

    enum class HapticType {
        LIGHT, MEDIUM, SUCCESS, ERROR
    }

    fun triggerHaptic(type: HapticType) {
        if (!isVibrationEnabled) return
        try {
            val v = vibrator ?: return
            if (!v.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (type) {
                    HapticType.LIGHT -> v.vibrate(VibrationEffect.createOneShot(12, 100))
                    HapticType.MEDIUM -> v.vibrate(VibrationEffect.createOneShot(24, 140))
                    HapticType.SUCCESS -> {
                        val timings = longArrayOf(0, 25, 50, 30)
                        val amplitudes = intArrayOf(0, 140, 0, 180)
                        v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    HapticType.ERROR -> {
                        val timings = longArrayOf(0, 30, 40, 30)
                        val amplitudes = intArrayOf(0, 120, 0, 120)
                        v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    HapticType.LIGHT -> v.vibrate(12)
                    HapticType.MEDIUM -> v.vibrate(24)
                    HapticType.SUCCESS -> v.vibrate(50)
                    HapticType.ERROR -> v.vibrate(longArrayOf(0, 30, 40, 30), -1)
                }
            }
        } catch (_: Exception) {
            // Ignore vibration failure
        }
    }
}
