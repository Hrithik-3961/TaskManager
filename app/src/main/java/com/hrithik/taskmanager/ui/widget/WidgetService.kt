package com.hrithik.taskmanager.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.data.Tasks
import java.lang.reflect.Type


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
            val gson = Gson()
            val myType: Type = object : TypeToken<ArrayList<Tasks>>() {}.type
            list = gson.fromJson(intent.getStringExtra("tasks"), myType)
        }

        override fun onCreate() {
        }

        override fun onDataSetChanged() {

        }

        override fun onDestroy() {

        }

        override fun getCount() = list.size

        override fun getViewAt(p0: Int): RemoteViews {
            val view = RemoteViews(context.packageName, R.layout.item_widget)
            view.setTextViewText(R.id.itemText, list[p0].task)
            return view
        }

        override fun getLoadingView() = null

        override fun getViewTypeCount() = 1

        override fun getItemId(p0: Int) = p0.toLong()

        override fun hasStableIds() = true
    }
}