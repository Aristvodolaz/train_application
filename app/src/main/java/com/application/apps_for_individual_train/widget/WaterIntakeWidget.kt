package com.application.apps_for_individual_train.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.application.apps_for_individual_train.R
import com.application.apps_for_individual_train.activity.StartActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WaterIntakeWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_UPDATE_WATER = "com.application.apps_for_individual_train.ACTION_UPDATE_WATER"
        const val EXTRA_WATER_AMOUNT = "water_amount"
        private const val TAG = "WaterIntakeWidget"

        fun updateWidgets(context: Context) {
            val intent = Intent(context, WaterIntakeWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, WaterIntakeWidget::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(TAG, "onUpdate called for ${appWidgetIds.size} widgets")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        Log.d(TAG, "onReceive called with action: ${intent.action}")
        
        if (intent.action == ACTION_UPDATE_WATER) {
            val waterAmount = intent.getIntExtra(EXTRA_WATER_AMOUNT, 0)
            Log.d(TAG, "Updating water intake with amount: $waterAmount")
            updateWaterIntake(context, waterAmount)
        }
    }

    private fun updateWaterIntake(context: Context, amount: Int) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val userId = user.uid
        Log.d(TAG, "Updating water intake for user: $userId")

        val userNutritionRef = FirebaseDatabase.getInstance().getReference("user_nutrition").child(userId)
        
        userNutritionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val currentIntake = snapshot.child("water_intake").getValue(Int::class.java) ?: 0
                    val waterGoal = snapshot.child("water_goal").getValue(Int::class.java) ?: 2500
                    
                    val newIntake = (currentIntake + amount).coerceAtMost(waterGoal)
                    Log.d(TAG, "Current intake: $currentIntake, New intake: $newIntake")
                    
                    userNutritionRef.child("water_intake").setValue(newIntake)
                        .addOnSuccessListener {
                            Log.d(TAG, "Database update successful")
                            // Update all widgets after successful database update
                            updateWidgets(context)
                        }
                        .addOnFailureListener { error ->
                            Log.e(TAG, "Error updating database: ${error.message}")
                        }
                } else {
                    Log.e(TAG, "Nutrition data does not exist")
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database operation cancelled: ${error.message}")
            }
        })
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.water_intake_widget)
        
        try {
            // Set pending intent to open app when widget is clicked
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, StartActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent)
            
            // Set up buttons click actions
            setButtonClickAction(context, views, R.id.btn_add_200ml, 200)
            setButtonClickAction(context, views, R.id.btn_add_300ml, 300)
            setButtonClickAction(context, views, R.id.btn_add_500ml, 500)
            
            // Update widget data if user is logged in
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                loadUserData(context, user.uid, views, appWidgetManager, appWidgetId)
            } else {
                // Default values for logged out state
                views.setTextViewText(R.id.water_amount, "0 / 2500 мл")
                views.setProgressBar(R.id.water_progress, 100, 0, false)
                appWidgetManager.updateAppWidget(appWidgetId, views)
                Log.d(TAG, "User not logged in, showing default values")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun setButtonClickAction(context: Context, views: RemoteViews, buttonId: Int, amount: Int) {
        try {
            val intent = Intent(context, WaterIntakeWidget::class.java).apply {
                action = ACTION_UPDATE_WATER
                putExtra(EXTRA_WATER_AMOUNT, amount)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                buttonId, // Use button ID as request code to make PendingIntents unique
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            
            views.setOnClickPendingIntent(buttonId, pendingIntent)
            Log.d(TAG, "Button click action set for button: $buttonId with amount: $amount")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting button click action: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun loadUserData(
        context: Context,
        userId: String,
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val userNutritionRef = FirebaseDatabase.getInstance().getReference("user_nutrition").child(userId)
        
        userNutritionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.exists()) {
                        val waterIntake = snapshot.child("water_intake").getValue(Int::class.java) ?: 0
                        val waterGoal = snapshot.child("water_goal").getValue(Int::class.java) ?: 2500
                        
                        val progress = ((waterIntake.toFloat() / waterGoal) * 100).toInt()
                        
                        views.setTextViewText(R.id.water_amount, "$waterIntake / $waterGoal мл")
                        views.setProgressBar(R.id.water_progress, 100, progress, false)
                        
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                        Log.d(TAG, "Widget updated with water intake: $waterIntake/$waterGoal, progress: $progress%")
                    } else {
                        Log.e(TAG, "User nutrition data not found")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading user data: ${e.message}")
                    e.printStackTrace()
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database operation cancelled: ${error.message}")
            }
        })
    }
} 