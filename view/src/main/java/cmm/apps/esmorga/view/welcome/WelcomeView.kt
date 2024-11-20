package cmm.apps.esmorga.view.welcome

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cmm.apps.designsystem.EsmorgaButton
import cmm.apps.esmorga.view.Screen
import cmm.apps.esmorga.view.navigation.ScaffoldViewModel
import cmm.apps.esmorga.view.welcome.WelcomeScreenTestTags.WELCOME_PRIMARY_BUTTON
import cmm.apps.esmorga.view.welcome.WelcomeScreenTestTags.WELCOME_SECONDARY_BUTTON
import cmm.apps.esmorga.view.welcome.model.WelcomeEffect
import cmm.apps.esmorga.view.welcome.model.WelcomeUiState
import org.koin.androidx.compose.koinViewModel

@Screen
@Composable
fun WelcomeScreen(
    wvm: WelcomeViewModel = koinViewModel(),
    scaffoldViewModel: ScaffoldViewModel = viewModel(LocalContext.current as ComponentActivity),
    onLoginRegisterClicked: () -> Unit,
    onEnterAsGuestClicked: () -> Unit
) {
    val uiState: WelcomeUiState by wvm.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        wvm.effect.collect { eff ->
            when (eff) {
                is WelcomeEffect.NavigateToEventList -> onEnterAsGuestClicked()
                is WelcomeEffect.NavigateToLogin -> onLoginRegisterClicked()
            }
        }
    }

    scaffoldViewModel.setUpTopBar(null)

    WelcomeView(uiState = uiState, onPrimaryButtonClicked = { wvm.onPrimaryButtonClicked() }, onSecondaryButtonClicked = { wvm.onSecondaryButtonClicked() })
}

@Composable
fun WelcomeView(uiState: WelcomeUiState, onPrimaryButtonClicked: () -> Unit, onSecondaryButtonClicked: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = uiState.icon), contentDescription = "App logo",
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(32.dp))
        )
        Spacer(modifier = Modifier.height(32.dp))
        EsmorgaButton(text = uiState.primaryButtonText, modifier = Modifier.testTag(WELCOME_PRIMARY_BUTTON)) {
            onPrimaryButtonClicked()
        }
        Spacer(modifier = Modifier.height(32.dp))
        EsmorgaButton(text = uiState.secondaryButtonText, primary = false, modifier = Modifier.testTag(WELCOME_SECONDARY_BUTTON)) {
            onSecondaryButtonClicked()
        }
    }
}

object WelcomeScreenTestTags {
    const val WELCOME_PRIMARY_BUTTON = "welcome screen primary button"
    const val WELCOME_SECONDARY_BUTTON = "welcome screen secondary button"
}