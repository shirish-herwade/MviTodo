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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        topBar = { TopAppBar(title = { Text("Todo Items") }) },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                BottomInputArea(onIntent)
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
                        TodoRow(todo, onIntent)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomInputArea(onIntent: (TodoIntent) -> Unit) {
    val title = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title.value,
            onValueChange = { title.value = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter todo title") }
        )

        Button(
            modifier = Modifier.padding(top = 8.dp),
            onClick = {
                handleSaveClick(title, onIntent)
            }
        ) {
            Text(text = "Save todo")
        }
    }
}

fun handleSaveClick(titleState: MutableState<String>, onIntent: (TodoIntent) -> Unit) {
    val title = titleState.value.trim()
    if (title.isNotBlank()) {
        onIntent(TodoIntent.SaveTodo(title))
        titleState.value = ""
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoRow(todo: Todo, onIntent: (TodoIntent) -> Unit) {
    Column(
        modifier = Modifier
            .combinedClickable(
                onClick = { },
                onLongClick = { onIntent(TodoIntent.Delete(todo)) }
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
                checked = todo.isDone,
                onCheckedChange = { isChecked ->
                    onIntent(TodoIntent.Update(todo.copy(isDone = isChecked)))
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
            Todo("Todo 1", isDone = false, id = 1),
            Todo("Todo 2", isDone = true, id = 2),
            Todo("Todo 3", isDone = false, id = 3)
        )
    )

    MviTodoTheme {
        MainScreen(
            state = mockState,
            onIntent = { }
        )
    }
}
