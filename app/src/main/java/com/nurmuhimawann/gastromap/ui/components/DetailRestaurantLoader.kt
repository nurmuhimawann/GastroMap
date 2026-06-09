package com.nurmuhimawann.gastromap.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nurmuhimawann.gastromap.ui.theme.GastroMapTheme

@Composable
fun DetailRestaurantLoader(
    modifier: Modifier = Modifier,
) {
    Column(
    modifier = modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(
                    min = 250.dp,
                    max = 300.dp
                )
                .background(
                    shimmerBrush(
                        targetValue = 1300f,
                        showShimmer = true
                    )
                )
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Box(
                modifier = modifier
                    .width(300.dp)
                    .heightIn(
                        min = 30.dp,
                        max = 30.dp
                    )
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = true
                        )
                    )
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Box(
                modifier = modifier
                    .width(300.dp)
                    .heightIn(
                        min = 20.dp,
                        max = 30.dp
                    )
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = true
                        )
                    )
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 100.dp,
                        max = 100.dp
                    )
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = true
                        )
                    )
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Box(
                modifier = modifier
                    .width(100.dp)
                    .heightIn(
                        min = 20.dp,
                        max = 30.dp
                    )
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = true
                        )
                    )
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Row {
                val categoryCount = 3
                repeat(categoryCount) {
                    Box(
                        modifier = modifier
                            .width(80.dp)
                            .heightIn(
                                min = 20.dp,
                                max = 30.dp
                            )
                            .background(
                                shimmerBrush(
                                    targetValue = 1300f,
                                    showShimmer = true
                                )
                            )
                    )
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                }
            }
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Box(
                modifier = modifier
                    .width(100.dp)
                    .heightIn(
                        min = 20.dp,
                        max = 30.dp
                    )
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = true
                        )
                    )
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Row {
                val menuCount = 3
                repeat(menuCount) {
                    Box(
                        modifier = modifier
                            .width(100.dp)
                            .heightIn(
                                min = 100.dp
                            )
                            .clip(
                                RoundedCornerShape(16.dp)
                            )
                            .background(
                                shimmerBrush(
                                    targetValue = 1300f,
                                    showShimmer = true
                                )
                            )
                    )
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                }
            }
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Box(
                modifier = modifier
                    .width(100.dp)
                    .heightIn(
                        min = 20.dp,
                        max = 30.dp
                    )
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = true
                        )
                    )
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Row {
                val drinkCount = 3
                repeat(drinkCount) {
                    Box(
                        modifier = modifier
                            .width(100.dp)
                            .heightIn(
                                min = 100.dp
                            )
                            .clip(
                                RoundedCornerShape(16.dp)
                            )
                            .background(
                                shimmerBrush(
                                    targetValue = 1300f,
                                    showShimmer = true
                                )
                            )
                    )
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                }
            }
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Box(
                modifier = modifier
                    .width(100.dp)
                    .height(20.dp)
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = true
                        )
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 125.dp,
                        max = 125.dp
                    )
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = true
                        )
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailRestaurantLoaderPreview() {
    GastroMapTheme {
        DetailRestaurantLoader()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DetailRestaurantLoaderDarkPreview() {
    GastroMapTheme {
        DetailRestaurantLoader()
    }
}
