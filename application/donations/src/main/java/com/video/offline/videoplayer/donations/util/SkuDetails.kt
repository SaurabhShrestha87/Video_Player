package com.video.offline.videoplayer.donations.util

import org.json.JSONObject

/**
 * Represents an in-app product's listing details.
 */
class SkuDetails(private val mItemType: String, private val mJson: String) {
    val sku: String
    val type: String
    val price: String
    val priceAmountMicros: Long
    val priceCurrencyCode: String
    val title: String
    val description: String

    constructor(jsonSkuDetails: String) : this(IabHelper.ITEM_TYPE_INAPP, jsonSkuDetails) {}

    override fun toString(): String {
        return "SkuDetails:$mJson"
    }

    init {
        val o = JSONObject(mJson)
        sku = o.optString("productId")
        type = o.optString("type")
        price = o.optString("price")
        priceAmountMicros = o.optLong("price_amount_micros")
        priceCurrencyCode = o.optString("price_currency_code")
        title = o.optString("title")
        description = o.optString("description")
    }
}