package com.example.runalyze.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.runalyze.BottomNavItem
import com.example.runalyze.R
import com.example.runalyze.utils.Destination


data class HomeOption(
    val id: Int,
    val image: Int = R.drawable.running_red,
    val text: String,
    val route: String
)

// Home screen element object
object Options {
    val optionList = listOf(
        HomeOption(
            id = 1,
            image = R.drawable.running_red,
            text = "Start running",
            route = Destination.CurrentRun.route
        ),
        HomeOption(
            id = 2,
            image = R.drawable.running_blue,
            text = "Running plan recommendation",
            route = BottomNavItem.Planning.route
        ),
        HomeOption(
            id = 3,
            image = R.drawable.running_dark,
            text = "Set a goal",
            route = Destination.AddGoal.route
        )
    )
}


@Composable
// Home screen
fun Home(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            items(Options.optionList) { option ->
                HomeItem(option = option, navController = navController)
            }
        }
    }
}

@Composable
// Home item
fun HomeItem(option: HomeOption, navController: NavController) {
    Box(
        modifier = Modifier
            .height(250.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = option.image), contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate(option.route)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )

            ) {
                Text(text = option.text, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    HomeItem(
        option = HomeOption(
            id = 1,
            image = R.drawable.running_red,
            text = "Start a quick run",
            route = Destination.CurrentRun.route
        ), navController = rememberNavController()
    )
}