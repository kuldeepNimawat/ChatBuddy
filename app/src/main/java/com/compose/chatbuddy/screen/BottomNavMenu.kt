package com.compose.chatbuddy.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.compose.chatbuddy.data.BottomNavigationMenuItems
import com.compose.chatbuddy.util.navigateTo
import java.time.format.TextStyle

@Composable
fun BottomNavMenu(
    selectedItem: BottomNavigationMenuItems,
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(top = 5.dp)
                .fillMaxWidth()
                .wrapContentHeight().align(Alignment.BottomCenter)
                .background(Color.White),
            verticalAlignment = Alignment.Bottom
        ) {
            for (item in BottomNavigationMenuItems.values()) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(90.dp)
                        .padding(5.dp)
                        .weight(1f)
                        .clickable {
                            navigateTo(navController, item.navDestination.route)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Image(
                        painter = painterResource(id = item.icon), contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp)
                            .weight(1f),
                        colorFilter = if (item == selectedItem)
                            ColorFilter.tint(Color.Blue)
                        else
                            ColorFilter.tint(Color.Gray)
                    )

                    Text(
                        text = item.tabName, modifier = Modifier
                            .padding(5.dp)
                            .wrapContentSize(),
                        fontSize = 16.sp,
                        color = if (item == selectedItem) Color.Blue else Color.Gray
                    )
                }

            }
        }
    }
}