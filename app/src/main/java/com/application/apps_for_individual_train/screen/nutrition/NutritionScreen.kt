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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Meal(
    val type: String,
    val time: String,
    val calories: Int,
    val foods: List<Food>
)

data class Food(
    val name: String,
    val calories: Int,
    val proteins: Int,
    val carbs: Int,
    val fats: Int
)

@Composable
fun NutritionScreen() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    
    var showAddMealDialog by remember { mutableStateOf(false) }
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    
    // Nutrition data state variables
    var waterIntake by remember { mutableStateOf(0) }
    var waterGoal by remember { mutableStateOf(2500) }
    var proteins by remember { mutableStateOf(0) }
    var carbs by remember { mutableStateOf(0) }
    var fats by remember { mutableStateOf(0) }
    var caloriesConsumed by remember { mutableStateOf(0) }
    var caloriesGoal by remember { mutableStateOf(2000) }
    
    // Load user nutrition data if user is logged in
    LaunchedEffect(userId) {
        if (userId != null) {
            val userNutritionRef = FirebaseDatabase.getInstance().getReference("user_nutrition").child(userId)
            userNutritionRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        waterIntake = snapshot.child("water_intake").getValue(Int::class.java) ?: 0
                        waterGoal = snapshot.child("water_goal").getValue(Int::class.java) ?: 2500
                        proteins = snapshot.child("proteins").getValue(Int::class.java) ?: 0
                        carbs = snapshot.child("carbs").getValue(Int::class.java) ?: 0
                        fats = snapshot.child("fats").getValue(Int::class.java) ?: 0
                        caloriesConsumed = snapshot.child("calories_consumed").getValue(Int::class.java) ?: 0
                        caloriesGoal = snapshot.child("calories_goal").getValue(Int::class.java) ?: 2000
                        
                        // Load meals data
                        val mealsSnapshot = snapshot.child("meals")
                        if (mealsSnapshot.exists()) {
                            val mealsList = mutableListOf<Meal>()
                            for (mealSnapshot in mealsSnapshot.children) {
                                val type = mealSnapshot.child("type").getValue(String::class.java) ?: ""
                                val time = mealSnapshot.child("time").getValue(String::class.java) ?: ""
                                val calories = mealSnapshot.child("calories").getValue(Int::class.java) ?: 0
                                
                                val foodsList = mutableListOf<Food>()
                                val foodsSnapshot = mealSnapshot.child("foods")
                                for (foodSnapshot in foodsSnapshot.children) {
                                    val name = foodSnapshot.child("name").getValue(String::class.java) ?: ""
                                    val foodCalories = foodSnapshot.child("calories").getValue(Int::class.java) ?: 0
                                    val foodProteins = foodSnapshot.child("proteins").getValue(Int::class.java) ?: 0
                                    val foodCarbs = foodSnapshot.child("carbs").getValue(Int::class.java) ?: 0
                                    val foodFats = foodSnapshot.child("fats").getValue(Int::class.java) ?: 0
                                    
                                    foodsList.add(Food(name, foodCalories, foodProteins, foodCarbs, foodFats))
                                }
                                
                                mealsList.add(Meal(type, time, calories, foodsList))
                            }
                            meals = mealsList
                        }
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
        }
    }

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
                WaterTrackingCard(waterIntake = waterIntake, waterGoal = waterGoal)
            }
            
            // Карточка с калориями
            item {
                CaloriesCard(caloriesConsumed = caloriesConsumed, caloriesGoal = caloriesGoal)
            }
            
            // Карточка с макроэлементами
            item {
                MacronutrientsCard(proteins = proteins, carbs = carbs, fats = fats)
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
            if (meals.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Нет данных о приемах пищи",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Добавьте прием пищи, чтобы отслеживать питание",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(meals.size) { index ->
                    val meal = meals[index]
                    MealCard(
                        mealType = meal.type,
                        calories = meal.calories,
                        time = meal.time,
                        foods = meal.foods
                    )
                }
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
                    val updatedMeals = meals + newMeal
                    meals = updatedMeals
                    
                    // Save the new meal to Firebase
                    if (userId != null) {
                        val userMealsRef = FirebaseDatabase.getInstance().getReference("user_nutrition")
                            .child(userId)
                            .child("meals")
                        
                        val mealKey = userMealsRef.push().key ?: return@AddMealDialog
                        
                        val mealMap = mapOf(
                            "type" to newMeal.type,
                            "time" to newMeal.time,
                            "calories" to newMeal.calories
                        )
                        
                        userMealsRef.child(mealKey).setValue(mealMap)
                        
                        // Update calories consumed
                        val userNutritionRef = FirebaseDatabase.getInstance().getReference("user_nutrition")
                            .child(userId)
                        
                        userNutritionRef.child("calories_consumed")
                            .setValue(caloriesConsumed + newMeal.calories)
                    }
                    
                    showAddMealDialog = false
                }
            )
        }
    }
}

