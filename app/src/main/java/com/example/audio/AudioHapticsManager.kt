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
import kotlin.math.sin

class AudioHapticsManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    fun playMoveX(soundEnabled: Boolean, hapticEnabled: Boolean) {
        if (hapticEnabled) triggerLightHaptic()
        if (soundEnabled) {
            scope.launch {
                playFrequencySweep(startFreq = 1200.0, endFreq = 1900.0, durationMs = 60, volume = 0.35f)
            }
        }
    }

    fun playMoveO(soundEnabled: Boolean, hapticEnabled: Boolean) {
        if (hapticEnabled) triggerLightHaptic()
        if (soundEnabled) {
            scope.launch {
                playFrequencySweep(startFreq = 780.0, endFreq = 540.0, durationMs = 75, volume = 0.35f)
            }
        }
    }

    fun playWin(soundEnabled: Boolean, hapticEnabled: Boolean) {
        if (hapticEnabled) triggerSuccessHaptic()
        if (soundEnabled) {
            scope.launch {
                // Ascending triumphant arpeggio: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
                val notes = listOf(523.25, 659.25, 783.99, 1046.50)
                for (note in notes) {
                    playSingleTone(freq = note, durationMs = 70, volume = 0.4f)
                }
            }
        }
    }

    fun playDraw(soundEnabled: Boolean, hapticEnabled: Boolean) {
        if (hapticEnabled) triggerLightHaptic()
        if (soundEnabled) {
            scope.launch {
                playSingleTone(freq = 440.0, durationMs = 80, volume = 0.3f)
                playSingleTone(freq = 330.0, durationMs = 120, volume = 0.25f)
            }
        }
    }

    fun playButtonClick(soundEnabled: Boolean, hapticEnabled: Boolean) {
        if (hapticEnabled) triggerLightHaptic()
        if (soundEnabled) {
            scope.launch {
                playSingleTone(freq = 950.0, durationMs = 15, volume = 0.2f)
            }
        }
    }

    private fun triggerLightHaptic() {
        try {
            vibrator?.let { v ->
                if (v.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(18)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    private fun triggerSuccessHaptic() {
        try {
            vibrator?.let { v ->
                if (v.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(
                            VibrationEffect.createWaveform(
                                longArrayOf(0, 35, 50, 60),
                                -1
                            )
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(longArrayOf(0, 35, 50, 60), -1)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    private fun playSingleTone(freq: Double, durationMs: Int, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Sine wave with exponential decay envelope to avoid audio clicks
            val envelope = (1.0 - (i.toDouble() / numSamples))
            val angle = 2.0 * Math.PI * freq * t
            buffer[i] = (sin(angle) * Short.MAX_VALUE * volume * envelope).toInt().toShort()
        }

        playPcmBuffer(buffer, sampleRate)
    }

    private fun playFrequencySweep(
        startFreq: Double,
        endFreq: Double,
        durationMs: Int,
        volume: Float
    ) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * Math.PI * currentFreq / sampleRate
            val envelope = if (progress < 0.1) progress / 0.1 else (1.0 - progress)
            buffer[i] = (sin(phase) * Short.MAX_VALUE * volume * envelope).toInt().toShort()
        }

        playPcmBuffer(buffer, sampleRate)
    }

    private fun playPcmBuffer(buffer: ShortArray, sampleRate: Int) {
        var audioTrack: AudioTrack? = null
        try {
            val bufferSize = buffer.size * 2
            audioTrack = AudioTrack.Builder()
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
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep((buffer.size.toDouble() / sampleRate * 1000).toLong() + 20)
        } catch (_: Exception) {
            // Ignore audio glitches safely
        } finally {
            try {
                audioTrack?.stop()
                audioTrack?.release()
            } catch (_: Exception) {}
        }
    }
}
