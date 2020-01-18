package com.zestworks.currencycoverterapplication.repository

import com.zestworks.currencycoverterapplication.network.NetworkResult

interface Repository {
    suspend fun getCurrencyData( baseCurrency : String = "EUR") : NetworkResult<CurrencyData>
}