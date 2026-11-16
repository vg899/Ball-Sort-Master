package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundManager {

    private const val SAMPLE_RATE = 22050
    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true

    private var musicJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Synthesizes and plays a tone or chord directly via AudioTrack.
     */
    private fun playTone(frequencies: List<Double>, durationMs: Int, volume: Float = 0.5f, decay: Boolean = true) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / SAMPLE_RATE
                    val envelope = if (decay) {
                        (1.0 - (i.toDouble() / numSamples)).coerceIn(0.0, 1.0)
                    } else 1.0

                    var sampleSum = 0.0
                    for (freq in frequencies) {
                        sampleSum += sin(2.0 * Math.PI * freq * time)
                    }
                    val normalized = (sampleSum / frequencies.size) * envelope * volume
                    buffer[i] = (normalized * Short.MAX_VALUE).toInt().toShort()
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
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()

                delay(durationMs.toLong() + 50)
                track.stop()
                track.release()
            } catch (_: Exception) {
                // Ignore audio hardware errors
            }
        }
    }

    /**
     * Ball selected / lift sound (gentle upwards pop).
     */
    fun playBallLift() {
        playTone(listOf(523.25, 659.25), durationMs = 90, volume = 0.4f) // C5 + E5
    }

    /**
     * Ball drop / tube placement sound (satisfying lower pop).
     */
    fun playBallDrop() {
        playTone(listOf(392.0, 440.0), durationMs = 110, volume = 0.45f) // G4 + A4
    }

    /**
     * Invalid move buzz.
     */
    fun playInvalidMove() {
        playTone(listOf(180.0, 220.0), durationMs = 120, volume = 0.3f)
    }

    /**
     * Standard UI button click.
     */
    fun playButtonClick() {
        playTone(listOf(880.0), durationMs = 50, volume = 0.25f)
    }

    /**
     * Coin collection chime.
     */
    fun playCoinSound() {
        scope.launch {
            playTone(listOf(987.77), durationMs = 80, volume = 0.4f) // B5
            delay(70)
            playTone(listOf(1318.51), durationMs = 150, volume = 0.5f) // E6
        }
    }

    /**
     * Normal level completed fanfare!
     */
    fun playLevelWin() {
        scope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            for (note in notes) {
                playTone(listOf(note), durationMs = 120, volume = 0.5f)
                delay(90)
            }
        }
    }

    /**
     * Grand Final celebration sound (extended majestic chord arpeggio).
     */
    fun playGrandFinalCelebration() {
        scope.launch {
            val notes = listOf(440.0, 554.37, 659.25, 880.0, 1108.73, 1318.51)
            for (note in notes) {
                playTone(listOf(note, note * 1.5), durationMs = 200, volume = 0.6f)
                delay(140)
            }
        }
    }

    /**
     * Background ambient calming synth loop.
     */
    fun startBackgroundMusic() {
        if (musicJob != null || !isMusicEnabled) return
        musicJob = scope.launch {
            val ambientChords = listOf(
                listOf(261.63, 329.63, 392.0), // C major
                listOf(220.0, 261.63, 329.63),  // A minor
                listOf(174.61, 220.0, 261.63),  // F major
                listOf(196.0, 246.94, 293.66)   // G major
            )
            var index = 0
            while (isActive && isMusicEnabled) {
                playTone(ambientChords[index], durationMs = 1800, volume = 0.08f, decay = true)
                delay(2200)
                index = (index + 1) % ambientChords.size
            }
        }
    }

    fun stopBackgroundMusic() {
        musicJob?.cancel()
        musicJob = null
    }

    fun setMusic(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) {
            startBackgroundMusic()
        } else {
            stopBackgroundMusic()
        }
    }
}
