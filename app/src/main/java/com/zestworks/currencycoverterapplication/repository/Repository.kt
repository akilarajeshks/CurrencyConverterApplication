package com.zestworks.currencycoverterapplication.repository

import com.zestworks.currencycoverterapplication.repository.network.NetworkResult

interface Repository {
    suspend fun getCurrencyData( baseCurrency : String) : NetworkResult<CurrencyData>
}