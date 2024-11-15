package cmm.apps.esmorga.view.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class TopBarUiState(
    val navigationIcon: (@Composable () -> Unit) = {},
    val title: String = "",
    val actions: (@Composable RowScope.() -> Unit) = {},
    val isVisible: Boolean
)