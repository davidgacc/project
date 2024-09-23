package com.example.project.util

import com.example.project.service.TransactionService
import org.springframework.stereotype.Component

@Component
class MerchantMCCMapping {
    private val merchantMCCMap: MutableMap<String, String> = mutableMapOf()

    init {
        merchantMCCMap["PADARIA DO ZE SAO PAULO BR"] = "5811"
        merchantMCCMap["UBER TRIP SAO PAULO BR"] = "5811"
        merchantMCCMap["UBER EATS SAO PAULO BR"] = "5812"
        merchantMCCMap["PAG*JoseDaSilva RIO DE JANEI BR"] = "5411"
        merchantMCCMap["PICPAY*BILHETEUNICO GOIANIA BR"] = "5412"
    }

    fun getMCC(merchant: String, defaultMCC: String): String {
        return merchantMCCMap.getOrDefault(merchant, TransactionService.TransactionCategory.CASH.name)
    }
}