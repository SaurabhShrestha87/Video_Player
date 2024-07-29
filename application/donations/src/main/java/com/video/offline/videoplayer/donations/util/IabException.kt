package com.video.offline.videoplayer.donations.util

/**
 * Exception thrown when something went wrong with in-app billing.
 * An IabException has an associated IabResult (an error).
 * To get the IAB result that caused this exception to be thrown,
 * call [.getResult].
 */
class IabException @JvmOverloads constructor(val result: IabResult, cause: Exception? = null) : Exception(result.message, cause) {
    constructor(response: Int, message: String?) : this(IabResult(response, message))
    constructor(response: Int, message: String?, cause: Exception?) : this(IabResult(response, message), cause)
}