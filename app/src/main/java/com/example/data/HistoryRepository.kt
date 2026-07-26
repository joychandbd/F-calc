package com.example.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: CalculationHistoryDao) {
    val allHistory: Flow<List<CalculationHistoryEntity>> = dao.getAllHistory()

    suspend fun insert(expression: String, result: String, feetInchesResult: String, note: String? = null) {
        if (expression.isBlank() || result == "Error") return
        val entity = CalculationHistoryEntity(
            expression = expression,
            result = result,
            feetInchesResult = feetInchesResult,
            note = note
        )
        dao.insertHistory(entity)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteHistoryById(id)
    }

    suspend fun clearAll() {
        dao.clearAllHistory()
    }
}
