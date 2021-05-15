package com.hrithik.taskmanager.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.google.gson.Gson
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.data.PreferencesManager
import com.hrithik.taskmanager.data.room.TaskDao
import com.hrithik.taskmanager.di.ApplicationScope
import com.hrithik.taskmanager.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH = "actionRefresh"
    }

    @Inject
    lateinit var taskDao: TaskDao

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        if (appWidgetIds != null) {
            for (appWidgetId in appWidgetIds) {
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

                val serviceIntent = Intent(context, WidgetService::class.java)
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

                val refreshIntent = Intent(context, MainActivity::class.java)
                val pending = PendingIntent.getBroadcast(
                    context,
                    100,
                    refreshIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                refreshIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

                val view =
                    RemoteViews(context?.packageName, R.layout.layout_widget)
                view.setOnClickPendingIntent(R.id.text, pendingIntent)
                view.setOnClickPendingIntent(R.id.refresh, pending)
                /*when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        view.setInt(R.id.listView, "setBackgroundResource", R.drawable.bottom_rounded_light)
                    }
                }*/

                applicationScope.launch {
                    val preferences = preferencesManager.preferencesFlow.first()
                    val l = taskDao.getTasks(preferences.sortOrder).first()

                    val list = ArrayList(l)
                    Log.d("tag1", preferences.sortOrder.name)
                    Log.d("tag2", list.toString())
                    val gson = Gson()
                    val json = gson.toJson(list)
                    serviceIntent.putExtra("tasks", json)
                    view.setRemoteAdapter(R.id.listView, serviceIntent)
                    appWidgetManager?.notifyAppWidgetViewDataChanged(
                        appWidgetId,
                        R.id.listView
                    )
                    appWidgetManager?.updateAppWidget(appWidgetId, view)
                }
            }
        }

    }

    /*override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("mytag", intent?.action!!)
        if (ACTION_REFRESH == intent.action) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView)
        }
        super.onReceive(context, intent)
    }*/
}