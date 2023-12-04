package com.example.lab_5


import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.lab_5.R

class appWidjet : AppWidgetProvider(){
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Обновление виджета
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Создаем RemoteViews объект для работы с макетом виджета
            val views = RemoteViews(context.packageName, R.layout.widget_layout)  // Замените на ваш путь к ресурсам

            // TODO: Здесь нужно получить последнее сообщение и установить его текст в TextView
            val lastMessage = getLastMessage(context) // Метод, который получает последнее сообщение
            views.setTextViewText(R.id.widgetMessageTextView, "Last Message: $lastMessage") // Замените на ваш путь к ресурсам

            // Обновляем виджет
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // Метод для получения последнего сообщения (замените его на ваш логику)
        private fun getLastMessage(context: Context): String {
            // Здесь нужно взять последнее сообщение из вашего источника данных (например, Firebase)
            // В данном примере просто возвращаем фиксированный текст
            return "Hello, Widget!"
        }
    }
}