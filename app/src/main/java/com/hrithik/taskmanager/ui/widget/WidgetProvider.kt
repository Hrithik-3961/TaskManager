package com.hrithik.taskmanager.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.RemoteViews
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.data.PreferencesManager
import com.hrithik.taskmanager.data.Tasks
import com.hrithik.taskmanager.data.room.TaskDao
import com.hrithik.taskmanager.di.ApplicationScope
import com.hrithik.taskmanager.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@AndroidEntryPoint
class WidgetProvider : AppWidgetProvider() {

    companion object {
        lateinit var tasks: LiveData<List<Tasks>>
    }

    @Inject
    lateinit var taskDao: TaskDao

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope


    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        if (appWidgetIds != null) {

            for (appWidgetId in appWidgetIds) {
                val homeIntent = Intent(context, MainActivity::class.java)
                val pendingHomeIntent = PendingIntent.getActivity(context, 0, homeIntent, 0)

                val addIntent = Intent(context, MainActivity::class.java)
                val pendingAddIntent = PendingIntent.getActivity(context, 0, addIntent, 0)

                val serviceIntent = Intent(context, WidgetService::class.java)
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

                var view = RemoteViews(context?.packageName, R.layout.layout_widget_dark)

                when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        view =
                            RemoteViews(context.packageName, R.layout.layout_widget_dark)
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        view =
                            RemoteViews(context.packageName, R.layout.layout_widget_light)
                    }
                }
                view.setOnClickPendingIntent(R.id.text, pendingHomeIntent)
                view.setOnClickPendingIntent(R.id.add, pendingAddIntent)

                val preferencesFLow = preferencesManager.preferencesFlow
                val taskFlow = preferencesFLow.flatMapLatest { preferences ->
                    taskDao.getTasks(preferences.sortOrder)
                }

                tasks = taskFlow.asLiveData()

                tasks.observeForever {
                    view.setRemoteAdapter(R.id.listView, serviceIntent)
                    appWidgetManager?.updateAppWidget(appWidgetId, view)
                    appWidgetManager?.notifyAppWidgetViewDataChanged(
                        appWidgetId,
                        R.id.listView
                    )
                }
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val ids = intent?.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.listView)
        super.onReceive(context, intent)
    }
}