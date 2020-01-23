package com.zestworks.currencycoverterapplication.viewmodel

// Outputs
sealed class CurrencyUiModel {
    object LoadingCurrencyUiModel : CurrencyUiModel()
    data class SuccessCurrencyUiModel(val currencyList: List<Currency>) : CurrencyUiModel()
    data class ErrorCurrencyUiModel(val reason: String) : CurrencyUiModel()
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

