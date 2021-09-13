package com.example.composestudy.snowball.stockchart


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.composestudy.snowball.stockchart.utils.generateKLineData
import com.example.composestudy.snowball.stockchart.utils.generateMinuteTimeData
import com.example.composestudy.snowball.stockchart.views.KLineView
import com.example.composestudy.snowball.stockchart.views.MinuteTimeView
import com.example.composestudy.snowball.stockchart.views.StockChartHeaderView
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch



@ExperimentalPagerApi
@Composable
fun StockDetailPage(onBack: () -> Unit) {
    StockDetailScreen(onBack)

}

@ExperimentalPagerApi
@Composable
private fun StockDetailScreen(onBack: () -> Unit) {
    Scaffold {
        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth()) {
            StockChartHeaderView()
            StockDetailView(onBack)
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun StockDetailView(onBack: () -> Unit) {
    val pages = listOf("分时", "日K")
    Column {
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState(
            pageCount = pages.size,
            initialOffscreenLimit = 2,
        )
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    color = Color.Transparent
                )
            },
            modifier = Modifier.wrapContentWidth(),
            divider = {
                TabRowDefaults.Divider(color = Color.LightGray, thickness = 0.5.dp)
            },
            backgroundColor = Color.White,
            contentColor = Color.White
        ) {
            pages.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .wrapContentHeight()
                        .width(60.dp)
                        .background(Color.White)
                        .align(alignment = Alignment.Start),
                ) {
                    Text(title,
                        maxLines = 1,
                        fontSize = if (pagerState.currentPage == index) 16.sp else 14.sp,
                        fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                        color = Color.Black,
                    )
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            dragEnabled = false
        ) {
            if (pagerState.currentPage == 0) {
                MinuteTimeView(generateMinuteTimeData(LocalContext.current))
            } else if (pagerState.currentPage == 1) {
                KLineView(generateKLineData(LocalContext.current))
            }
        }
    }
}












