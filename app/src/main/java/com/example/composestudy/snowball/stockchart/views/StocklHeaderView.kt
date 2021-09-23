package com.example.composestudy.snowball.stockchart.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composestudy.ui.Color_666666
import com.example.composestudy.ui.Red_F54346
import com.example.composestudy.ui.Red_FF8776

@Preview
@Composable
fun StockChartHeaderView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth()
            .padding(CHART_PADDING)) {
        Row(modifier = Modifier
            .wrapContentWidth()
            .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "317.5", fontSize = 36.sp, color = Red_F54346)
            Text(text = "+6.9", fontSize = 14.sp,
                modifier = Modifier.padding(start = 10.dp),
                color = Red_F54346
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "高 318.68", fontSize = 14.sp,
                modifier = Modifier.weight(1f), color = Color_666666
            )
            Text(text = "开 312", fontSize = 14.sp,
                modifier = Modifier.weight(1f), color = Color_666666
            )
            Text(text = "量 100万手", fontSize = 14.sp,
                modifier = Modifier.weight(1f), color = Color_666666
            )
            Text(text = "总市值 900亿", fontSize = 14.sp,
                modifier = Modifier.weight(1f), color = Color_666666
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "低 311", fontSize = 14.sp,
                modifier = Modifier.weight(1f), color = Color_666666
            )
            Text(text = "换 3.2%", fontSize = 14.sp,
                modifier = Modifier.weight(1f), color = Color_666666
            )
            Text(text = "额 60亿", fontSize = 14.sp,
                modifier = Modifier.weight(1f), color = Color_666666
            )
            Text(text = "市盈TTM 7.82", fontSize = 14.sp,
                modifier = Modifier.weight(1f), color = Color_666666
            )
        }
    }
}


@Preview
@Composable
fun ChartHeadView() {
    Row(modifier = Modifier
        .padding(start = CHART_PADDING, end = CHART_PADDING, top = CHART_PADDING)
        .wrapContentHeight()) {
        Text(text = "均价:316", fontSize = 14.sp, color = Red_FF8776)
        Text(text = "最新:317.5 +5.67 + 5.6%", fontSize = 14.sp,
            modifier = Modifier.padding(start = 10.dp), color = Red_F54346)
    }
}