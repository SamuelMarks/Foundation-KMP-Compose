package ui

import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import data.browseWeb
import data.getTodayDate
import data.getTodayUtcMs
import data.hideAndClearFocus
import foundation.composeapp.generated.resources.Res
import foundation.composeapp.generated.resources.action_settings
import foundation.composeapp.generated.resources.button_expand_less
import foundation.composeapp.generated.resources.button_expand_more
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max

enum class AppBarAction(
    val labelRes: StringResource,
    val icon: ImageVector
) {
    SETTINGS(
        Res.string.action_settings,
        Icons.Default.Settings
    )
}

@Composable
fun Modifier.consumeClick() = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null
) { /* no-op */ }

@Composable
fun ExpandableTitledCard(
    modifier: Modifier = Modifier,
    title: String,
    maxUnexpandedHeight: Dp = 180.dp,
    onExpand: ((expanded: Boolean) -> Unit)? = null,
    content: @Composable ColumnScope.(isExpanded: Boolean) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    
    val animatedIcon by animateFloatAsState(
        targetValue = if (expanded) 180f else 360f
    )
    
    val animatedMaxHeight by animateDpAsState(
        label = "max_height",
        targetValue = if (expanded) {
            Float.POSITIVE_INFINITY.dp
        } else {
            maxUnexpandedHeight
        }
    )
    
    val onClick = {
        expanded = !expanded
        onExpand?.invoke(expanded)
    }
    
    Card(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .wrapContentHeight(),
        onClick = {
            onClick()
        }
    ) {
        EdgeFadeBase(
            showStartEdgeFade = false,
            showEndEdgeFade = !expanded,
            orientation = Orientation.Vertical
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .heightIn(max = animatedMaxHeight)
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TitleText(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                            .padding(vertical = 4.dp),
                        text = title,
                        maxLines = if (expanded) 2 else 1
                    )
                    
                    IconButton(
                        onClick = {
                            onClick()
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .graphicsLayer {
                                    rotationZ = animatedIcon
                                },
                            imageVector = Icons.Default.ExpandLess,
                            contentDescription = if (expanded) {
                                stringResource(Res.string.button_expand_less)
                            } else {
                                stringResource(Res.string.button_expand_more)
                            }
                        )
                    }
                }
                
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    content(expanded)
                    
                    AnimatedVisibility(expanded) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TitleText(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = 2
) {
    Text(
        modifier = modifier
            .animateContentSize(),
        text = text,
        style = MaterialTheme.typography.titleLarge,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start
    )
}

@Composable
fun SubtitleText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier
            .animateContentSize(),
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start
    )
}

@Composable
fun BodyText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier
            .animateContentSize(),
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start
    )
}

@Composable
fun HintText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier
            .animateContentSize(),
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start
    )
}

@Composable
fun LinkButton(
    modifier: Modifier = Modifier,
    text: String,
    url: String,
    onVibrate: () -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth(),
        onClick = {
            onVibrate()
            browseWeb(url)
        }
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ClickableIcon(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

// Adapted for multiplatform from: https://dev.to/bmonjoie/jetpack-compose-reveal-effect-1fao

@Composable
fun CircularReveal(
    modifier: Modifier = Modifier,
    startDelayMs: Int,
    revealDurationMs: Int,
    onCompleted: (suspend () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var isRevealed by remember { mutableStateOf(false) }
    
    val animationProgress: State<Float> = animateFloatAsState(
        targetValue = if (isRevealed) 1f else 0f,
        animationSpec = tween(durationMillis = revealDurationMs, easing = TRANSITION_EASING),
        label = "circular_reveal"
    )
    
    LaunchedEffect(Unit) {
        delay(startDelayMs.toLong())
        isRevealed = true
        delay(revealDurationMs.toLong())
        onCompleted?.invoke()
    }
    
    Box(
        modifier = modifier
            .circularReveal(animationProgress.value)
    ) {
        content()
    }
}

fun Modifier.circularReveal(
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    offset: Offset? = null
) = clip(CircularRevealShape(progress, offset))

private class CircularRevealShape(
    @FloatRange(from = 0.0, to = 1.0) private val progress: Float,
    private val offset: Offset? = null
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val startingOffset = offset ?: Offset(size.width / 2f, size.height / 2f)
                // end result slightly larger than full screen
                val revealRadius = max(size.width, size.height) * progress * 0.7f
                
                addOval(
                    Rect(
                        topLeft = Offset(
                            x = startingOffset.x - revealRadius,
                            y = startingOffset.y - revealRadius
                        ),
                        bottomRight = Offset(
                            x = startingOffset.x + revealRadius,
                            y = startingOffset.y + revealRadius
                        )
                    )
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= getTodayUtcMs()
    override fun isSelectableYear(year: Int): Boolean = year <= getTodayDate().year
}

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    text: String,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    maxLines: Int = 1,
    maxTextLength: Int? = null,
    hintText: String? = null,
    onValueChange: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    
    var noteText by remember(text) {
        mutableStateOf(text)
    }
    
    LaunchedEffect(noteText) {
        onValueChange(noteText)
    }
    
    OutlinedTextField(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        value = noteText,
        onValueChange = {
            noteText = if (maxTextLength != null) it.take(maxTextLength) else it
        },
        label = {
            if (hintText != null) {
                Text(hintText)
            }
        },
        keyboardOptions = KeyboardOptions(
            autoCorrect = true,
            capitalization = capitalization,
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController.hideAndClearFocus(focusManager)
            }
        ),
        minLines = minLines,
        maxLines = maxLines,
        supportingText = {
            if (maxTextLength != null) {
                Text("${noteText.length}/$maxTextLength")
            }
        }
    )
}


// Scrollable edge fade lists

@Composable
fun EdgeFadeGrid(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    gridState: LazyGridState,
    fadeSize: Dp = 24.dp,
    gridContent: @Composable () -> Unit
) {
    val orientation = gridState.layoutInfo.orientation
    
    val showStartEdgeFade by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0
        }
    }
    
    val showEndEdgeFade by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            if (layoutInfo.visibleItemsInfo.isNotEmpty()) {
                val lastVisibleItemInfo = layoutInfo.visibleItemsInfo.last()
                val lastItemBottom = lastVisibleItemInfo.offset.y + lastVisibleItemInfo.size.height
                // Check if the last item's end edge is below the viewport's end (indicating it's not fully visible)
                lastItemBottom > layoutInfo.viewportEndOffset
            } else {
                false
            }
        }
    }
    
    EdgeFadeBase(
        modifier = modifier,
        colorScheme = colorScheme,
        showStartEdgeFade = showStartEdgeFade,
        showEndEdgeFade = showEndEdgeFade,
        orientation = orientation,
        fadeSize = fadeSize,
        content = gridContent
    )
}

