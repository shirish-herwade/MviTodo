package com.mvi.todo.state

import com.google.android.gms.maps.model.LatLng

data class MapState(
    val lastLocation: LatLng? = null,
    val isPermissionGranted: Boolean = false
) {
}