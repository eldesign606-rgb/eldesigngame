package com.example.game

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class AudioSystem(private val scope: CoroutineScope) {

    var soundEnabled: Boolean = true
    var musicEnabled: Boolean = true
        set(value) {
            field = value
            if (value) startAmbientMusic() else stopAmbientMusic()
        }

    private var musicJob: Job? = null
    private val sampleRate = 22050

    fun playClick() {
        if (!soundEnabled) return
        playTone(frequency = 700.0, durationMs = 35, volume = 0.4f, type = WaveType.SINE)
    }

    fun playRotate() {
        if (!soundEnabled) return
        playTone(frequency = 440.0, durationMs = 45, volume = 0.35f, type = WaveType.TRIANGLE)
    }

    fun playCorrect() {
        if (!soundEnabled) return
        scope.launch(Dispatchers.Default) {
            playTone(frequency = 523.25, durationMs = 80, volume = 0.5f) // C5
            delay(80)
            playTone(frequency = 659.25, durationMs = 80, volume = 0.55f) // E5
            delay(80)
            playTone(frequency = 783.99, durationMs = 140, volume = 0.6f) // G5
        }
    }

    fun playWrong() {
        if (!soundEnabled) return
        scope.launch(Dispatchers.Default) {
            playTone(frequency = 220.0, durationMs = 120, volume = 0.55f, type = WaveType.SAWTOOTH)
            delay(100)
            playTone(frequency = 174.61, durationMs = 180, volume = 0.6f, type = WaveType.SAWTOOTH)
        }
    }

    fun playCoin() {
        if (!soundEnabled) return
        scope.launch(Dispatchers.Default) {
            playTone(frequency = 987.77, durationMs = 60, volume = 0.5f) // B5
            delay(50)
            playTone(frequency = 1318.51, durationMs = 120, volume = 0.55f) // E6
        }
    }

    fun playLevelComplete() {
        if (!soundEnabled) return
        scope.launch(Dispatchers.Default) {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            for ((idx, n) in notes.withIndex()) {
                playTone(frequency = n, durationMs = if (idx == 3) 280 else 100, volume = 0.65f)
                delay(95)
            }
        }
    }

    fun playAchievement() {
        if (!soundEnabled) return
        scope.launch(Dispatchers.Default) {
            val fanfare = listOf(440.0, 554.37, 659.25, 880.0) // A4, C#5, E5, A5
            for ((idx, n) in fanfare.withIndex()) {
                playTone(frequency = n, durationMs = if (idx == 3) 350 else 110, volume = 0.7f)
                delay(105)
            }
        }
    }

    fun startAmbientMusic() {
        if (!musicEnabled || musicJob?.isActive == true) return
        musicJob = scope.launch(Dispatchers.Default) {
            val ambientChords = listOf(
                listOf(261.63, 329.63, 392.00), // C Maj
                listOf(220.00, 261.63, 329.63), // A Min
                listOf(174.61, 220.00, 261.63), // F Maj
                listOf(196.00, 246.94, 293.66)  // G Maj
            )
            var chordIdx = 0
            while (isActive && musicEnabled) {
                val chord = ambientChords[chordIdx % ambientChords.size]
                chordIdx++
                for (note in chord) {
                    if (!isActive || !musicEnabled) break
                    playTone(frequency = note * 0.75, durationMs = 500, volume = 0.12f, type = WaveType.SINE)
                    delay(550)
                }
                delay(400)
            }
        }
    }

    fun stopAmbientMusic() {
        musicJob?.cancel()
        musicJob = null
    }

    private enum class WaveType { SINE, TRIANGLE, SAWTOOTH }

    private fun playTone(
        frequency: Double,
        durationMs: Int,
        volume: Float = 0.5f,
        type: WaveType = WaveType.SINE
    ) {
        try {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            if (numSamples <= 0) return
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val rawWave = when (type) {
                    WaveType.SINE -> sin(2.0 * PI * frequency * time)
                    WaveType.TRIANGLE -> {
                        val p = (time * frequency) % 1.0
                        if (p < 0.5) 4.0 * p - 1.0 else 3.0 - 4.0 * p
                    }
                    WaveType.SAWTOOTH -> {
                        2.0 * ((time * frequency) % 1.0) - 1.0
                    }
                }
                // Apply soft envelope (fade in / fade out to avoid clicks)
                val envelope = when {
                    i < numSamples * 0.1 -> i / (numSamples * 0.1)
                    i > numSamples * 0.8 -> (numSamples - i) / (numSamples * 0.2)
                    else -> 1.0
                }
                val sample = (rawWave * envelope * volume * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
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

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            // Release track after playback completes
            scope.launch(Dispatchers.Default) {
                delay(durationMs.toLong() + 50L)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Ignore audio initialization errors gracefully
        }
    }
}
