package com.cornai.data.remote

import com.cornai.data.model.ScanHistory

class FirebaseService {
    suspend fun saveScanHistory(userId: String, history: ScanHistory): Result<String> {
        return Result.success(history.id)
    }
}