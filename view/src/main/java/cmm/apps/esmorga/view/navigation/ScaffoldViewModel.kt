package cmm.apps.esmorga.view.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScaffoldViewModel : ViewModel() {

    private val _topBarUiState: MutableStateFlow<TopBarUiState?> = MutableStateFlow(TopBarUiState())
    val topBarUiState: StateFlow<TopBarUiState?> = _topBarUiState.asStateFlow()

    private val _snackbarEffect: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val snackbarEffect: SharedFlow<String> = _snackbarEffect.asSharedFlow()

    fun setUpTopBar(topBarUiState: TopBarUiState?) {
        _topBarUiState.value = topBarUiState
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch { _snackbarEffect.tryEmit(message) }
    }
}


data class TopBarUiState(
    var topBarTitle: String = "",
    var navigationIcon: @Composable () -> Unit = {},
    var topBarActions: @Composable RowScope.() -> Unit = {}
)