package ui.scanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import foundation.composeapp.generated.resources.Res
import foundation.composeapp.generated.resources.scanner_unsupported_text
import org.jetbrains.compose.resources.stringResource
import ui.BodyText

@Composable
actual fun CodeScannerLayout(
    modifier: Modifier,
    onVibrate: () -> Unit,
    onCompletion: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BodyText(
            text = stringResource(Res.string.scanner_unsupported_text)
        )
    }
}