package com.mvi.todo.intent

sealed class MapIntent {
    object RefreshLocation : MapIntent()
    data class PermissionResult(val isGranted: Boolean) : MapIntent()
}