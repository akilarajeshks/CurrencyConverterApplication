package com.zestworks.currencycoverterapplication.view

// Outputs
sealed class CurrencyViewData {
    object LoadingCurrencyViewData : CurrencyViewData()
    data class SuccessCurrencyViewData(val currencyList: List<Currency>) : CurrencyViewData()
    data class ErrorCurrencyViewData(val reason: String) : CurrencyViewData()
}

data class Currency(
        val name: String,
        val value: Double
)


// Inputs
sealed class UIEvent {
    data class StartEditUIEvent(val currencyName: String) : UIEvent()
    data class TextChangeUIEvent(val value: Double) : UIEvent()
}

