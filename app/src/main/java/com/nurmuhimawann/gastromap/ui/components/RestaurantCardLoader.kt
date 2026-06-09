package com.nurmuhimawann.gastromap.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nurmuhimawann.gastromap.ui.theme.GastroMapTheme

@Composable
fun RestaurantCardLoader(
    modifier: Modifier = Modifier
) {
    Card(

        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 150.dp,
                        max = 200.dp
                    )
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = true
                        )
                    )
            )
            Spacer(
                modifier = Modifier.size(8.dp)
            )
            Column {
                Box(
                    modifier = Modifier
                        .background(
                            shimmerBrush(
                                targetValue = 1300f,
                                showShimmer = true
                            )
                        )
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
                Box(
                    modifier = Modifier
                        .background(
                            shimmerBrush(
                                targetValue = 1300f,
                                showShimmer = true
                            )
                        )
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
                Box(
                    modifier = Modifier
                        .background(
                            shimmerBrush(
                                targetValue = 1300f,
                                showShimmer = true
                            )
                        )
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
                Row {
                    Box(
                        modifier = Modifier
                            .background(
                                shimmerBrush(
                                    targetValue = 1300f,
                                    showShimmer = true
                                )
                            )
                            .width(100.dp)
                            .height(20.dp)
                    )
                }
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
                Box(
                    modifier = Modifier
                        .background(
                            shimmerBrush(
                                targetValue = 1300f,
                                showShimmer = true
                            )
                        )
                        .width(50.dp)
                        .height(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RestaurantCardLoaderPreview() {
    GastroMapTheme {
        RestaurantCardLoader()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RestaurantCardLoaderDarkPreview() {
    GastroMapTheme {
        RestaurantCardLoader()
    }
}
