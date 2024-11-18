package cmm.apps.esmorga.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cmm.apps.esmorga.view.home.BottomNavItem
import cmm.apps.esmorga.view.home.BottomNavItemRoute
import cmm.apps.esmorga.view.home.EsmorgaBottomBar
import cmm.apps.esmorga.view.navigation.EsmorgaNavigationGraph
import cmm.apps.esmorga.view.navigation.ScaffoldViewModel
import cmm.apps.esmorga.view.theme.EsmorgaTheme
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val mvm: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        var uiState: MainUiState by mutableStateOf(MainUiState())
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                mvm.uiState.onEach { uiState = it }.collect {
                    if (!it.loading) {
                        setupNavigation(it.loggedIn)
                    }
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            uiState.loading
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    private fun setupNavigation(loggedIn: Boolean) {
        setContent {
            EsmorgaTheme {
                val navigationController = rememberNavController()
                val bottomNavItems = listOf(
                    BottomNavItem.Explore,
                    BottomNavItem.MyEvents,
                    BottomNavItem.Profile
                )

                HomeView(bottomNavItems, navigationController) {
                    EsmorgaNavigationGraph(navigationController = navigationController, loggedIn)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(bottomNavItems: List<BottomNavItem>, navigationController: NavHostController, content: @Composable () -> Unit) {
    val scaffoldViewModel: ScaffoldViewModel = viewModel(LocalContext.current as ComponentActivity)
    val topBarUiState by scaffoldViewModel.topBarUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        scaffoldViewModel.snackbarEffect.collect { msg -> snackbarHostState.showSnackbar(message = msg) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            topBarUiState?.let {
                TopAppBar(
                    title = { Text(it.topBarTitle) },
                    navigationIcon = it.navigationIcon,
                    actions = it.topBarActions
                )
            }
        },
        bottomBar = {
            val navBackStackEntry by navigationController.currentBackStackEntryAsState()
            val currentRoute =
                navBackStackEntry?.destination?.hierarchy?.first()?.route?.substringAfterLast(".")
            val route = bottomNavItems.find { currentRoute == it.route.screen }?.route

            val visibility = route != null
            HomeBottomBar(bottomNavItems, visibility, navigationController, route)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content.invoke()
        }
    }
}

@Composable
fun HomeBottomBar(bottomNavItems: List<BottomNavItem>, visibility: Boolean, navigationController: NavHostController, currentRoute: BottomNavItemRoute?) {
    AnimatedVisibility(
        visible = visibility
    ) {
        Column {
            HorizontalDivider(Modifier.fillMaxWidth(), thickness = 1.dp, color = colorScheme.surfaceVariant)
            EsmorgaBottomBar(navigationController, bottomNavItems, currentRoute)
        }
    }
}
