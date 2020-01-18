package com.zestworks.currencycoverterapplication.repository
import com.google.gson.annotations.SerializedName


data class CurrencyData(
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val rates: HashMap<String,Double>
)