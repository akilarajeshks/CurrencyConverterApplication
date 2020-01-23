package com.zestworks.currencycoverterapplication

import com.google.gson.Gson
import com.zestworks.currencycoverterapplication.repository.CurrencyData
import com.zestworks.currencycoverterapplication.view.Currency
import java.io.File


val dummySuccessDataEURBase: CurrencyData = Gson()
        .fromJson(File("src/test/java/com/zestworks/currencycoverterapplication/dummydata.json")
                .bufferedReader()
                .use { it.readText() }
                , CurrencyData::class.java)

val dummySuccessDataINRBase: CurrencyData = Gson()
        .fromJson(File("src/test/java/com/zestworks/currencycoverterapplication/dummydataINR.json")
                .bufferedReader()
                .use { it.readText() }
                , CurrencyData::class.java)

val dummyListOfCurrencyData = mutableListOf(
        Currency(name = dummySuccessDataEURBase.base, value = 1.0)
).apply {
    addAll(dummySuccessDataEURBase.rates.entries.map { Currency(it.key, it.value) })
}

val dummyListOfDataAfterChangeBy2 = mutableListOf<Currency>().apply {
    addAll(dummyListOfCurrencyData.map { Currency(it.name, it.value.times(2.0)) })
}

val dummyListAfterClickingINR = mutableListOf<Currency>().apply {
    add(Currency("INR", dummyListOfCurrencyData.find { it.name == "INR" }!!.value))
    add(Currency(dummyListOfCurrencyData.first().name, dummyListOfCurrencyData.first().value))
    addAll(dummySuccessDataEURBase.rates.entries.filter { it.key != "INR" }.map { Currency(it.key, it.value) })
}
const val errorMessage = "Network Failed"

