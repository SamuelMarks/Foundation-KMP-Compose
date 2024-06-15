package screens.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import config.isPortraitMode
import data.MainViewModel
import data.decryptAndUncompress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
expect fun CodeScannerLayout(
    modifier: Modifier,
    onVibrate: () -> Unit,
    onCompletion: (String) -> Unit,
    onFailure: (String) -> Unit
)

@Composable
fun ScanCodeScreen(
    viewModel: MainViewModel,
    onVibrate: () -> Unit,
    onComplete: (result: String) -> Unit
) {
    val surfaceContainerColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.secondary
    val onPrimaryColor = MaterialTheme.colorScheme.onSecondary
    
    val isPortraitMode = isPortraitMode()
    val coroutineScope = rememberCoroutineScope()
    val receiveCodeScan: (String) -> Unit = {
        coroutineScope.launch(Dispatchers.Main) {
            val decryptedCode = it.decryptAndUncompress()
            viewModel.setSharedText(decryptedCode)
            onComplete(decryptedCode)
            onVibrate()
        }
    }
    
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CodeScannerLayout(
            modifier = Modifier
                .fillMaxSize(),
            onVibrate = onVibrate,
            onCompletion = receiveCodeScan,
            onFailure = receiveCodeScan // TODO: proper error handling (Toast/Snackbar)
        )
    }
}