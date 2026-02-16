package com.mvi.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.mvi.todo.intent.MapIntent
import com.mvi.todo.state.MapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var locationJob: Job? = null

    fun onIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.RefreshLocation -> {
                if (_mapState.value.isPermissionGranted) {
                    startLocationUpdates()
                }
            }

            is MapIntent.PermissionResult -> {
                val wasGranted = _mapState.value.isPermissionGranted
                _mapState.update {
                    it.copy(isPermissionGranted = intent.isGranted)
                }
                // Only start updates if permission is newly granted or not already running
                if (intent.isGranted && (locationJob == null || !locationJob!!.isActive)) {
                    startLocationUpdates()
                } else if (!intent.isGranted) {
                    locationJob?.cancel()
                    locationJob = null
                }
            }
        }
    }

    private fun startLocationUpdates() {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
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
                            // Handle failure
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(60000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }
}
