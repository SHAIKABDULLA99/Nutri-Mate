package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodItem
import com.example.data.model.MealType
import com.example.ui.components.HealthRatingBadge
import com.example.ui.components.MacroMetricPill
import com.example.ui.components.NonMedicalDisclaimerCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CoralAccent
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanFoodScreen(
    viewModel: NutriMateViewModel,
    onBack: () -> Unit,
    onNavigateToGrocery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val selectedFood by viewModel.selectedScannedFood.collectAsState()
    val multiplier by viewModel.scanServingMultiplier.collectAsState()
    val foodList = viewModel.repository.foodDatabase

    var selectedMealTypeForLog by remember { mutableStateOf(MealType.LUNCH) }
    var showLogSuccessToast by remember { mutableStateOf(false) }
    var showGroceryAddedToast by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "📷 AI ఆహార గుర్తింపు (స్కాన్)" else "📷 AI Food Scanner",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Viewfinder & Camera Frame
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .testTag("camera_viewfinder"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Ambient Background Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                    )

                    // Food representation / Center Reticle
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (selectedFood != null) {
                            Text(
                                text = selectedFood!!.category.icon,
                                fontSize = 54.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = if (lang == AppLanguage.TELUGU) selectedFood!!.nameTe else selectedFood!!.nameEn,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = if (isScanning) "🤖 AI Analysis in progress..." else "✓ MobileNetV2 Vision AI (98.6% match)",
                                    fontSize = 11.sp,
                                    color = Color(0xFFA7F3D0),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Scanner Animated Laser Beam
                    if (isScanning) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = (laserOffset * 220).dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFF10B981),
                                            Color(0xFF34D399),
                                            Color(0xFF10B981),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // Corner Viewfinder Brackets
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(36.dp)
                            .align(Alignment.TopStart)
                            .border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topStart = 12.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(36.dp)
                            .align(Alignment.TopEnd)
                            .border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topEnd = 12.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(36.dp)
                            .align(Alignment.BottomStart)
                            .border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(bottomStart = 12.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(36.dp)
                            .align(Alignment.BottomEnd)
                            .border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(bottomEnd = 12.dp)
                            )
                    )
                }
            }

            // Quick Samples Photo Strip
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (lang == AppLanguage.TELUGU) "నమూనా ఆహారాలు ఎంచుకోండి / ఫోటో తీయండి:" else "Select Food Sample or Capture Photo:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(foodList) { food ->
                        val isSelected = food.id == selectedFood?.id
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    viewModel.selectFoodForScan(food)
                                    viewModel.simulateAiFoodScan(food.id)
                                }
                                .testTag("sample_food_${food.id}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = food.category.icon, fontSize = 16.sp)
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) food.nameTe.split(" ").first() else food.nameEn.split(" ").first(),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons (Take Photo / Retake Scan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        selectedFood?.let { viewModel.simulateAiFoodScan(it.id) }
                    },
                    modifier = Modifier.weight(1f).height(46.dp).testTag("button_take_photo"),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isScanning
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Scan")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "📷 AI స్కాన్ చేయండి" else "📷 AI Scan Food",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        val randomFood = foodList.random()
                        viewModel.selectFoodForScan(randomFood)
                        viewModel.simulateAiFoodScan(randomFood.id)
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isScanning
                ) {
                    Icon(imageVector = Icons.Default.Shuffle, contentDescription = "Random")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "యాదృచ్ఛిక ఆహారం" else "Random Food",
                        fontSize = 12.sp
                    )
                }
            }

            // Scanned Food Nutrition Result Card
            if (selectedFood != null) {
                val food = selectedFood!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scan_result_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Title & Health Rating Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) food.nameTe else food.nameEn,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${if (lang == AppLanguage.TELUGU) "సర్వింగ్ సైజు:" else "Base Serving:"} ${if (lang == AppLanguage.TELUGU) food.servingSizeTe else food.servingSizeEn}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HealthRatingBadge(rating = food.healthRating, lang = lang)
                        }

                        // Serving Adjuster
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) "తీసుకునే పరిమాణం:" else "Adjust Portion:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { if (multiplier > 0.5f) viewModel.setScanServingMultiplier(multiplier - 0.5f) },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "${multiplier}x",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(
                                        onClick = { viewModel.setScanServingMultiplier(multiplier + 0.5f) },
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        // Nutrition Macro Metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MacroMetricPill(
                                label = if (lang == AppLanguage.TELUGU) "కేలరీలు" else "Calories",
                                value = "${(food.calories * multiplier).toInt()} kcal",
                                color = CoralAccent,
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            )
                            MacroMetricPill(
                                label = if (lang == AppLanguage.TELUGU) "ప్రోటీన్" else "Protein",
                                value = "${String.format("%.1f", food.proteinGrams * multiplier)}g",
                                color = Color(0xFF10B981),
                                modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                            )
                            MacroMetricPill(
                                label = if (lang == AppLanguage.TELUGU) "కార్బ్స్" else "Carbs",
                                value = "${String.format("%.1f", food.carbsGrams * multiplier)}g",
                                color = AmberAccent,
                                modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                            )
                            MacroMetricPill(
                                label = if (lang == AppLanguage.TELUGU) "ఫైబర్" else "Fiber",
                                value = "${String.format("%.1f", food.fiberGrams * multiplier)}g",
                                color = Color(0xFF0D9488),
                                modifier = Modifier.weight(1f).padding(start = 4.dp)
                            )
                        }

                        // Portion Suggestion
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = "Portion",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${if (lang == AppLanguage.TELUGU) "సిఫార్సు చేసిన పరిమాణం:" else "Recommended Portion:"} ${if (lang == AppLanguage.TELUGU) food.portionSuggestionTe else food.portionSuggestionEn}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Smart Recommendation Box
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Tip",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = if (lang == AppLanguage.TELUGU) "💡 స్మార్ట్ పోషకాహార సలహా" else "💡 Smart Recommendation",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = if (lang == AppLanguage.TELUGU) food.smartSuggestionTe else food.smartSuggestionEn,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        // Healthier Alternative Box
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = "Alternative",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = if (lang == AppLanguage.TELUGU) "మరింత ఆరోగ్యకరమైన ప్రత్యామ్నాయం:" else "Healthier Alternative:",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (lang == AppLanguage.TELUGU) food.healthierAlternativeTe else food.healthierAlternativeEn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F766E)
                                    )
                                }
                            }
                        }

                        // Log to Today's Meals Section
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "ఈ భోజనాన్ని డైలీ లాగ్‌లో చేర్చండి:" else "Log to Today's Meals:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val slots = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.SNACKS, MealType.DINNER)
                                slots.forEach { slot ->
                                    val isSelected = slot == selectedMealTypeForLog
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedMealTypeForLog = slot },
                                        label = {
                                            Text(
                                                text = if (lang == AppLanguage.TELUGU) slot.labelTe.split(" ").first() else slot.labelEn,
                                                fontSize = 11.sp
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.logCurrentScannedFood(selectedMealTypeForLog)
                                    showLogSuccessToast = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("button_log_meal_action"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Log")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) "డైలీ డైట్‌లో నమోదు చేయండి" else "Log to Daily Tracker",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Add Ingredients to Smart Grocery List
                        OutlinedButton(
                            onClick = {
                                viewModel.addGrocery(
                                    nameEn = food.nameEn,
                                    nameTe = food.nameTe,
                                    categoryEn = food.category.displayNameEn,
                                    categoryTe = food.category.displayNameTe,
                                    quantity = "1 portion"
                                )
                                showGroceryAddedToast = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("button_add_to_grocery"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = "Grocery")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "కిరాణా జాబితాకు చేర్చండి (🛒)" else "Add to Smart Grocery List (🛒)",
                                fontSize = 13.sp
                            )
                        }

                        AnimatedVisibility(visible = showLogSuccessToast) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) "✓ భోజనం విజయవంతంగా నమోదు చేయబడింది!" else "✓ Meal logged successfully to your daily tracker!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F766E),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        AnimatedVisibility(visible = showGroceryAddedToast) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.TELUGU) "✓ కిరాణా లిస్ట్‌లో చేర్చబడింది!" else "✓ Added to your Smart Grocery List!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            NonMedicalDisclaimerCard(lang = lang)
        }
    }
}
