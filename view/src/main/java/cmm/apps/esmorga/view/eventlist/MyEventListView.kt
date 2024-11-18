package cmm.apps.esmorga.view.eventlist

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cmm.apps.designsystem.EsmorgaButton
import cmm.apps.designsystem.EsmorgaText
import cmm.apps.designsystem.EsmorgaTextStyle
import cmm.apps.esmorga.domain.event.model.Event
import cmm.apps.esmorga.view.R
import cmm.apps.esmorga.view.Screen
import cmm.apps.esmorga.view.eventlist.MyEventListScreenTestTags.MY_EVENT_LIST_TITLE
import cmm.apps.esmorga.view.eventlist.model.EventListUiModel
import cmm.apps.esmorga.view.eventlist.model.MyEventListEffect
import cmm.apps.esmorga.view.eventlist.model.MyEventListError
import cmm.apps.esmorga.view.eventlist.model.MyEventListUiState
import cmm.apps.esmorga.view.extensions.observeLifecycleEvents
import cmm.apps.esmorga.view.navigation.ScaffoldViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import org.koin.androidx.compose.koinViewModel

@Screen
@Composable
fun MyEventListScreen(
    elvm: MyEventListViewModel = koinViewModel(),
    scaffoldViewModel: ScaffoldViewModel = viewModel(LocalContext.current as ComponentActivity),
    onEventClick: (event: Event) -> Unit, onSignInClick: () -> Unit
) {
    val uiState: MyEventListUiState by elvm.uiState.collectAsStateWithLifecycle()

    val message = stringResource(R.string.snackbar_no_internet)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    elvm.observeLifecycleEvents(lifecycle)
    LaunchedEffect(Unit) {
        elvm.effect.collect { eff ->
            when (eff) {
                is MyEventListEffect.ShowNoNetworkPrompt -> scaffoldViewModel.showSnackbar(message)
                is MyEventListEffect.NavigateToEventDetail -> onEventClick(eff.event)
                is MyEventListEffect.NavigateToSignIn -> onSignInClick()
            }
        }
    }

    LaunchedEffect(Unit) { scaffoldViewModel.setUpTopBar(null) }

    MyEventListView(
        uiState = uiState,
        onSignInClick = { elvm.onSignInClick() },
        onEventClick = { elvm.onEventClick(it) },
        onRetryClick = { elvm.loadMyEvents() }
    )
}

@Composable
fun MyEventListView(
    uiState: MyEventListUiState,
    onSignInClick: () -> Unit,
    onEventClick: (event: EventListUiModel) -> Unit,
    onRetryClick: () -> Unit
) {
    Column {
        EsmorgaText(
            text = stringResource(R.string.screen_my_events_title),
            style = EsmorgaTextStyle.HEADING_1,
            modifier = Modifier
                .padding(vertical = 32.dp, horizontal = 16.dp)
                .testTag(MY_EVENT_LIST_TITLE)
        )
        if (uiState.loading) {
            EventListLoading()
        } else {
            when (uiState.error) {
                MyEventListError.EMPTY_LIST -> MyEventsEmptyView()
                MyEventListError.NOT_LOGGED_IN -> MyEventGuestError(
                    stringResource(R.string.unauthenticated_error_title),
                    stringResource(R.string.button_login)
                ) { onSignInClick() }

                MyEventListError.UNKNOWN -> MyEventGuestError(stringResource(R.string.default_error_title), stringResource(R.string.button_retry)) { onRetryClick() }
                null -> EventList(uiState.eventList, onEventClick, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun MyEventGuestError(errorMessage: String, buttonText: String, onButtonClicked: () -> Unit) {
    val lottieAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.oops))
    Column(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp
            )
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        LottieAnimation(
            composition = lottieAnimation,
            iterations = Int.MAX_VALUE,
            contentScale = ContentScale.Inside,
            modifier = Modifier.fillMaxHeight(0.3f)
        )
        EsmorgaText(errorMessage, style = EsmorgaTextStyle.HEADING_2)
        Spacer(modifier = Modifier.weight(1f))
        EsmorgaButton(buttonText) {
            onButtonClicked.invoke()
        }
    }
}

@Composable
fun MyEventsEmptyView() {
    val lottieAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxSize(),
    ) {
        EsmorgaText(
            text = stringResource(R.string.screen_my_events_empty_text),
            style = EsmorgaTextStyle.HEADING_1,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        LottieAnimation(
            composition = lottieAnimation,
            iterations = Int.MAX_VALUE,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .align(Alignment.Start)
        )
        Spacer(modifier = Modifier.weight(0.1f))
    }

}

object MyEventListScreenTestTags {
    const val MY_EVENT_LIST_TITLE = "event list screen title"
}