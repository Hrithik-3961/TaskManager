package com.hrithik.taskmanager.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.data.Tasks


class WidgetService : RemoteViewsService() {

    override fun onGetViewFactory(p0: Intent): RemoteViewsFactory {
        return WidgetItemFactory(applicationContext, p0)
    }

    class WidgetItemFactory(
        val context: Context,
        private val intent: Intent
    ) : RemoteViewsFactory {

        private val appWidgetId: Int
        private var list = ArrayList<Tasks>()

        init {
            appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        override fun onCreate() {
            Handler(Looper.getMainLooper()).post {
                WidgetProvider.tasks.observeForever {
                    list = ArrayList(it)
                }
            }

        }

        override fun onDataSetChanged() {
            Handler(Looper.getMainLooper()).post {
                WidgetProvider.tasks.observeForever {
                    list = ArrayList(it)
                }
            }

        }

        override fun onDestroy() {

        }

        override fun getCount() = list.size

        override fun getViewAt(p0: Int): RemoteViews {
            val view = RemoteViews(context.packageName, R.layout.item_widget)
            view.setTextViewText(R.id.itemText, list[p0].task)
            if (list[p0].dateTime.isNotBlank())
                view.setTextViewText(R.id.dateTimeText, list[p0].dateTime)
            else
                view.setViewVisibility(R.id.dateTimeLinear, View.GONE)

            when (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    view.setInt(R.id.linear, "setBackgroundColor", Color.WHITE)
                    view.setTextColor(R.id.itemText, Color.BLACK)
                    view.setTextColor(R.id.dateTimeText, Color.BLACK)
                    view.setInt(R.id.btn, "setBackgroundColor", Color.BLACK)
                }
            }
            return view
        }

        override fun getLoadingView() = null

        override fun getViewTypeCount() = 1

        override fun getItemId(p0: Int) = p0.toLong()

        override fun hasStableIds() = true
    }
}