package com.video.offline.videoplayer.util

import android.content.Context
import com.video.offline.videoplayer.R

object TextUtils {

    /**
     * Common string separator used in the whole app
     */
    const val separator = '·'

    /**
     * Create a string separated by the common [separator]
     *
     * @param pieces the strings to join
     * @return a string containing all the [pieces] if they are not blanked, separated by the [separator]
     */
    @JvmName("separatedStringArgs")
    fun separatedString(vararg pieces: String?) = separatedString(this.separator, arrayOf(*pieces))

    /**
     * Create a string separated by the common [separator]
     *
     * @param pieces the strings to join in an [Array]
     * @return a string containing all the [pieces] if they are not blanked, separated by the [separator]
     */
    fun separatedString(pieces: Array<String?>) = separatedString(this.separator, pieces)

    /**
     * Create a string separated by a custom [separator]
     *
     * @param pieces the strings to join
     * @return a string containing all the [pieces] if they are not blanked, separated by [separator]
     */
    @JvmName("separatedStringArgs")
    fun separatedString(separator: Char, vararg pieces: String?) = separatedString(separator, arrayOf(*pieces))

    /**
     * Create a string separated by a custom [separator]
     *
     * @param pieces the strings to join in an [Array]
     * @return a string containing all the [pieces] if they are not blanked, separated by [separator]
     */
    fun separatedString(separator: Char, pieces: Array<String?>) = pieces.filter { it?.isNotBlank() == true }.joinToString(separator = " $separator ")

    /**
     * Format the chapter title.
     * If title is null return "Chapter: <num>"
     * If title contains letters only prepend "Chapter: <title>"
     * If title contains any non alpha characters return as-is
     *
     * @param context the context to use to retrieve the string
     * @param chapterNum the current chapter number
     * @param title the title to format
     * @return a formatted string
     */
    fun formatChapterTitle(context: Context, chapterNum: Int, title: String?): String {
        return when {
            title.isNullOrBlank() -> context.getString(R.string.current_chapter, chapterNum.toString())
            title.all { it.isLetter() } -> context.getString(R.string.current_chapter, title)
            else -> title
        }
    }
}