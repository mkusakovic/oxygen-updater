package com.arjanvlek.oxygenupdater.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Device(
    val id: Long,
    var enabled: Boolean,
    val name: String?,

    @JsonIgnore
    private val productName: String
) {

    lateinit var productNames: List<String>

    /**
     * Treat device as enabled by default, for backwards compatibility
     *
     * @param id          the device ID
     * @param name        the device name
     * @param productName the device name (ro.product.name)
     */
    constructor(id: Long, name: String?, productName: String) : this(id, true, name, productName)

    @JsonProperty("enabled")
    fun setEnabled(enabled: String?) {
        this.enabled = enabled != null && enabled == "1"
    }

    @JsonProperty("product_names")
    fun setProductName(productName: String) {
        productNames = getProductNames(productName)
    }

    fun getProductNames(productNameTemplate: String): List<String> {
        return productNameTemplate.trim { it <= ' ' }.split(",")
            // Remove spaces after comma separation.
            .map { productName -> productName.trim { it <= ' ' } }
    }

    init {
        setProductName(productName)
    }
}