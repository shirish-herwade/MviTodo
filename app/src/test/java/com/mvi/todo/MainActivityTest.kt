package com.mvi.todo

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import com.mvi.todo.model.local.Todo
import com.mvi.todo.state.TodoState
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var todoList: List<Todo>
    private lateinit var state: TodoState

    @Before
    fun setUp() {
        todoList = getTodoList()
        state = TodoState(items = todoList)
    }

    @After
    fun tearDown() {
        // Reset any global state if needed. Currently nothing to reset, kept for symmetry and future use.
    }

    @Test
    fun `Activity basic initialization`() {
        // //Verify that onCreate executes without throwing any exceptions and the Activity reaches the CREATED state.
//        MainActivity().onCreate(null)

    }

    @Test
    fun `Edge to edge configuration`() {
        // //Verify that enableEdgeToEdge() is invoked during the initialization process to ensure correct window insets.

    }

    @Test
    fun `Theme application`() {
        // Ensure that MviTodoTheme is correctly applied as the root wrapper for the Compose content hierarchy.
        // TODO implement test
    }

    @Test
    fun `Hilt ViewModel injection`() {
        // //Verify that hiltViewModel() successfully provides a non-null instance of TodoViewModel using Hilt dependency injection.
        // TODO implement test
    }

    @Test
    fun `Initial state collection`() {
        // Check if the initial state from the ViewModel is correctly collected and passed to the MainScreen composable.
        // TODO implement test
    }

    @Test
    fun `State update propagation`() {
        // //Verify that when the ViewModel state changes, the Compose UI recomposes MainScreen with the updated state values.
        // TODO implement test
    }

    @Test
    fun `Intent callback binding`() {
        // Ensure the onIntent lambda passed to MainScreen correctly triggers the onIntent method within the TodoViewModel instance.
        // TODO implement test
    }

    @Test
    fun `Null savedInstanceState handling`() {
        // //Verify the activity initializes correctly during a cold start when savedInstanceState is null.
        // TODO implement test
    }

    @Test
    fun `Non null savedInstanceState restoration`() {
        // Test activity restoration logic when savedInstanceState is provided after a configuration change or process death.
        // TODO implement test
    }

    @Test
    fun `Configuration change resiliency`() {
        // //Verify that the ViewModel instance is retained and the state is preserved across configuration changes like screen rotation.
        // TODO implement test
    }

    @Test
    fun `Compose Content View attachment`() {
        // Ensure that setContent successfully attaches the ComposeView to the activity's window decor view.
        // TODO implement test
    }

    @Test
    fun `Lifecycle owner consistency`() {
        // //Verify that the Composable tree uses the Activity as the correct LifecycleOwner for state collection and effect handling.
        // TODO implement test
    }


    @Test
    fun `MainScreen loading state UI`() {
        //Verify that `CircularProgressIndicator` is displayed in the center of the screen when `state.isLoading` is true.
        val state = TodoState(isLoading = true)
        composeTestRule.setContent {
            MainScreen(state = state, onIntent = {})
        }
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("loadingIndicator").assertIsDisplayed()
    }

    @Test
    fun `MainScreen empty state UI`() {
        //Verify that the text 'Nothing found' is displayed in the center when `state.isLoading` is false and `state.items` is empty.
        val state = TodoState(isLoading = false, items = emptyList())
        composeTestRule.setContent {
            MainScreen(state = state, onIntent = {})
        }
        composeTestRule.onNodeWithText("Nothing found").assertIsDisplayed()
    }

    @Test
    fun `MainScreen displays todo items`() {
        //Verify that `LazyColumn` is displayed and `TodoRow` is composed for each item in `state.items` when the list is not empty.
        todoList = getTodoList()
        composeTestRule.setContent {
            MainScreen(state = state, onIntent = {})
        }

        val allRows = composeTestRule.onAllNodesWithTag("todoRow", useUnmergedTree = true)
        allRows.assertCountEquals(todoList.size)

        todoList.forEachIndexed { index, todo ->
            val rowNode = allRows[index]
            val textNode = composeTestRule.onNodeWithText(todo.title)

            rowNode.onParent().assertHasClickAction()
            rowNode.assertIsDisplayed()
            textNode.assertIsDisplayed()

            val checkBoxNode = rowNode.onChildAt(1)
//            checkBoxNode.assertHasClickAction()
            if (todo.isSelected) {
                checkBoxNode.assertIsOn()
            } else {
                checkBoxNode.assertIsOff()
            }
        }
//        composeTestRule.onAllNodesWithTag("todoRow").assertCountEquals(todoList.size)
//        composeTestRule.onNodeWithTag("todoRow").performClick()
    }

    private fun getTodoList(): List<Todo> {
        return listOf<Todo>(
            Todo("Task 1", 1, false),
            Todo("Task 2", 2, true),
            Todo("Task 3", 3, true),
            Todo("Task 4", 4, true)
        )
    }

    @Test
    fun `MainScreen delete button enabled with selections`() {
        //Verify that clicking the top bar delete icon triggers the `ShowDeleteDialog` intent with `DeleteSelection.Selected` when at least one todo item is selected (`it.isSelected` is true).
        val todoList = getTodoList()
        val state = TodoState(isLoading = false, items = todoList)
        composeTestRule.setContent {
            MainScreen(state = state, onIntent = {})
        }
        composeTestRule.onNodeWithContentDescription("Delete selected icons").assertIsEnabled()
    }

    @Test
    fun `MainScreen delete button disabled without selections`() {
        //Verify that clicking the top bar delete icon does nothing when no todo items are selected .
        val todoList = getTodoList()
//        val unselectedList = todoList.map { it.copy(isSelected = false) }

        todoList.forEach { todo ->
            todo.copy(isSelected = false)
        }

        val state = TodoState(isLoading = false, items = todoList)

        composeTestRule.setContent {
            MainScreen(state = state, onIntent = {})
        }
//        composeTestRule.onNodeWithContentDescription("Delete selected icons").assertIsNotEnabled()
    }

    @Test
    fun `MainScreen map navigation`() {
        //Verify that clicking the map icon in the top app bar invokes the `onNavigateToMap` callback.

        val context =
            androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val navController = TestNavHostController(context)
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        composeTestRule.runOnUiThread {
//            navController.setGraph(
//                navController.createGraph(startDestination = "home") {
//                    composable("home") {}
//                    composable("map") {}
//                }
//            )
            // Assign the created NavGraph directly to avoid calling the Int-based setGraph overload
            navController.graph = navController.createGraph(startDestination = "home") {
                composable("home") {}
                composable("map") {}
            }
        }

        composeTestRule.setContent {
            MainScreen(
                state = state,
                onIntent = {},
                onNavigateToMap = { navController.navigate("map") }
            )
        }
//        composeTestRule.onNodeWithContentDescription("Go to Map").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Go to Map").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "map")
    }

    @Test
    fun `ShowDeleteDialog visibility for single item`() {
        //Verify the `AlertDialog` is shown when `state.showDelete` is true and `state.deleteSelection` is `Single`, and that the text includes the specific todo's title.
        // TODO implement test
    }

    @Test
    fun `ShowDeleteDialog visibility for multiple items`() {
        //Verify the `AlertDialog` is shown when `state.showDelete` is true and `state.deleteSelection` is `Selected`, displaying the generic text for multiple deletions.
        // TODO implement test
    }

    @Test
    fun `ShowDeleteDialog hidden state`() {
        //Verify that the `AlertDialog` is not composed when `state.showDelete` is false.
        // TODO implement test
    }

    @Test
    fun `ShowDeleteDialog confirm delete action`() {
        //Verify that clicking the 'Delete' `TextButton` in the `AlertDialog` invokes the `onIntent` with `TodoIntent.ConfirmDelete` .
        // TODO implement test
    }

    @Test
    fun `ShowDeleteDialog dismiss action via button`() {
        //Verify that clicking the 'Cancel' `TextButton` in the `AlertDialog` invokes the `onIntent` with `TodoIntent.DismissDeleteDialog` .
        // TODO implement test
    }

    @Test
    fun `ShowDeleteDialog dismiss action via onDismissRequest`() {
        //Verify that an outside click or back press on the `AlertDialog` invokes the `onIntent` with `TodoIntent.DismissDeleteDialog` .
        // TODO implement test
    }

    @Test
    fun `BottomInputArea displays draft title`() {
        //Verify that the `TextField` correctly displays the `state.draftTitle` .
        // TODO implement test
    }

    @Test
    fun `BottomInputArea handles title change`() {
        //Verify that typing in the `TextField` invokes `onIntent` with `TodoIntent.ChangeDraftTitle` and the new text .
        // TODO implement test
    }

    @Test
    fun `BottomInputArea handles save click`() {
        //Verify that clicking the 'Save todo' button invokes `onIntent` with `TodoIntent.SubmitTodo` .
        // TODO implement test
    }

    @Test
    fun `BottomInputArea handles IME padding`() {
        //Verify that the `imePadding` modifier is applied, ensuring the input area adjusts when the keyboard is shown.
        // TODO implement test
    }

    @Test
    fun `handleSaveClick with blank title`() {
        //Verify that `onIntent` is NOT called and the title state is NOT cleared when the `titleState` contains only whitespace.
        // TODO implement test
    }

    @Test
    fun `handleSaveClick with valid title`() {
        //Verify that `onIntent` is called with the trimmed title and that the `titleState` is cleared to an empty string.
        // TODO implement test
    }

    @Test
    fun `TodoRow displays title correctly`() {
        //Verify that the `Text` composable inside `TodoRow` correctly displays the `todo.title`.
        // TODO implement test
    }

    @Test
    fun `TodoRow checkbox state checked`() {
        //Verify the `Checkbox` is checked when `todo.isSelected` is true.
        // TODO implement test
    }

    @Test
    fun `TodoRow checkbox state unchecked`() {
        //Verify the `Checkbox` is unchecked when `todo.isSelected` is false.
        // TODO implement test
    }

    @Test
    fun `TodoRow checkbox interaction`() {
        //Verify that clicking the `Checkbox` invokes `onIntent` with `TodoIntent.Update` and the toggled `isSelected` state .
        // TODO implement test
    }

    @Test
    fun `TodoRow long click interaction`() {
        //Verify that a long click on the `TodoRow` invokes `onIntent` with `TodoIntent.ShowDeleteDialog` for a `DeleteSelection.Single` .
        // TODO implement test
    }

    @Test
    fun `TodoRow short click interaction`() {
        //Verify that a short click on the `TodoRow` does nothing, as the `onClick` lambda is empty.
        // TODO implement test
    }

    @Test
    fun `MainScreenPreview renders without errors`() {
        //Verify that the `MainScreenPreview` composable renders successfully with a mock `TodoState` containing multiple items .
        // TODO implement test
    }

    @Test
    fun `MainScreenPreview with empty items`() {
//        Create a preview test where `mockState.items` is empty to //Verify the 'Nothing found' text renders correctly in Preview.
        // TODO implement test
    }

    @Test
    fun `MainScreenPreview in loading state`() {
//        Create a preview test where `mockState.isLoading` is true to //Verify the `CircularProgressIndicator` renders correctly in Preview.
        // TODO implement test
    }
}
