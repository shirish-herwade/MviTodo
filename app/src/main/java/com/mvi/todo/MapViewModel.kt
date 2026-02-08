package com.mvi.todo

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.mvi.todo.intent.MapIntent
import com.mvi.todo.state.MapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {

    private val _mapState = MutableStateFlow(MapState())
    val mapState = _mapState.asStateFlow()

//    val currentLocation = { mutableStateOf<LatLng?>(null) }

    init {
        startLocationUpdates()
    }

    fun onIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.RefreshLocation -> {
                if (_mapState.value.isPermissionGranted) {
                    startLocationUpdates()
                }
            }

            is MapIntent.PermissionResult -> {
                _mapState.update {
                    it.copy(isPermissionGranted = intent.isGranted)
                }
                if (intent.isGranted) {
                    startLocationUpdates()
                }
            }
        }
    }

    private fun startLocationUpdates() {
        viewModelScope.launch @androidx.annotation.RequiresPermission(
            allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION]
        ) {
            while (true) {
                try {
                    fusedLocationProviderClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    )
                        .addOnSuccessListener { location ->
                            location?.let {
                                _mapState.update {
                                    it.copy(
                                        lastLocation = LatLng(
                                            location.latitude,
                                            location.longitude
                                        )
                                    )
                                }
                            }
                        }
                        .addOnFailureListener {
                            //TODO Handle failure
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(60000)
            }
        }
    }
}