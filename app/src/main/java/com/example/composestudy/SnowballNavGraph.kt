package com.example.composestudy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.composestudy.snowball.home.HomeActivityPage
import com.example.composestudy.snowball.home.data.HomeViewModel
import com.example.composestudy.snowball.stockchart.StockDetailPage
import com.google.accompanist.pager.ExperimentalPagerApi


/**
 * Destinations used in the ([SnowballActivity]).
 */
object MainDestinations {
    const val HOME_PAGE_HOT = "home_page_hot"
    const val STOCK_DETAIL_PAGE = "stock_detail_page"
}

@ExperimentalPagerApi
@Composable
fun SnowballNavGraph(viewModel: HomeViewModel, startDestination: String = MainDestinations.HOME_PAGE_HOT) {
    val navController = rememberNavController()
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.HOME_PAGE_HOT) {
            HomeActivityPage(viewModel, actions)
        }
        composable(MainDestinations.STOCK_DETAIL_PAGE) { backStackEntry ->
//            val arguments = requireNotNull(backStackEntry.arguments)
//            val parcelable = arguments.getString(STOCK_DETAIL_PAGE_URL)
//            val fromJson = Gson().fromJson(parcelable, Article::class.java)
            StockDetailPage(onBack = actions.upPress)
        }
    }
}


/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {

    val homePage: () -> Unit = {
        navigate(navController, MainDestinations.HOME_PAGE_HOT)
    }
    val toStockDetail: () -> Unit = {
        navigate(navController, MainDestinations.STOCK_DETAIL_PAGE)
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }

    private fun navigate(navController: NavHostController, route: String) {
        navController.navigate(route)
    }
}