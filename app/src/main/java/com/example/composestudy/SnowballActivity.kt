package com.example.composestudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.composestudy.snowball.home.data.HomeViewModel


import com.example.composestudy.ui.SnowballTheme
import com.example.composestudy.snowball.home.util.generateRandomPostItem
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.insets.ProvideWindowInsets


class SnowballActivity : ComponentActivity() {
    private val postViewModel by viewModels<HomeViewModel>()

    @ExperimentalPagerApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnowballTheme {
                ProvideWindowInsets {
                    Scaffold {
                        initPostViewModel()
                        SnowballNavGraph(viewModel = postViewModel)
                    }
                }
            }
        }
    }

    @Composable
    private fun initPostViewModel() {
        postViewModel.addRecItem(generateRandomPostItem())
        postViewModel.addRecItem(generateRandomPostItem())
        postViewModel.addRecItem(generateRandomPostItem())
        postViewModel.addRecItem(generateRandomPostItem())
        postViewModel.addRecItem(generateRandomPostItem())

        postViewModel.addFollowItem(generateRandomPostItem())
        postViewModel.addFollowItem(generateRandomPostItem())
        postViewModel.addFollowItem(generateRandomPostItem())
        postViewModel.addFollowItem(generateRandomPostItem())
        postViewModel.addFollowItem(generateRandomPostItem())

        postViewModel.addHotItem(generateRandomPostItem())
        postViewModel.addHotItem(generateRandomPostItem())
        postViewModel.addHotItem(generateRandomPostItem())
        postViewModel.addHotItem(generateRandomPostItem())
        postViewModel.addHotItem(generateRandomPostItem())
    }
}








