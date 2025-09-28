@file:OptIn(ExperimentalMaterial3Api::class)

package com.mnp.resqme.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mnp.resqme.ui.components.RescueBottomNavigation
import com.mnp.resqme.ui.navigation.Screen
import com.mnp.resqme.util.getGuideCategories
import com.mnp.resqme.util.getGuidesByCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesScreen(
    navController: NavController
) {
    var selectedCategory by remember { mutableStateOf("all") }
    val categories = getGuideCategories()
    val guides = getGuidesByCategory(selectedCategory)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rescue Guides",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        bottomBar = {
            RescueBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Categories",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            onClick = { selectedCategory = category.id },
                            label = { Text(category.name) },
                            selected = selectedCategory == category.id,
                            leadingIcon = {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.name,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available Guides",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${guides.size} guides",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(guides) { guide ->
                GuideCard(
                    guide = guide,
                    onClick = {
                        navController.navigate(
                            Screen.GuideDetail.createRoute(guide.id)
                        )
                    }
                )
            }
        }
    }
}

