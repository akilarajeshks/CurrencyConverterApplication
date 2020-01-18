package com.zestworks.currencycoverterapplication

import com.google.gson.Gson
import com.zestworks.currencycoverterapplication.repository.CurrencyData
import com.zestworks.currencycoverterapplication.view.Currency
import java.io.File


val dummySuccessData = Gson()
        .fromJson(File("src/test/java/com/zestworks/currencycoverterapplication/dummydata.json")
                .bufferedReader()
                .use { it.readText() }
                , CurrencyData::class.java)

val dummyListOfCurrencyData = mutableListOf(
        Currency(name = dummySuccessData.base, value = 1.0)
).apply {
    addAll(dummySuccessData.rates.entries.map { Currency(it.key, it.value) })
}
const val errorMessage = "Network Failed"

