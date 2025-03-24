package dev.aaa1115910.bv.component

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.tv.material3.Carousel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.entity.CarouselData
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.util.focusedBorder

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PgcCarousel(
    modifier: Modifier = Modifier,
    data: List<CarouselData.CarouselItem>
) {
    val context = LocalContext.current

    CarouselContent(
        modifier = modifier,
        data = data,
        onClick = { item ->
            SeasonInfoActivity.actionStart(
                context = context,
                epId = item.episodeId,
                seasonId = item.seasonId,
                proxyArea = ProxyArea.checkProxyArea(item.title)
            )
        }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UgcCarousel(
    modifier: Modifier = Modifier,
    data: List<CarouselData.CarouselItem>
) {
    val context = LocalContext.current

    CarouselContent(
        modifier = modifier,
        data = data,
        onClick = { item ->
            VideoInfoActivity.actionStart(
                context = context,
                aid = item.avid!!
            )
        }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CarouselContent(
    modifier: Modifier = Modifier,
    data: List<CarouselData.CarouselItem>,
    onClick: (CarouselData.CarouselItem) -> Unit
) {
    var isBoxSelected by remember { mutableStateOf(false) }
    val carouselContainerFocusRequester = remember { FocusRequester() }

    val cardFocusRequesters by remember(data) {
        derivedStateOf {
            if (data.isNotEmpty()) {
                List(data.size) { FocusRequester() }.toMutableList()
            } else {
                mutableListOf()
            }
        }
    }

    Box(
        modifier = modifier
            .focusable()
            .onFocusChanged { focusState ->
                isBoxSelected = (focusState.isFocused || focusState.hasFocus)
                if (isBoxSelected) {
                    if (cardFocusRequesters.isNotEmpty()) {
                        cardFocusRequesters.firstOrNull()?.requestFocus()
                    }
                }
            }
            .focusRequester(carouselContainerFocusRequester)
    ) {
        Carousel(
            itemCount = data.size,
            modifier = Modifier
                .height(240.dp)
                .clip(MaterialTheme.shapes.large)
                .focusedBorder()
                .focusProperties {
                    canFocus = isBoxSelected
                },
            contentTransformEndToStart =
            fadeIn(tween(1000)).togetherWith(fadeOut(tween(1000))),
            contentTransformStartToEnd =
            fadeIn(tween(1000)).togetherWith(fadeOut(tween(1000)))
        ) { itemIndex ->
            val focusRequester = cardFocusRequesters.getOrNull(itemIndex) ?: remember { FocusRequester() }
            CarouselCard(
                data = data[itemIndex],
                onClick = { onClick(data[itemIndex]) },
                focusRequester = focusRequester
            )
        }
    }
}


@Composable
fun CarouselCard(
    modifier: Modifier = Modifier,
    data: CarouselData.CarouselItem,
    onClick: () -> Unit = {},
    focusRequester: FocusRequester
) {
    AsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() }
            .focusRequester(focusRequester)
            .focusable(),
        model = data.cover,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter
    )
}
