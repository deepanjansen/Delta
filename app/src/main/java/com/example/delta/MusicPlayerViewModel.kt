package com.example.delta

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.State


class MusicPlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val functionality = Functionality(application.applicationContext)

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _currentPosition = mutableStateOf(0f)
    val currentPosition: State<Float> = _currentPosition

    private val _totalDuration = mutableStateOf(1f)
    val totalDuration: State<Float> = _totalDuration

    private val _songName = mutableStateOf("Unknown Song")
    val songName: State<String> = _songName

    private val _isShuffling = mutableStateOf(false)
    val isShuffling: State<Boolean> = _isShuffling

    fun play() {
        functionality.playSongs()
        _songName.value = functionality.getCurrentSongName()
        _isPlaying.value = true
    }

    fun pause() {
        functionality.pause()
        _isPlaying.value = false
    }

    fun resume() {
        functionality.resume()
        _isPlaying.value = true
    }

    fun seekTo(position: Float) {
        functionality.seekTo(position.toInt())
        _currentPosition.value = position
    }

    fun updateProgress() {
        _currentPosition.value = functionality.getCurrentPosition().toFloat()
        _totalDuration.value = functionality.getDuration().toFloat()
    }

    fun nextSong() {
        functionality.next()
        _songName.value = functionality.getCurrentSongName()
        _currentPosition.value = 0f
        _isPlaying.value = true
    }

    fun previousSong() {
        functionality.previous()
        _songName.value = functionality.getCurrentSongName()
        _currentPosition.value = 0f
        _isPlaying.value = true
    }

    fun toggleShuffle() {
        functionality.toggleShuffle()
        _isShuffling.value = functionality.isShuffling
    }
}
