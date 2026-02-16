package com.mvi.todo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mvi.todo.intent.MapIntent
import com.mvi.todo.state.MapState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

fun checkLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    state: MapState,
    onIntent: (MapIntent) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isPreview = LocalInspectionMode.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            onIntent(MapIntent.PermissionResult(isGranted))
        }
    )

    // Initial check and request
    LaunchedEffect(Unit) {
        if (!isPreview) {
            val isGranted = checkLocationPermission(context)
            onIntent(MapIntent.PermissionResult(isGranted))
            if (!isGranted) {
                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // Remember a single MapView reference for lifecycle handling but let AndroidView create/manage the actual view attachment.
    val mapRef = remember { mutableStateOf<MapView?>(null) }

    // Handle Lifecycle and "Return from Settings" using the remembered map reference
    DisposableEffect(lifecycleOwner, mapRef.value) {
        val mapView = mapRef.value
        if (mapView == null) return@DisposableEffect onDispose {}

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    val isGranted = checkLocationPermission(context)
                    onIntent(MapIntent.PermissionResult(isGranted))
                    mapView.onResume()
                }

                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isPreview) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Map View (Preview Mode)")
                }
            } else {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        // Create the MapView here so AndroidView manages its attachment lifecycle.
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            // store reference for lifecycle handling
                            mapRef.value = this
                        }
                    },
                    update = { view ->
                        state.lastLocation?.let {
                            val point = GeoPoint(it.latitude, it.longitude)

                            with(view) {
                                // ensure we don't repeatedly re-center if already set
                                if (zoomLevelDouble == 0.0) {
                                    controller.setZoom(18.0)
                                    controller.setCenter(point)
                                }
                                // clear overlays before adding (prevents layering markers)
                                overlays.clear()
                                val marker = Marker(view).apply {
                                    position = point
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = "Current location"
                                }
                                overlays.add(marker)
                                invalidate()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun MapPreview() {
    MapScreen(
        state = MapState(),
        onIntent = {},
        onBack = {}
    )
}
