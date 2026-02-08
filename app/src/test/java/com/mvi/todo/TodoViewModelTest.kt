package com.mvi.todo

//import com.mvi.todo.ToDoViewModel
import com.mvi.todo.model.local.Todo
import com.mvi.todo.model.repository.TodoRepository
import com.mvi.todo.state.TodoState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


//@RunWith(MockitoJUnitRunner::class)
class TodoViewModelTest {

    private val repository = mockk<TodoRepository>(relaxed = true)
    private lateinit var viewModel: TodoViewModel

    @Before
    fun before() {
        every {
            repository.getAllTodoListFlow()
        } returns flowOf(getTodoList())

        viewModel = TodoViewModel(repository)
    }

    private fun getTodoList(): List<Todo> {
        return listOf<Todo>(
            Todo("1", 1, false),
            Todo("2", 2, true),
            Todo("3", 3, true),
            Todo("4", 4, true)
        )
    }

    @After
    fun after() {

    }

    @Test
    fun `getState initial state validation`() = runTest {
//        every { viewModel.state.value } returns TodoState()

        viewModel.deleteSelected()
        coVerify {
            repository.deleteTodos(any())
        }
//        cancelAndIgnoreRemainingEvents()
    }


    @Test
    fun `getState repository flow collection`() {
        // Verify that when repository.getAllTodoListFlow emits a new list, the StateFlow updates items and sets isLoading to false.
        // TODO implement test
    }

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

}