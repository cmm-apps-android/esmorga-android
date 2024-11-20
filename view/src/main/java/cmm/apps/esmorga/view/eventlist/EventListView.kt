package cmm.apps.esmorga.view.eventlist

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cmm.apps.designsystem.EsmorgaButton
import cmm.apps.designsystem.EsmorgaLinearLoader
import cmm.apps.designsystem.EsmorgaText
import cmm.apps.designsystem.EsmorgaTextStyle
import cmm.apps.esmorga.domain.event.model.Event
import cmm.apps.esmorga.view.R
import cmm.apps.esmorga.view.Screen
import cmm.apps.esmorga.view.eventlist.EventListScreenTestTags.EVENT_LIST_EVENT_NAME
import cmm.apps.esmorga.view.eventlist.EventListScreenTestTags.EVENT_LIST_TITLE
import cmm.apps.esmorga.view.eventlist.model.EventListEffect
import cmm.apps.esmorga.view.eventlist.model.EventListUiModel
import cmm.apps.esmorga.view.eventlist.model.EventListUiState
import cmm.apps.esmorga.view.extensions.observeLifecycleEvents
import cmm.apps.esmorga.view.navigation.ScaffoldViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.koin.androidx.compose.koinViewModel

@Screen
@Composable
fun EventListScreen(
    elvm: EventListViewModel = koinViewModel(),
    scaffoldViewModel: ScaffoldViewModel = viewModel(LocalContext.current as ComponentActivity),
    onEventClick: (event: Event) -> Unit
) {
    val uiState: EventListUiState by elvm.uiState.collectAsStateWithLifecycle()

    val message = stringResource(R.string.snackbar_no_internet)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    elvm.observeLifecycleEvents(lifecycle)
    LaunchedEffect(Unit) {
        elvm.effect.collect { eff ->
            when (eff) {
                is EventListEffect.ShowNoNetworkPrompt -> scaffoldViewModel.showSnackbar(message)
                is EventListEffect.NavigateToEventDetail -> onEventClick(eff.event)
            }
        }
    }

    scaffoldViewModel.setUpTopBar(null)

    EventListView(
        uiState = uiState,
        onRetryClick = { elvm.loadEvents() },
        onEventClick = { elvm.onEventClick(it) }
    )
}

@Composable
fun EventListView(uiState: EventListUiState, onRetryClick: () -> Unit, onEventClick: (event: EventListUiModel) -> Unit) {
    Column {
        EsmorgaText(
            text = stringResource(R.string.screen_event_list_title),
            style = EsmorgaTextStyle.HEADING_1,
            modifier = Modifier
                .padding(vertical = 32.dp, horizontal = 16.dp)
                .testTag(EVENT_LIST_TITLE)
        )
        if (uiState.loading) {
            EventListLoading()
        } else {
            if (uiState.error.isNullOrBlank().not()) {
                EventListError(onRetryClick)
            } else if (uiState.eventList.isEmpty()) {
                EventListEmpty()
            } else {
                EventList(uiState.eventList, onEventClick, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun EventListLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        EsmorgaText(text = stringResource(R.string.screen_event_list_loading), style = EsmorgaTextStyle.HEADING_1, modifier = Modifier.padding(vertical = 16.dp))
        EsmorgaLinearLoader(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun EventListEmpty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_event_list_empty),
            contentDescription = stringResource(id = R.string.screen_event_list_empty_text),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        EsmorgaText(
            text = stringResource(R.string.screen_event_list_empty_text),
            style = EsmorgaTextStyle.HEADING_2,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
        )
    }
}

@Composable
fun EventListError(onRetryClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .size(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_error),
                    contentDescription = stringResource(R.string.default_error_title),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                EsmorgaText(text = stringResource(R.string.default_error_title), style = EsmorgaTextStyle.HEADING_2, modifier = Modifier.padding(vertical = 4.dp))
                EsmorgaText(text = stringResource(R.string.default_error_body), style = EsmorgaTextStyle.BODY_1)
            }
        }

        Box(modifier = Modifier.height(32.dp))

        EsmorgaButton(text = stringResource(R.string.button_retry)) {
            onRetryClick()
        }
    }
}

@Composable
fun EventList(events: List<EventListUiModel>, onEventClick: (event: EventListUiModel) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn {
        items(events.size) { pos ->
            val event = events[pos]

            Column(modifier = modifier
                .padding(bottom = 32.dp)
                .clickable {
                    onEventClick(event)
                }) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(event.imageUrl)
                        //.crossfade(true) //Open bug in Coil https://github.com/coil-kt/coil/issues/1688 leads to image not being properly scaled if crossfade is used
                        .build(),
                    placeholder = painterResource(R.drawable.img_event_list_empty),
                    error = painterResource(R.drawable.img_event_list_empty),
                    contentDescription = stringResource(id = R.string.content_description_event_image).format(event.cardTitle),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                EsmorgaText(
                    text = event.cardTitle,
                    style = EsmorgaTextStyle.HEADING_2,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .testTag(EVENT_LIST_EVENT_NAME)
                )
                EsmorgaText(text = event.cardSubtitle1, style = EsmorgaTextStyle.BODY_1_ACCENT, modifier = Modifier.padding(vertical = 4.dp))
                EsmorgaText(text = event.cardSubtitle2, style = EsmorgaTextStyle.BODY_1_ACCENT, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

object EventListScreenTestTags {
    const val EVENT_LIST_TITLE = "event list screen title"
    const val EVENT_LIST_EVENT_NAME = "event list event name"
}