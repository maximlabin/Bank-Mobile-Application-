package com.labin.piggybank.compose.navigation

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey

@Composable
fun NavigationBar(
    selectedKey: NavKey,
    modifier: Modifier = Modifier,
    onSelectedKey: (NavKey) -> Unit
) {
//    BottomAppBar(
//        modifier = modifier,
//
//    ) {
//        NavigationBarItem(
//
//        )
//    }
}