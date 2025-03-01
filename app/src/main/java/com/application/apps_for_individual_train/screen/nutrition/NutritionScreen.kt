package com.application.apps_for_individual_train.screen.nutrition

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NutritionScreen() {
    var showAddMealDialog by remember { mutableStateOf(false) }
    var meals by remember { mutableStateOf(sampleMeals) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Питание и вода",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            
            // Карточка с отслеживанием воды
            item {
                WaterTrackingCard()
            }
            
            // Карточка с калориями
            item {
                CaloriesCard()
            }
            
            // Карточка с макроэлементами
            item {
                MacronutrientsCard()
            }
            
            // Приемы пищи
            item {
                Text(
                    text = "Приемы пищи",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            // Динамический список приемов пищи
            items(meals.size) { index ->
                val meal = meals[index]
                MealCard(
                    mealType = meal.type,
                    calories = meal.calories,
                    time = meal.time,
                    foods = meal.foods
                )
            }
            
            // Кнопка добавления приема пищи
            item {
                AddMealButton(onClick = { showAddMealDialog = true })
            }
            
            // Рекомендации
            item {
                NutritionTipsCard()
            }
        }
        
        // Диалог добавления приема пищи
        if (showAddMealDialog) {
            AddMealDialog(
                onDismiss = { showAddMealDialog = false },
                onAddMeal = { newMeal ->
                    meals = meals + newMeal
                    showAddMealDialog = false
                }
            )
        }
    }
}

@Composable
fun WaterTrackingCard() {
    var waterIntake by remember { mutableStateOf(1200) } // в мл
    val waterGoal = 2500 // в мл
    val waterProgress = waterIntake.toFloat() / waterGoal
    
    val animatedProgress by animateFloatAsState(
        targetValue = waterProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "water_progress"
    )
    
    // Сохраняем цвет перед использованием в Canvas
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val waterColor = Color(0xFF03A9F4) // Голубой цвет для воды
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Потребление воды",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "$waterIntake / $waterGoal мл",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 15.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    // Фоновый круг
                    drawCircle(
                        color = surfaceVariantColor,
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                    
                    // Прогресс
                    drawArc(
                        color = waterColor, // Голубой цвет для воды
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset(center.x - radius, center.y - radius)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Water",
                        tint = waterColor,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Text(
                        text = "${(waterProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WaterButton(
                    amount = 200,
                    onClick = { waterIntake = (waterIntake + 200).coerceAtMost(waterGoal) }
                )
                
                WaterButton(
                    amount = 300,
                    onClick = { waterIntake = (waterIntake + 300).coerceAtMost(waterGoal) }
                )
                
                WaterButton(
                    amount = 500,
                    onClick = { waterIntake = (waterIntake + 500).coerceAtMost(waterGoal) }
                )
            }
        }
    }
}

@Composable
fun WaterButton(amount: Int, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color(0xFF03A9F4),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = "+$amount мл",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CaloriesCard() {
    val consumedCalories = 1650
    val burnedCalories = 450
    val goalCalories = 2000
    val remainingCalories = goalCalories - consumedCalories + burnedCalories
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Калории",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CalorieItem(
                    title = "Цель",
                    value = goalCalories,
                    icon = Icons.Default.Flag,
                    color = MaterialTheme.colorScheme.primary
                )
                
                CalorieItem(
                    title = "Потреблено",
                    value = consumedCalories,
                    icon = Icons.Default.Restaurant,
                    color = MaterialTheme.colorScheme.tertiary
                )
                
                CalorieItem(
                    title = "Сожжено",
                    value = burnedCalories,
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFFF5722)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Осталось: $remainingCalories ккал",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CalorieItem(title: String, value: Int, icon: ImageVector, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MacronutrientsCard() {
    val proteins = 120 // г
    val carbs = 200 // г
    val fats = 60 // г
    
    val proteinCalories = proteins * 4
    val carbsCalories = carbs * 4
    val fatsCalories = fats * 9
    val totalCalories = proteinCalories + carbsCalories + fatsCalories
    
    val proteinPercentage = proteinCalories.toFloat() / totalCalories
    val carbsPercentage = carbsCalories.toFloat() / totalCalories
    val fatsPercentage = fatsCalories.toFloat() / totalCalories
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Макроэлементы",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroItem(
                    title = "Белки",
                    value = proteins,
                    percentage = proteinPercentage,
                    color = MaterialTheme.colorScheme.primary
                )
                
                MacroItem(
                    title = "Углеводы",
                    value = carbs,
                    percentage = carbsPercentage,
                    color = MaterialTheme.colorScheme.tertiary
                )
                
                MacroItem(
                    title = "Жиры",
                    value = fats,
                    percentage = fatsPercentage,
                    color = Color(0xFFFF9800)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Полоса с процентным соотношением
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(proteinPercentage)
                        .background(MaterialTheme.colorScheme.primary)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(carbsPercentage)
                        .background(MaterialTheme.colorScheme.tertiary)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(fatsPercentage)
                        .background(Color(0xFFFF9800))
                )
            }
        }
    }
}

