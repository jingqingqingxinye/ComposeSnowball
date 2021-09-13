package com.example.composestudy.snowball.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.composestudy.R
import com.example.composestudy.ui.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.window.DialogProperties
import com.example.composestudy.MainActions
import com.example.composestudy.snowball.home.data.HomeViewModel
import com.example.composestudy.snowball.home.data.PostData
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch



@ExperimentalPagerApi
@Composable
fun HomeActivityPage(viewModel: HomeViewModel, actions: MainActions) {
    TabContentScreen(
        recItems = viewModel.recItems,
        followItems = viewModel.followItems,
        hotItems = viewModel.hotItems,
        actions = actions
    )
}


@ExperimentalPagerApi
@Composable
private fun TabContentScreen(recItems: List<PostData>,
                             followItems: List<PostData>,
                             hotItems: List<PostData>,
                             actions: MainActions) {
    val pages = listOf("关注", "推荐", "热门")
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
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    color = Color.White
                )
            },
            backgroundColor = Color.White,
            modifier = Modifier.width(210.dp),
            divider = {
                TabRowDefaults.Divider(color = Color.White)
            }
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
                        .height(50.dp)
                        .background(Color.White),
                selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Gray
                ) {
                    Text(title,
                        maxLines = 1,
                        fontSize = if (pagerState.currentPage == index) 20.sp else 16.sp,
                        fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                modifier = Modifier.clickable {

            }) {
                val curItems = if (pagerState.currentPage == 0) {
                    followItems
                } else if (pagerState.currentPage == 1) {
                    recItems
                } else {
                    item {
                        FastNewsCard()
                    }
                    hotItems
                }
                items(items = curItems) {
                    PostCardView(itemData = it, actions = actions)
                }
            }
        }
    }
}


//@Preview
//@Composable
//fun ShowPostItemView() {
//    val itemData = generateRandomPostItem()
//    PostCardView(itemData = itemData, {  })
//}

/**
 * 帖子卡片模版
 */

@Composable
private fun PostCardView(itemData: PostData, actions: MainActions) {
    Column(modifier = Modifier
        .padding(top = 16.dp)
        .clickable {
            actions.toStockDetail()
        }) {
        Row(verticalAlignment = Alignment.Top) {
            TextWithBackground(string = itemData.stockId)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(itemData.userName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(itemData.publishTime, fontSize = 12.sp,
                    color = Color.Gray)
            }
        }
        Text(text = itemData.postTitle,
            fontSize = 17.sp, style = MaterialTheme.typography.h6,
            color = Color.Black,
            modifier = Modifier.padding(top = 9.dp, bottom = 7.dp))
        Text(text = itemData.postContent,
            fontSize = 15.sp, style = MaterialTheme.typography.body1,
            color = Color.DarkGray)
        Image(
            painter = painterResource(id = R.drawable.header),
            contentDescription = null,
            modifier = Modifier
                .height(100.dp)
                .width(160.dp)
                .clip(shape = RoundedCornerShape(4.dp))
                .padding(top = 8.dp),
            contentScale = ContentScale.Crop
        )
        PostFooter()
        Divider(color = Gray_979797, thickness = 0.3.dp)
    }
}


/**
 * 带背景的text
 */
@Composable
private fun TextWithBackground(string: String) {
    // Text 不能直接设置文案垂直居中，需要包裹一层
    Row(modifier = Modifier
        .size(40.dp, 40.dp)
        .background(color = Red_FFC0CB, shape = Shapes.medium),
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = string,
            color = Color.Red,
            modifier = Modifier
                .width(40.dp)
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            fontSize = 10.sp)
    }
}

/**
 * footer组件
 */

@Preview
@Composable
fun PostFooter() {
    // 约束布局使用
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(top = 10.dp, bottom = 10.dp)) {
        // 创建约束参考对象
        val (imageForward, text1, imageComment, imageLike, imageMore) = createRefs()
        val guideline = createGuidelineFromStart(fraction = 0.16f)
        Image(
            painter = painterResource(id = R.drawable.status_forward),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(imageForward) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Text(text = "1", color = Color.Gray,
            modifier = Modifier
                .padding(start = 2.dp)
                .constrainAs(text1) {
                    start.linkTo(imageForward.end)
                    top.linkTo(imageForward.top)
                    bottom.linkTo(imageForward.bottom)
                })
        Image(
            painter = painterResource(id = R.drawable.status_comment),
            contentDescription = null,
            modifier = Modifier
                .width(80.dp)
                .constrainAs(imageComment) {
                    start.linkTo(guideline)
                    baseline.linkTo(imageForward.baseline, margin = 0.dp)
                }
        )
        Image(
            painter = painterResource(id = R.drawable.status_like),
            contentDescription = null,
            modifier = Modifier
                .width(80.dp)
                .constrainAs(imageLike) {
                    start.linkTo(imageComment.end)
                    baseline.linkTo(imageComment.baseline, margin = 0.dp)
                }
        )
        val openDialog = remember { mutableStateOf(false) }
        Image(
            painter = painterResource(id = R.drawable.ic_feed_foot_icon_more),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(imageMore) {
                    end.linkTo(parent.end)
                    baseline.linkTo(imageComment.baseline, margin = 0.dp)
                }
                .clickable(onClick = { openDialog.value = !openDialog.value })
        )
        ShowAlertDialog(openDialog)
    }
}


@Composable
fun TextCenter(string: String, size: TextUnit) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {
        Text(text = string, fontSize = size)
    }
}

@Preview
@Composable
fun FastNewsCard() {
    Card(modifier = Modifier
        .fillMaxWidth(), elevation = 2.dp, backgroundColor = Color.White, border = BorderStroke(0.3.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
                horizontalArrangement =  Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.snowball_fast_news_logo), contentDescription = null)
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(40.dp)
                        .height(20.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(9.dp))) {
                    Text(text = "收起", fontSize = 10.sp)
                }
            }
            FastNewsItem()
            FastNewsItem()
            FastNewsItem()
        }
    }
}

@Preview
@Composable
private fun FastNewsItem() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "17:54",
            fontSize = 12.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            "",
            Modifier
                .size(6.dp)
                .background(color = Color.Red, shape = RoundedCornerShape(3.dp))
        )
        Text(
            text = "舍得酒业：预计上半年实现净利润7.1亿元-7.5亿元，同比增长332.42%-356.78%；受国内疫情逐步得到控制，中高端白酒消费市场明显回暖，公司老酒战略逐步被市场接受等原因影响，公司营业收入实现较大幅度增长。",
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}


@Composable
fun ShowAlertDialog(isShow: MutableState<Boolean>){
    if (isShow.value) {
        AlertDialog(
            onDismissRequest = {
                isShow.value = false
            },
            buttons = {
                Row {
                    Button(
                        onClick = {
                            isShow.value = false
                        },
                        modifier = Modifier.weight(1f,true),
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    ) {
                        Text(text = "取消")
                    }
                    Button(
                        onClick = {
                            isShow.value = false
                        },
                        modifier = Modifier.weight(1f,true),
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    ) {
                        Text(text = "确定")
                    }
                }
            },
            title = {
                TextCenter(string = "提示", 17.sp)
            },
            text = {
                TextCenter(string = "还未实现该功能", 15.sp)
            },
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.White,
            contentColor = Color.Black,
            properties = DialogProperties(),
            modifier = Modifier.width(260.dp)
        )
    }
}









