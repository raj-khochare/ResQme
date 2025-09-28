@file:OptIn(ExperimentalMaterial3Api::class)

package com.mnp.resqme.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.mnp.resqme.data.models.GuideItem
import com.mnp.resqme.util.getCategoryIcon
import com.mnp.resqme.util.getGuideById


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideCard(
    guide: GuideItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = guide.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = guide.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2
                    )
                }

                // Priority indicator
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (guide.priority) {
                        1 -> MaterialTheme.colorScheme.errorContainer
                        2 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                ) {
                    Text(
                        text = when (guide.priority) {
                            1 -> "HIGH"
                            2 -> "MED"
                            else -> "LOW"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = when (guide.priority) {
                            1 -> MaterialTheme.colorScheme.error
                            2 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getCategoryIcon(guide.category),
                        contentDescription = guide.category,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = guide.category.replace("_", " ").uppercase(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Duration",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "${guide.estimatedTime} min",
//                        fontSize = 12.sp,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
                }
            }
        }
    }
}

@Composable
fun StepCard(
    stepNumber: Int,
    stepText: String,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isCompleted) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = stepNumber.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stepText,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete() }
            )
        }
    }
}

@Composable
fun GuideDetailScreen(
    navController: NavController,
    guideId: String
) {
    // In a real app, this would come from a ViewModel
    val guide = getGuideById(guideId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = guide.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Bookmark */ }) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Bookmark")
                    }
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
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

                // Guide Header Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when (guide.priority) {
                            1 -> MaterialTheme.colorScheme.errorContainer
                            2 -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (guide.priority) {
                                        1 -> Icons.Default.PriorityHigh
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = "Priority",
                                    tint = when (guide.priority) {
                                        1 -> MaterialTheme.colorScheme.error
                                        2 -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (guide.priority) {
                                        1 -> "HIGH PRIORITY"
                                        2 -> "MEDIUM PRIORITY"
                                        else -> "STANDARD GUIDE"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = when (guide.priority) {
                                        1 -> MaterialTheme.colorScheme.error
                                        2 -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                )
                            }

                            Text(
                                text = "${guide.estimatedTime} min",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = guide.description,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                if (guide.priority == 1) {
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
                                contentDescription = "Warning",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "This is a high-priority emergency procedure. If this is a life-threatening situation, call 112 immediately.",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Step-by-Step Instructions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            itemsIndexed(guide.steps) { index, step ->
                StepCard(
                    stepNumber = index + 1,
                    stepText = step,
                    isCompleted = false,
                    onToggleComplete = { /* Toggle step completion */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
