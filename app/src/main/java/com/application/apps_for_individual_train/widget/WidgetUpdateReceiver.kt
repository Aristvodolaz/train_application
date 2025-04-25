package com.application.apps_for_individual_train.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Вспомогательный приемник для обработки обновлений виджета
 */
class WidgetUpdateReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "WidgetUpdateReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received intent with action: ${intent.action}")
        
        if (intent.action == WaterIntakeWidget.ACTION_UPDATE_WATER) {
            val amount = intent.getIntExtra(WaterIntakeWidget.EXTRA_WATER_AMOUNT, 0)
            Log.d(TAG, "Forwarding water update request with amount: $amount")
            
            // Пересылаем интент на основной виджет
            val widgetIntent = Intent(context, WaterIntakeWidget::class.java)
            widgetIntent.action = WaterIntakeWidget.ACTION_UPDATE_WATER
            widgetIntent.putExtra(WaterIntakeWidget.EXTRA_WATER_AMOUNT, amount)
            context.sendBroadcast(widgetIntent)
        }
    }
} 