package com.hrithik.taskmanager.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.google.gson.Gson
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.data.PreferencesManager
import com.hrithik.taskmanager.data.TaskDao
import com.hrithik.taskmanager.di.ApplicationScope
import com.hrithik.taskmanager.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var taskDao: TaskDao

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var preferencesManager: PreferencesManager

    //val sortOrder = state.get<SortOrder>("sortOrder")

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        if (appWidgetIds != null) {
            for (i in appWidgetIds) {
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

                val serviceIntent = Intent(context, WidgetService::class.java)
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i)
                serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

                applicationScope.launch {
                    val preferences = preferencesManager.preferencesFlow.first()
                    val l = taskDao.getTasks("", preferences.sortOrder).first()
                    val list = ArrayList(l)
                    val gson = Gson()
                    val json = gson.toJson(list)
                    serviceIntent.putExtra("tasks", json)
                    val view = RemoteViews(context?.packageName, R.layout.layout_widget)
                    view.setOnClickPendingIntent(R.id.text, pendingIntent)
                    view.setRemoteAdapter(R.id.listView, serviceIntent)
                    appWidgetManager?.updateAppWidget(i, view)
                }

            }
        }
    }
}