package com.example.project.util

import com.example.project.model.TransactionCategory
import org.springframework.stereotype.Component

@Component
class MerchantMCCMapping {
    private val merchantMCCMap: MutableMap<String, String> = mutableMapOf()
    private val merchantCategoryMap: MutableMap<String, String> = mutableMapOf()

    init {
        merchantMCCMap["PADARIA DO ZE SAO PAULO BR"] = "5811"
        merchantMCCMap["UBER TRIP SAO PAULO BR"] = "5811"
        merchantMCCMap["UBER EATS SAO PAULO BR"] = "5812"
        merchantMCCMap["PAG*JoseDaSilva RIO DE JANEI BR"] = "5411"
        merchantMCCMap["PICPAY*BILHETEUNICO GOIANIA BR"] = "5412"

    }

    fun getMCC(merchant: String, defaultMCC: String): String {
        return merchantMCCMap.getOrDefault(merchant, TransactionCategory.CASH.name)
    }

    fun getCategoryByMerchant(merchant: String): String {
        // Check if the merchant name contains "EATS"
        if (merchant.contains("EATS", ignoreCase = true)) {
            return TransactionCategory.FOOD.name
        }

        // Check if the merchant name contains "PADARIA"
        if (merchant.contains("PADARIA", ignoreCase = true)) {
            return TransactionCategory.MEAL.name
        }

        // Fallback to other merchant category mappings
        return merchantCategoryMap.entries.firstOrNull { merchant.contains(it.key, ignoreCase = true) }?.value
            ?: TransactionCategory.CASH.name
    }

    fun mapMCCToCategory(mcc: String): String {
        return when (mcc) {
            "5411", "5412" -> TransactionCategory.FOOD.name
            "5811", "5812" -> TransactionCategory.MEAL.name
            else -> TransactionCategory.CASH.name
        }
    }
}