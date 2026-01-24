package com.mvi.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mvi.todo.intent.TodoIntent
import com.mvi.todo.model.local.Todo
import com.mvi.todo.state.TodoState
import com.mvi.todo.ui.theme.MviTodoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MviTodoTheme {
                val todoViewModel: TodoViewModel = hiltViewModel()
                val state by todoViewModel.state.collectAsState()

                MainScreen(
                    state = state,
                    onIntent = todoViewModel::onIntent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: TodoState,
    onIntent: (TodoIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Todo Items")
                },
                actions = {
                    IconButton(
                        onClick = {
                            val hasSelection = state.items.any {
                                it.isSelected
                            }
                            if (hasSelection) {
                                onIntent(
                                    TodoIntent.ShowDeleteDialog(
                                        selection = TodoIntent.DeleteSelection.Selected
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete selected icons"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.maps_and_flags),
                            contentDescription = "Go to Map",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                BottomInputArea(state, onIntent)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.items.isEmpty()) {
                Text(
                    text = "Nothing found",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.items) { todo ->
                        TodoRow(state, todo, onIntent)
                    }
                }
            }
        }
    }
    ShowDeleteDialog(state, onIntent)
}

@Composable
fun ShowDeleteDialog(state: TodoState, onIntent: (TodoIntent) -> Unit) {
    var textToShow: String
    if (state.deleteSelection == TodoIntent.DeleteSelection.Single) {
        textToShow = "Do you want to delete this todo '${state.selectedTodo?.title}' ?"
    } else {
        textToShow = "Do you want to delete all selected todos ?"
    }

    if (state.showDelete) {
        AlertDialog(
            onDismissRequest = { onIntent(TodoIntent.DismissDeleteDialog) },
            title = { Text(text = "Delete Todo ?") },
            text = { Text(text = textToShow) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onIntent(TodoIntent.ConfirmDelete)
                    })
                { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = {
                    onIntent(TodoIntent.DismissDeleteDialog)
                })
                {
                    Text("Cancel")
                }
            })
    }
}


@Composable
fun BottomInputArea(state: TodoState, onIntent: (TodoIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = state.draftTitle,
            onValueChange = { onIntent(TodoIntent.ChangeDraftTitle(title = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter todo title") }
        )

        Button(
            modifier = Modifier.padding(top = 8.dp),
            onClick = {
                onIntent(TodoIntent.SubmitTodo)
            }
        ) {
            Text(text = "Save todo")
        }
    }
}

fun handleSaveClick(titleState: MutableState<String>, onIntent: (TodoIntent) -> Unit) {
    val title = titleState.value.trim()
    if (title.isNotBlank()) {
//        onIntent(TodoIntent.SaveTodo(title))
        titleState.value = ""
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoRow(state: TodoState, todo: Todo, onIntent: (TodoIntent) -> Unit) {
    Column(
        modifier = Modifier
            .combinedClickable(
                onClick = { },
                onLongClick = {
                    onIntent(
                        TodoIntent.ShowDeleteDialog(
                            todo = todo,
                            selection = TodoIntent.DeleteSelection.Single
                        )
                    )
                }
            )
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = todo.title)
            Checkbox(
                checked = todo.isSelected,
                onCheckedChange = { isChecked ->
                    onIntent(TodoIntent.Update(todo.copy(isSelected = isChecked)))
                }
            )
        }
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val mockState = TodoState(
        items = listOf(
            Todo("Todo 1", isSelected = false, id = 1),
            Todo("Todo 2", isSelected = true, id = 2),
            Todo("Todo 3", isSelected = false, id = 3)
        ),
        isLoading = false,
        error = null,
        draftTitle = "draft",
        showDelete = false,
//        selectedTodo = null
    )

    MviTodoTheme {
        MainScreen(
            state = mockState,
            onIntent = { }
        )
    }
}
