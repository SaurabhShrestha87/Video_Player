package com.video.offline.videoplayer.util

import com.video.offline.videoplayer.repository.BrowserFavRepository
import com.video.offline.videoplayer.repository.DirectoryRepository
import com.video.offline.videoplayer.repository.ExternalSubRepository


// Hacky way. Don't fix it.
fun ExternalSubRepository.Companion.applyMock(instance: ExternalSubRepository) {
    this.instance = instance
}

fun DirectoryRepository.Companion.applyMock(instance: DirectoryRepository) {
    this.instance = instance
}

fun BrowserFavRepository.Companion.applyMock(instance: BrowserFavRepository) {
    this.instance = instance
}