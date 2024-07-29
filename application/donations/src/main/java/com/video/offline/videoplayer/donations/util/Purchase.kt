package com.video.offline.videoplayer.donations.util

import org.json.JSONObject

/**
 * Represents an in-app billing purchase.
 */
class Purchase(var itemType: String, private var originalJson: String, signature: String) {
    var orderId: String
    var packageName: String
    var sku: String
    var purchaseTime: Long
    var purchaseState: Int
    var developerPayload: String
    var token: String
    var signature: String
    var isAutoRenewing: Boolean

    override fun toString(): String {
        return "PurchaseInfo(type:$itemType):$originalJson"
    }

    init {
        val o = JSONObject(originalJson)
        orderId = o.optString("orderId")
        packageName = o.optString("packageName")
        sku = o.optString("productId")
        purchaseTime = o.optLong("purchaseTime")
        purchaseState = o.optInt("purchaseState")
        developerPayload = o.optString("developerPayload")
        token = o.optString("token", o.optString("purchaseToken"))
        isAutoRenewing = o.optBoolean("autoRenewing")
        this.signature = signature
    }
}