@Composable
fun MacroItem(title: String, value: Int, percentage: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value г",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "${(percentage * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MealCard(mealType: String, calories: Int, time: String, foods: List<String>) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (mealType) {
                            "Завтрак" -> Icons.Default.FreeBreakfast
                            "Обед" -> Icons.Default.LunchDining
                            "Ужин" -> Icons.Default.DinnerDining
                            else -> Icons.Default.Restaurant
                        },
                        contentDescription = mealType,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = mealType,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "$calories ккал",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Column {
                foods.forEach { food ->
                    Text(
                        text = "• $food",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddMealButton(onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(56.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add meal",
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "Добавить прием пищи",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NutritionTipsCard() {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Советы по питанию",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "• Пейте больше воды между приемами пищи",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            
            Text(
                text = "• Увеличьте потребление белка для лучшего восстановления мышц",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            
            Text(
                text = "• Ешьте больше овощей и фруктов для получения витаминов",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            
            Text(
                text = "• Ограничьте потребление сахара и обработанных продуктов",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

// Модель данных для приема пищи
data class Meal(
    val type: String,
    val calories: Int,
    val time: String,
    val foods: List<String>
)

// Примеры приемов пищи
val sampleMeals = listOf(
    Meal(
        type = "Завтрак",
        calories = 450,
        time = "08:30",
        foods = listOf(
            "Овсянка с фруктами - 300 ккал",
            "Яйцо вареное - 70 ккал",
            "Кофе с молоком - 80 ккал"
        )
    ),
    Meal(
        type = "Обед",
        calories = 650,
        time = "13:00",
        foods = listOf(
            "Куриная грудка - 200 ккал",
            "Рис бурый - 150 ккал",
            "Салат овощной - 100 ккал",
            "Хлеб цельнозерновой - 100 ккал",
            "Яблоко - 100 ккал"
        )
    ),
    Meal(
        type = "Ужин",
        calories = 550,
        time = "19:00",
        foods = listOf(
            "Рыба запеченная - 250 ккал",
            "Овощи на пару - 100 ккал",
            "Творог обезжиренный - 100 ккал",
            "Чай зеленый - 0 ккал"
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealDialog(onDismiss: () -> Unit, onAddMeal: (Meal) -> Unit) {
    var mealType by remember { mutableStateOf("Перекус") }
    var mealTime by remember { mutableStateOf("12:00") }
    var totalCalories by remember { mutableStateOf("0") }
    var foodItems by remember { mutableStateOf("") }
    
    val mealTypes = listOf("Завтрак", "Обед", "Ужин", "Перекус")
    var expandedMealType by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Добавить прием пищи",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Выбор типа приема пищи
                ExposedDropdownMenuBox(
                    expanded = expandedMealType,
                    onExpandedChange = { expandedMealType = it }
                ) {
                    OutlinedTextField(
                        value = mealType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Тип приема пищи") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMealType)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedMealType,
                        onDismissRequest = { expandedMealType = false }
                    ) {
                        mealTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = type) },
                                onClick = {
                                    mealType = type
                                    expandedMealType = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Время приема пищи
                OutlinedTextField(
                    value = mealTime,
                    onValueChange = { mealTime = it },
                    label = { Text("Время (HH:MM)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Калории
                OutlinedTextField(
                    value = totalCalories,
                    onValueChange = { 
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            totalCalories = it
                        }
                    },
                    label = { Text("Общие калории") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Список продуктов
                OutlinedTextField(
                    value = foodItems,
                    onValueChange = { foodItems = it },
                    label = { Text("Продукты (каждый с новой строки)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val calories = totalCalories.toIntOrNull() ?: 0
                            val foods = foodItems.split("\n")
                                .filter { it.isNotBlank() }
                                .map { it.trim() }
                            
                            val newMeal = Meal(
                                type = mealType,
                                calories = calories,
                                time = mealTime,
                                foods = foods
                            )
                            
                            onAddMeal(newMeal)
                        },
                        enabled = foodItems.isNotBlank() && totalCalories.isNotBlank()
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
} 