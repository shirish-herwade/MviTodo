package com.mvi.todo

//import com.mvi.todo.ToDoViewModel
import app.cash.turbine.test
import com.mvi.todo.model.local.Todo
import com.mvi.todo.model.repository.TodoRepository
import com.mvi.todo.state.TodoState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

//@RunWith(MockitoJUnitRunner::class)
class TodoViewModelTest {

    private val repository = mockk<TodoRepository>(relaxed = true)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TodoViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun before() {
        // Add these two lines to mock and redirect Dispatchers.IO
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns mainDispatcherRule.testDispatcher

        every {
            repository.getAllTodoListFlow()
        } returns flowOf(getTodoList())

        viewModel = TodoViewModel(
            repository,
//            testDispatcher
        )
    }

    @Test
    fun `selected todos get deleted`() = runTest {
//        every { viewModel.state.value } returns TodoState()

        viewModel.deleteSelected()
        coVerify {
            repository.deleteTodos(any())
        }
//        cancelAndIgnoreRemainingEvents()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getState repository flow collection`() = runTest()
    {
        val todoFlow = MutableSharedFlow<List<Todo>>(replay = 1)
        every { repository.getAllTodoListFlow() } returns todoFlow

        val viewModel = TodoViewModel(repository)

        viewModel.state.test {
            assertEquals(true, awaitItem().isLoading)

            val newData = listOf(Todo("New todo", 1, false))
            todoFlow.emit(newData)

            // 3. Verify state updates immediately
            val finalState = awaitItem()
            assertEquals(newData, finalState.items)
            assertEquals(false, finalState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `getState repository flow collection copilot`() = runTest {
//        val testDispatcher = StandardTestDispatcher(testScheduler)
//        Dispatchers.setMain(testDispatcher)
//
//        val todoFlow = MutableSharedFlow<List<Todo>>()
//        every { repository.getAllTodoListFlow() } returns todoFlow
//
//        val viewModel = TodoViewModel(repository)
//
//        viewModel.state.test {
//            val initialState = awaitItem()
//            assertEquals(false, initialState.isLoading)
//
//            // start collection / background work
//            testScheduler.advanceUntilIdle()
//
//            val loadingState = awaitItem()
//            assertEquals(true, loadingState.isLoading)
//
//            val newData = listOf(Todo("New todo", 1, false))
//            todoFlow.emit(newData)
//
//            // allow emission handling to run
//            testScheduler.advanceUntilIdle()
//
//            val finalState = awaitItem()
//            assertEquals(newData, finalState.items)
//            assertEquals(false, finalState.isLoading)
//
//            cancelAndIgnoreRemainingEvents()
//        }
//    }

    @Test
    fun `onIntent ChangeDraftTitle update`() {
        // Verify that passing ChangeDraftTitle intent correctly updates the draftTitle field in the current state.
        // TODO implement test
    }

    @Test
    fun `onIntent Delete repository call`() {
        // Verify that passing TodoIntent.Delete triggers the repository.delete method with the correct todo object.
        // TODO implement test
    }

    @Test
    fun `onIntent Insert repository call`() {
        // Verify that passing TodoIntent.Insert triggers the repository.insert method with the correct todo object.
        // TODO implement test
    }

    @Test
    fun `onIntent Update repository call`() {
        // Verify that passing TodoIntent.Update triggers the repository.update method with the correct todo object.
        // TODO implement test
    }

    @Test
    fun `onIntent SubmitTodo with valid title`() {
        // Verify that SubmitTodo inserts a new Todo and clears the draftTitle when draftTitle is not blank.
        // TODO implement test
    }

    @Test
    fun `onIntent SubmitTodo with blank title`() {
        // Verify that SubmitTodo does not call repository.insert and does not clear draftTitle if the title is empty or whitespace.
        // TODO implement test
    }

    @Test
    fun `onIntent ConfirmDelete Single selection`() {
        // Verify repository.delete is called with selectedTodo and state is reset when deleteSelection is Single.
        // TODO implement test
    }

    @Test
    fun `onIntent ConfirmDelete Single null check`() {
        // Verify repository.delete is NOT called and state is still reset if deleteSelection is Single but selectedTodo is null.
        // TODO implement test
    }

    @Test
    fun `onIntent ConfirmDelete Selected items`() {
        // Verify repository.deleteTodos is called with all items where isSelected is true and state is reset.
        // TODO implement test
    }

    @Test
    fun `onIntent ConfirmDelete Selected empty check`() {
        // Verify repository.deleteTodos is NOT called if deleteSelection is Selected but no items in the list are currently selected.
        // TODO implement test
    }

    @Test
    fun `onIntent ConfirmDelete null selection type`() {
        // Verify no repository action is taken and state remains unchanged if deleteSelection is null when ConfirmDelete is received.
        // TODO implement test
    }

    @Test
    fun `onIntent DismissDeleteDialog state reset`() {
        // Verify that showDelete is set to false and selectedTodo is cleared when DismissDeleteDialog is triggered.
        // TODO implement test
    }

    @Test
    fun `onIntent ShowDeleteDialog state update`() {
        // Verify that showDelete becomes true and the selection type/todo are correctly mapped into the state.
        // TODO implement test
    }

    @Test
    fun `onIntent DeleteSelected redirection`() {
        // Verify that TodoIntent.DeleteSelected correctly triggers the internal deleteSelected() logic.
        // TODO implement test
    }

    @Test
    fun `deleteSelected with multiple items`() {
        // Verify repository.deleteTodos is called with the exact subset of items from the state that have isSelected set to true.
        // TODO implement test
    }

    @Test
    fun `deleteSelected with no items`() {
        // Verify repository.deleteTodos is NOT called when the current state items list contains no selected entries.
        // TODO implement test
    }

    @Test
    fun `observeTodos error handling`() {
        // Test behavior if repository.getAllTodoListFlow throws an exception (Edge Case: ViewModelScope/Flow resilience).
        // TODO implement test
    }

    @Test
    fun `Concurrent intent processing safety`() {
        // Verify that multiple rapid intents (e.g. multiple Inserts) are handled correctly via the Dispatchers.IO coroutine launches.
        // TODO implement test
    }

    private fun getTodoList(): List<Todo> {
        return listOf<Todo>(
            Todo("Task 1", 1, false),
            Todo("Task 2", 2, true),
            Todo("Task 3", 3, true),
            Todo("Task 4", 4, true)
        )
    }

    @After
    fun after() {
        unmockkStatic(Dispatchers::class)
    }

}