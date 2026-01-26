package com.mvi.todo.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mvi.todo.MainScreen
import com.mvi.todo.MapScreen
import com.mvi.todo.TodoViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val todoViewModel: TodoViewModel = hiltViewModel()
            val state by todoViewModel.state.collectAsState()
            MainScreen(
                state = state,
                onIntent = todoViewModel::onIntent,
                onNavigateToMap = {
                    navController.navigate("map")
                }
            )
        }
        composable("map") {
            MapScreen(onBack = {
                navController.popBackStack()
            })
        }
    }
}