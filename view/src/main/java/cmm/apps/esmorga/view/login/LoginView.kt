package cmm.apps.esmorga.view.login

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cmm.apps.designsystem.EsmorgaButton
import cmm.apps.designsystem.EsmorgaText
import cmm.apps.designsystem.EsmorgaTextField
import cmm.apps.designsystem.EsmorgaTextStyle
import cmm.apps.esmorga.view.R
import cmm.apps.esmorga.view.Screen
import cmm.apps.esmorga.view.errors.model.EsmorgaErrorScreenArguments
import cmm.apps.esmorga.view.eventdetails.EventDetailsScreenTestTags.EVENT_DETAILS_BACK_BUTTON
import cmm.apps.esmorga.view.login.LoginScreenTestTags.LOGIN_EMAIL_INPUT
import cmm.apps.esmorga.view.login.LoginScreenTestTags.LOGIN_LOGIN_BUTTON
import cmm.apps.esmorga.view.login.LoginScreenTestTags.LOGIN_PASSWORD_INPUT
import cmm.apps.esmorga.view.login.LoginScreenTestTags.LOGIN_REGISTER_BUTTON
import cmm.apps.esmorga.view.login.LoginScreenTestTags.LOGIN_TITLE
import cmm.apps.esmorga.view.login.model.LoginEffect
import cmm.apps.esmorga.view.login.model.LoginUiState
import cmm.apps.esmorga.view.navigation.ScaffoldViewModel
import cmm.apps.esmorga.view.navigation.TopBarUiState
import org.koin.androidx.compose.koinViewModel

@Screen
@Composable
fun LoginScreen(
    lvm: LoginViewModel = koinViewModel(),
    scaffoldViewModel: ScaffoldViewModel = viewModel(LocalContext.current as ComponentActivity),
    onRegisterClicked: () -> Unit,
    onLoginSuccess: () -> Unit,
    onLoginError: (EsmorgaErrorScreenArguments) -> Unit,
    onBackClicked: () -> Unit
) {
    val uiState: LoginUiState by lvm.uiState.collectAsStateWithLifecycle()
    val message = stringResource(R.string.snackbar_no_internet)
    LaunchedEffect(Unit) {
        lvm.effect.collect { eff ->
            when (eff) {
                is LoginEffect.ShowNoNetworkSnackbar -> scaffoldViewModel.showSnackbar(message = message)
                is LoginEffect.NavigateToRegistration -> onRegisterClicked()
                is LoginEffect.ShowFullScreenError -> onLoginError(eff.esmorgaErrorScreenArguments)
                is LoginEffect.NavigateToEventList -> onLoginSuccess()
            }
        }
    }

    LaunchedEffect(Unit) {
        scaffoldViewModel.setUpTopBar(TopBarUiState(navigationIcon = {
            IconButton(onClick = { onBackClicked() }, modifier = Modifier.testTag(EVENT_DETAILS_BACK_BUTTON)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_description_back_icon)
                )
            }
        }))
    }

    LoginView(
        uiState = uiState,
        onLoginClicked = { email, password -> lvm.onLoginClicked(email, password) },
        onRegisterClicked = { lvm.onRegisterClicked() },
        onEmailChanged = { lvm.onEmailChanged() },
        onPassChanged = { lvm.onPassChanged() },
        validateEmail = { email -> lvm.validateEmail(email) },
        validatePass = { password -> lvm.validatePass(password) }
    )
}

@Composable
fun LoginView(
    uiState: LoginUiState,
    onLoginClicked: (String, String) -> Unit,
    onRegisterClicked: () -> Unit,
    onEmailChanged: () -> Unit,
    onPassChanged: () -> Unit,
    validateEmail: (String) -> Unit,
    validatePass: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.img_login_header),
            contentDescription = "Login header",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
        ) {
            EsmorgaText(
                text = stringResource(id = R.string.screen_login_title),
                style = EsmorgaTextStyle.HEADING_1,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .testTag(LOGIN_TITLE)
            )
            EsmorgaTextField(
                value = email,
                isEnabled = !uiState.loading,
                onValueChange = {
                    email = it
                    onEmailChanged()
                },
                title = R.string.field_title_email,
                placeholder = R.string.placeholder_email,
                errorText = uiState.emailError,
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            validateEmail(email)
                        }
                    }
                    .testTag(LOGIN_EMAIL_INPUT),
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email

            )
            EsmorgaTextField(
                value = password,
                isEnabled = !uiState.loading,
                onValueChange = {
                    password = it
                    onPassChanged()
                },
                title = R.string.field_title_password,
                placeholder = R.string.placeholder_password,
                errorText = uiState.passwordError,
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            validatePass(password)
                        }
                    }
                    .testTag(LOGIN_PASSWORD_INPUT),
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password,
                onDonePressed = {
                    onLoginClicked(email, password)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            EsmorgaButton(text = stringResource(id = R.string.button_login), isLoading = uiState.loading, modifier = Modifier.testTag(LOGIN_LOGIN_BUTTON)) {
                onLoginClicked(email, password)
            }
            EsmorgaButton(
                text = stringResource(id = R.string.button_create_account),
                isEnabled = !uiState.loading,
                primary = false,
                modifier = Modifier.testTag(LOGIN_REGISTER_BUTTON)
            ) {
                onRegisterClicked()
            }
        }
    }

}

object LoginScreenTestTags {
    const val LOGIN_TITLE = "login screen title"
    const val LOGIN_EMAIL_INPUT = "login screen email input"
    const val LOGIN_PASSWORD_INPUT = "login screen password input"
    const val LOGIN_LOGIN_BUTTON = "login screen login button"
    const val LOGIN_REGISTER_BUTTON = "login screen register button"
}