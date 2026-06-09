package com.nurmuhimawann.gastromap.ui.screen.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.nurmuhimawann.gastromap.common.Constants
import com.nurmuhimawann.gastromap.data.Result
import com.nurmuhimawann.gastromap.di.Injection
import com.nurmuhimawann.gastromap.ui.components.ErrorText
import com.nurmuhimawann.gastromap.ui.components.SimpleTopBar
import com.nurmuhimawann.gastromap.ui.components.shimmerBrush

@Composable
fun AboutScreen(
    viewModel: AboutScreenViewModel = viewModel(
        factory = Injection.provideViewModelFactory(
            LocalContext.current
        )
    ), isInDarkMode: Boolean, onToggleDarkMode: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AboutContent(
        uiState = uiState,
        onToggleDarkMode = onToggleDarkMode,
        isInDarkMode = isInDarkMode
    )
}

@Composable
fun AboutContent(
    uiState: AboutUiState,
    onToggleDarkMode: () -> Unit,
    isInDarkMode: Boolean,
) {
    Scaffold(
        topBar = {
            SimpleTopBar(
                title = "About",
                onToggleDarkMode = onToggleDarkMode,
                isInDarkMode = isInDarkMode
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (val result = uiState.result) {
                is Result.Loading -> {
                    // Loader
                }
                is Result.Success -> {
                    val user = result.data
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        SubcomposeAsyncImage(
                            model = user.avatarUrl,
                            contentDescription = user.name,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .width(200.dp)
                                .heightIn(200.dp)
                                .clip(CircleShape)
                        ) {
                            val state = painter.state
                            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                                Box(
                                    modifier = Modifier
                                        .background(shimmerBrush())
                                        .size(64.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                SubcomposeAsyncImageContent()
                            }
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = Constants.myEmail,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                is Result.Error -> {
                    ErrorText(
                        error = result.errorMessage
                    )
                }
            }
        }
    }
}
