package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GroceryItem
import com.example.ui.components.NonMedicalDisclaimerCard
import com.example.ui.components.SectionHeader
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(
    viewModel: NutriMateViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val groceryItems by viewModel.groceryItems.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }

    val pendingCount = groceryItems.count { !it.isBought }
    val boughtCount = groceryItems.count { it.isBought }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) "🛒 స్మార్ట్ కిరాణా & ప్యాంట్రీ" else "🛒 Smart Grocery & Pantry",
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
                actions = {
                    if (boughtCount > 0) {
                        IconButton(
                            onClick = { viewModel.clearCompletedGroceries() },
                            modifier = Modifier.testTag("clear_bought_groceries")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear bought",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddItemDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_grocery")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
        ) {
            // Status & Quick Health Staples Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "కొనవలసిన వస్తువులు: $pendingCount" else "Pending to Buy: $pendingCount",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "పూర్తయినవి: $boughtCount" else "In Pantry: $boughtCount",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        FilledTonalButton(
                            onClick = { viewModel.seedHealthyGroceryStaples() },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "+ ఆరోగ్యకరమైనవి" else "+ Healthy Staples",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Grocery Item List
            if (groceryItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = "🛒", fontSize = 42.sp)
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "కిరాణా జాబితా ఖాళీగా ఉంది" else "Grocery list is empty",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (lang == AppLanguage.TELUGU) "ఆహార స్కాన్ నుండి లేదా కింద ఉన్న బటన్ ద్వారా పోషకాహార వస్తువులను చేర్చండి." else "Add healthy ingredients from food scans or tap '+' to start planning your balanced pantry.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(groceryItems, key = { it.id }) { item ->
                    GroceryItemRow(
                        item = item,
                        lang = lang,
                        onToggleBought = { viewModel.toggleGroceryBought(item.id, item.isBought) },
                        onDelete = { viewModel.deleteGrocery(item.id) }
                    )
                }
            }

            item {
                NonMedicalDisclaimerCard(lang = lang)
            }
        }
    }

    if (showAddItemDialog) {
        AddGroceryItemDialog(
            lang = lang,
            onAdd = { nameEn, nameTe, catEn, catTe, qty ->
                viewModel.addGrocery(nameEn, nameTe, catEn, catTe, qty)
                showAddItemDialog = false
            },
            onDismiss = { showAddItemDialog = false }
        )
    }
}

@Composable
fun GroceryItemRow(
    item: GroceryItem,
    lang: AppLanguage,
    onToggleBought: () -> Unit,
    onDelete: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (item.isBought) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
        label = "item_bg"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleBought() }
            .testTag("grocery_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isBought) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = item.isBought,
                    onCheckedChange = { onToggleBought() },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )

                Column {
                    Text(
                        text = if (lang == AppLanguage.TELUGU) item.nameTe else item.nameEn,
                        fontSize = 15.sp,
                        fontWeight = if (item.isBought) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (item.isBought) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (item.isBought) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${if (lang == AppLanguage.TELUGU) item.categoryTe else item.categoryEn} • ${item.quantity}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddGroceryItemDialog(
    lang: AppLanguage,
    onAdd: (String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameEn by remember { mutableStateOf("") }
    var nameTe by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1 kg") }
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    val categories = listOf(
        Pair("Fresh Vegetables & Greens", "తాజా కూరగాయలు & ఆకుకూరలు"),
        Pair("High Protein & Legumes", "ప్రోటీన్ ఆహారాలు & పప్పులు"),
        Pair("Dairy & Probiotics", "పాల ఉత్పత్తులు & పెరుగు"),
        Pair("Millets & Whole Grains", "చిరుధాన్యాలు & మిల్లెట్స్"),
        Pair("Fresh Fruits", "తాజా పండ్లు")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == AppLanguage.TELUGU) "కిరాణా వస్తువు చేర్చండి" else "Add Grocery Item",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text(if (lang == AppLanguage.TELUGU) "వస్తువు పేరు (English)" else "Item Name (English)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nameTe,
                    onValueChange = { nameTe = it },
                    label = { Text(if (lang == AppLanguage.TELUGU) "వస్తువు పేరు (తెలుగు)" else "Item Name (Telugu)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(if (lang == AppLanguage.TELUGU) "పరిమాణం (ఉదా: 500g, 1kg, 2 bunches)" else "Quantity (e.g. 500g, 1kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameEn.isNotBlank() || nameTe.isNotBlank()) {
                        val cat = categories[selectedCategoryIndex]
                        onAdd(
                            if (nameEn.isBlank()) nameTe else nameEn,
                            if (nameTe.isBlank()) nameEn else nameTe,
                            cat.first,
                            cat.second,
                            quantity
                        )
                    }
                }
            ) {
                Text(if (lang == AppLanguage.TELUGU) "చేర్చండి" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (lang == AppLanguage.TELUGU) "రద్దు" else "Cancel")
            }
        }
    )
}
