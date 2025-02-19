package com.ajverma.jetfoodapp.domain.utils

object StringUtils {

    fun formatCurrency(amount: Double): String {
        val currencyFormat = java.text.NumberFormat.getCurrencyInstance()
        currencyFormat.currency = java.util.Currency.getInstance("USD")
        return currencyFormat.format(amount)
    }
}