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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rescue Guide",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
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

                // Emergency Alert Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Emergency Access",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Quick access to emergency services",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        TextButton(
                            onClick = { navController.navigate(Screen.Emergency.route) }
                        ) {
                            Text("ACCESS")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Quick Actions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(getQuickActions()) { action ->
                        QuickActionCard(
                            title = action.title,
                            icon = action.icon,
                            onClick = { navController.navigate(action.route) }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Recent Guides",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(getRecentGuides()) { guide ->
                GuideItemCard(
                    title = guide.title,
                    description = guide.description,
                    category = guide.category,
                    priority = guide.priority,
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

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp, 100.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun GuideItemCard(
    title: String,
    description: String,
    category: String,
    priority: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (category) {
                    "medical" -> Icons.Default.LocalHospital
                    "fire" -> Icons.Default.LocalFireDepartment
                    "water" -> Icons.Default.Water
                    else -> Icons.Default.Book
                },
                contentDescription = category,
                modifier = Modifier.size(40.dp),
                tint = when (priority) {
                    1 -> MaterialTheme.colorScheme.error
                    2 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Text(
                    text = category.uppercase(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go to guide"
            )
        }
    }
}

// Sample data - Replace with actual data from ViewModel
data class QuickAction(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)
data class GuideItem(val id: String, val title: String, val description: String, val category: String, val priority: Int)

fun getQuickActions() = listOf(
    QuickAction("First Aid", Icons.Default.MedicalServices, Screen.Guides.route),
    QuickAction("Fire Safety", Icons.Default.LocalFireDepartment, Screen.Guides.route),
    QuickAction("Water Rescue", Icons.Default.Water, Screen.Guides.route),
    QuickAction("Call 911", Icons.Default.Phone, Screen.Emergency.route)
)

fun getRecentGuides() = listOf(
    GuideItem("1", "CPR Basics", "Learn life-saving CPR techniques", "medical", 1),
    GuideItem("2", "Fire Extinguisher Use", "How to properly use different types of fire extinguishers", "fire", 1),
    GuideItem("3", "Earthquake Safety", "Steps to take during an earthquake", "natural_disaster", 2),
    GuideItem("4", "Water Rescue Basics", "Basic water rescue techniques", "water", 2)
)