@Composable
fun EdgeFadeStaggeredGrid(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    gridState: LazyStaggeredGridState,
    fadeSize: Dp = 24.dp,
    gridContent: @Composable () -> Unit
) {
    val orientation = gridState.layoutInfo.orientation
    
    val showStartEdgeFade by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0
        }
    }
    
    val showEndEdgeFade by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            if (layoutInfo.visibleItemsInfo.isNotEmpty()) {
                val lastVisibleItemInfo = layoutInfo.visibleItemsInfo.last()
                val lastItemBottom = lastVisibleItemInfo.offset.y + lastVisibleItemInfo.size.height
                // Check if the last item's end edge is below the viewport's end (indicating it's not fully visible)
                lastItemBottom > layoutInfo.viewportEndOffset
            } else {
                false
            }
        }
    }
    
    EdgeFadeBase(
        modifier = modifier,
        colorScheme = colorScheme,
        showStartEdgeFade = showStartEdgeFade,
        showEndEdgeFade = showEndEdgeFade,
        orientation = orientation,
        fadeSize = fadeSize,
        content = gridContent
    )
}


@Composable
fun EdgeFadeLazyList(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    listState: LazyListState,
    fadeSize: Dp = 24.dp,
    listContent: @Composable () -> Unit
) {
    val orientation = listState.layoutInfo.orientation
    
    val showStartEdgeFade by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    
    val showEndEdgeFade by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            layoutInfo.visibleItemsInfo.lastOrNull()?.let { lastVisibleItem ->
                val isLastItemFullyVisible = lastVisibleItem.offset + lastVisibleItem.size <= layoutInfo.viewportEndOffset
                // The last visible item is not the last item in the list or it's not fully visible
                (lastVisibleItem.index + 1 < layoutInfo.totalItemsCount) || !isLastItemFullyVisible
            } ?: false
        }
    }
    
    EdgeFadeBase(
        modifier = modifier,
        colorScheme = colorScheme,
        showStartEdgeFade = showStartEdgeFade,
        showEndEdgeFade = showEndEdgeFade,
        orientation = orientation,
        fadeSize = fadeSize,
        content = listContent
    )
}

@Composable
fun EdgeFadeBase(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    showStartEdgeFade: Boolean,
    showEndEdgeFade: Boolean,
    orientation: Orientation,
    fadeSize: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        content()
        
        AnimatedEdgeFade(
            colorScheme = colorScheme,
            isVisible = showStartEdgeFade,
            orientation = orientation,
            isStartEdge = true,
            fadeSize = fadeSize
        )
        
        AnimatedEdgeFade(
            colorScheme = colorScheme,
            isVisible = showEndEdgeFade,
            orientation = orientation,
            isStartEdge = false,
            fadeSize = fadeSize
        )
    }
}


@Composable
fun BoxScope.AnimatedEdgeFade(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    isVisible: Boolean,
    orientation: Orientation,
    isStartEdge: Boolean,
    fadeSize: Dp
) {
    val alignment by remember(orientation, isStartEdge) {
        derivedStateOf {
            when {
                orientation == Orientation.Horizontal && isStartEdge ->
                    Alignment.CenterStart
                
                orientation == Orientation.Horizontal && !isStartEdge ->
                    Alignment.CenterEnd
                
                orientation == Orientation.Vertical && isStartEdge ->
                    Alignment.TopCenter
                
                else ->
                    Alignment.BottomCenter
            }
        }
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .align(alignment)
            .then(
                when (orientation) {
                    Orientation.Horizontal -> Modifier.width(fadeSize)
                    Orientation.Vertical -> Modifier.height(fadeSize)
                }
            )
    ) {
        val colors = listOf(
            if (isStartEdge) colorScheme.background else Color.Transparent,
            if (isStartEdge) Color.Transparent else colorScheme.background
        )
        
        Box(
            modifier = Modifier
                .background(
                    when (orientation) {
                        Orientation.Horizontal ->
                            Brush.horizontalGradient(colors = colors)
                        
                        Orientation.Vertical ->
                            Brush.verticalGradient(colors = colors)
                    }
                )
                .then(
                    when (orientation) {
                        Orientation.Horizontal ->
                            Modifier.fillMaxHeight()
                        
                        Orientation.Vertical ->
                            Modifier.fillMaxWidth()
                    }
                )
        )
    }
}