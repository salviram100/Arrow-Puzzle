package com.example.game.audio

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
import kotlin.math.sin

class SoundEffectsManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val audioScope = CoroutineScope(Dispatchers.Default)

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    fun playFlySound() {
        if (!soundEnabled) return
        audioScope.launch {
            // High speed ascending frequency whoosh
            generateAndPlayTone(startFreq = 440.0, endFreq = 980.0, durationMs = 120, volume = 0.6f)
        }
        triggerHaptic(HapticType.LIGHT)
    }

    fun playBlockedSound() {
        if (!soundEnabled) return
        audioScope.launch {
            // Low thud buzz
            generateAndPlayTone(startFreq = 160.0, endFreq = 90.0, durationMs = 160, volume = 0.7f, shape = WaveShape.SAWTOOTH)
        }
        triggerHaptic(HapticType.ERROR)
    }

    fun playTapSound() {
        if (!soundEnabled) return
        audioScope.launch {
            generateAndPlayTone(startFreq = 800.0, endFreq = 1200.0, durationMs = 35, volume = 0.4f)
        }
        triggerHaptic(HapticType.TICK)
    }

    fun playStarSound(starIndex: Int) {
        if (!soundEnabled) return
        audioScope.launch {
            val freq = when (starIndex) {
                1 -> 523.25 // C5
                2 -> 659.25 // E5
                else -> 783.99 // G5
            }
            generateAndPlayTone(startFreq = freq, endFreq = freq * 1.2, durationMs = 180, volume = 0.7f)
        }
        triggerHaptic(HapticType.MEDIUM)
    }

    fun playVictoryFanfare() {
        if (!soundEnabled) return
        audioScope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            for (note in notes) {
                generateAndPlayTone(startFreq = note, endFreq = note, durationMs = 100, volume = 0.65f)
                kotlinx.coroutines.delay(80)
            }
        }
        triggerHaptic(HapticType.HEAVY)
    }

    enum class HapticType {
        TICK, LIGHT, MEDIUM, HEAVY, ERROR
    }

    private fun triggerHaptic(type: HapticType) {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    HapticType.TICK -> VibrationEffect.createOneShot(12, 60)
                    HapticType.LIGHT -> VibrationEffect.createOneShot(25, 120)
                    HapticType.MEDIUM -> VibrationEffect.createOneShot(50, 180)
                    HapticType.HEAVY -> VibrationEffect.createOneShot(90, 255)
                    HapticType.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 30, 40, 30), intArrayOf(0, 180, 0, 180), -1)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    private enum class WaveShape { SINE, SAWTOOTH }

    private fun generateAndPlayTone(
        startFreq: Double,
        endFreq: Double,
        durationMs: Int,
        volume: Float = 0.5f,
        shape: WaveShape = WaveShape.SINE
    ) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            if (numSamples <= 0) return

            val buffer = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val phaseIncrement = 2.0 * Math.PI * currentFreq / sampleRate
                phase += phaseIncrement

                // Envelope: quick attack and smooth decay
                val envelope = when {
                    progress < 0.1 -> progress / 0.1
                    else -> 1.0 - ((progress - 0.1) / 0.9)
                }

                val sample = when (shape) {
                    WaveShape.SINE -> sin(phase)
                    WaveShape.SAWTOOTH -> (2.0 * (phase / (2.0 * Math.PI) - Math.floor(phase / (2.0 * Math.PI) + 0.5)))
                }

                buffer[i] = (sample * envelope * volume * Short.MAX_VALUE).toInt().toShort()
            }

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                AudioTrack(
                    android.media.AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    buffer.size * 2,
                    AudioTrack.MODE_STATIC
                )
            }

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep((durationMs + 30).toLong())
            audioTrack.release()
        } catch (_: Exception) {}
    }
}
