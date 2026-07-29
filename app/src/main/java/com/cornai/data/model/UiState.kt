// Model state UI untuk menangani tampilan aplikasi.
// File: java\com\cornai\data\model\UiState.kt

package com.cornai.data.model

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
