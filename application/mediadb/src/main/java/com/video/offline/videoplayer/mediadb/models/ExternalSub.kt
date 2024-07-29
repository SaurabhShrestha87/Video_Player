package com.video.offline.videoplayer.mediadb.models

import androidx.room.Entity

@Entity(tableName = "external_subtitles_table", primaryKeys = ["mediaPath", "idSubtitle"])
data class ExternalSub (
    val idSubtitle: String,
    val subtitlePath: String,
    val mediaPath: String,
    val subLanguageID: String,
    val movieReleaseName: String
)