@Composable
fun WaterTrackingCard(waterIntake: Int, waterGoal: Int) {
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
fun CaloriesCard(caloriesConsumed: Int, caloriesGoal: Int) {
    val remainingCalories = caloriesGoal - caloriesConsumed
    
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
                    value = caloriesGoal,
                    icon = Icons.Default.Flag,
                    color = MaterialTheme.colorScheme.primary
                )
                
                CalorieItem(
                    title = "Потреблено",
                    value = caloriesConsumed,
                    icon = Icons.Default.Restaurant,
                    color = MaterialTheme.colorScheme.tertiary
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
fun MacronutrientsCard(proteins: Int, carbs: Int, fats: Int) {
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
fun MealCard(mealType: String, calories: Int, time: String, foods: List<Food>) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = mealType,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = "$calories ккал",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                foods.forEach { food ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = food.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = "${food.calories} ккал",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealDialog(
    onDismiss: () -> Unit,
    onAddMeal: (Meal) -> Unit
) {
    var mealType by remember { mutableStateOf("Завтрак") }
    var calories by remember { mutableStateOf("") }
    var mealTime by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    var foodText by remember { mutableStateOf("") }
    var foods by remember { mutableStateOf<List<String>>(emptyList()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Добавить прием пищи",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Тип приема пищи
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    OutlinedTextField(
                        value = mealType,
                        onValueChange = { mealType = it },
                        label = { Text("Тип приема пищи") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Время
                OutlinedTextField(
                    value = mealTime,
                    onValueChange = { mealTime = it },
                    label = { Text("Время") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Калории
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Калории") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Продукты
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = foodText,
                        onValueChange = { foodText = it },
                        label = { Text("Продукт") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = {
                            if (foodText.isNotBlank()) {
                                foods = foods + foodText
                                foodText = ""
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Добавить продукт"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Список добавленных продуктов
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 120.dp)
                ) {
                    foods.forEach { food ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• $food",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Отмена")
                    }
                    
                    Button(
                        onClick = {
                            val caloriesInt = calories.toIntOrNull() ?: 0
                            
                            // Convert string food items to Food objects
                            val foodObjects = foods.map { foodName ->
                                // Estimate calories per food item (in a real app, this would be from a database)
                                val estimatedCalories = caloriesInt / foods.size
                                Food(
                                    name = foodName,
                                    calories = estimatedCalories,
                                    proteins = 0, // This would come from a food database in a real app
                                    carbs = 0,    // This would come from a food database in a real app
                                    fats = 0      // This would come from a food database in a real app
                                )
                            }
                            
                            val meal = Meal(
                                type = mealType,
                                calories = caloriesInt,
                                time = mealTime,
                                foods = foodObjects
                            )
                            
                            onAddMeal(meal)
                        },
                        enabled = calories.isNotBlank() && foods.isNotEmpty()
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
} 