package com.rechinx.notedown.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.RelativeLayout
import android.widget.RemoteViews
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.rechinx.notedown.BuildConfig
import com.rechinx.notedown.Constants

import com.rechinx.notedown.R
import com.rechinx.notedown.repository.NoteDatabase
import com.rechinx.notedown.support.expandtededittext.DataImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

/**
 * Implementation of App Widget functionality.
 */
class NoteAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        val TAG = this.javaClass.simpleName

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val widgetText = context.getString(R.string.appwidget_text)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.note_app_widget)

            var text: String
            CompositeDisposable().add(NoteDatabase.getInstance(context).noteDao().getNotes()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if(!it.isEmpty()) {
                            text = it[0].detail.toString()
                            views.setTextViewText(R.id.appwidget_text, calcText(text))
                            // Instruct the widget manager to update the widget
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        }
                    }, { e -> Log.d(TAG, "cannot get. $e")}))
        }

        fun calcText(content: String): String {
            if(TextUtils.isEmpty(content)) {
                return content
            }
            var ret = ""
            val pattern = Pattern.compile(Constants.IMAGE_REGEX)
            var tmp = content
            while(true) {
                val localMatcher = pattern.matcher(tmp)
                if(!localMatcher.find()) break

                val st = localMatcher.start()
                val ed = localMatcher.end()

                if(st > 0) {
                    ret += tmp.substring(0, st) + "\n"
                }

                ret += "[图片]" + "\n"
                tmp = tmp.substring(ed)
            }
            if(!TextUtils.isEmpty(tmp)) {
                ret += tmp
            }
            return ret
        }
    }
}

