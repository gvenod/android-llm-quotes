package com.homefinch.quot

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat.getLocales
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

fun GetPrompt(addonString: String): String {
    var custom_prompt = "Act as an experienced mentor and motivation speaker for young " +
            "adult as target audience. " +
            "Quote should be short, positive and encouraging. " +
            "Generate a random quote. "
    var postfix = "Output message in english."
    if(!addonString.isEmpty()) {
        postfix = addonString
    }
    custom_prompt +=  addonString

    Log.v("quote", custom_prompt)
    return custom_prompt
}

@Composable
fun QuoteScreen(
    quotViewModel: QuotViewModel = viewModel()
) {
    val placeholderResult = stringResource(R.string.results_placeholder)
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by quotViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val system_lang: String = context.resources.configuration.getLocales().toString()
    //val currentTime: String? = SimpleDateFormat("HH:mm:ss", context.resources.configuration.getLocales().getDefault().format(Date())
    val tz: TimeZone = TimeZone.getDefault()
    val addonString : String = "Output message should be less than 25 words and in language " + system_lang +
            " and relevant to TimeZone   " + tz.getDisplayName( false, TimeZone.SHORT ) +
            " and Timezone id " + tz.id

    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = result,
                textAlign = TextAlign.Center,
            )
        }

        Box (contentAlignment = Alignment.CenterEnd) {
            Button(
                onClick = {
                    quotViewModel.sendPrompt(GetPrompt(addonString))
                },
            ) {
                Text(
                    text = stringResource(R.string.action_go)
                )
            }
        }

        if (uiState is UiState.Initial) {
            quotViewModel.sendPrompt(GetPrompt(addonString))
        } else if (uiState is UiState.Loading) {
            //CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            CircularProgressIndicator()
        } else {
            //var textColor = MaterialTheme.colorScheme.onSurface
            if (uiState is UiState.Error) {
                //textColor = MaterialTheme.colorScheme.error
                result = (uiState as UiState.Error).errorMessage
            } else if (uiState is UiState.Success) {
                //textColor = MaterialTheme.colorScheme.onSurface
                result = (uiState as UiState.Success).outputText
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun QuoteScreenPreview() {
    QuoteScreen()
}