package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


private const val NOTIFICATION_ID = 0
/*
making an extension function on NotificationManager
 */

fun NotificationManager.sendNotification(msg :String,appContext: Context,channelId :String,fileName: String,status:String){

    val contentIntent = Intent(appContext,DetailActivity::class.java).apply {
        putExtra(appContext.getString(R.string.INTENT_EXTRA_NOTIFICATION_ID), NOTIFICATION_ID)
        putExtra(appContext.getString(R.string.INTENT_FILENAME_ID),fileName)
        putExtra(appContext.getString(R.string.INTENT_STATUS_ID),status)


    }
    val pendingIntent = PendingIntent.getActivity(
                                appContext,
                                NOTIFICATION_ID,
                                contentIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                                )


  /*
  //this is repeating so commenting it out and using previous pending intent in action button too
  val receiverIntent = Intent(appContext,DetailActivity::class.java).apply {
        putExtra(appContext.getString(R.string.INTENT_EXTRA_NOTIFICATION_ID), NOTIFICATION_ID)
    }
    val receiverPendingIntent = PendingIntent.getActivity(
                                appContext,
                                NOTIFICATION_ID,
                                receiverIntent,
                                PendingIntent.FLAG_ONE_SHOT
    )
*/
    val builder = NotificationCompat.Builder(appContext,channelId)
        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
        .setContentTitle(appContext.getString(R.string.notification_title))
        .setContentText(msg)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_assistant_black_24dp,
           appContext.getString(R.string.notification_button),
                                                pendingIntent) //using pending intent instead of receiverPendingIntent

    notify(NOTIFICATION_ID,builder.build())

}

fun NotificationManager.cancelNotification(){
    cancelAll()
}