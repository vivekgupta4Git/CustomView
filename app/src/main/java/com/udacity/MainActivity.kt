package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.net.URL


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var URL : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        findViewById<RadioGroup>(R.id.group1).setOnCheckedChangeListener { _, i ->
            when( i )
            {
                R.id.option1 ->
                    URL = GLIDE_URL
                R.id.option2 -> URL = LOAD_APP_URL
                R.id.option3 -> URL = RETROFIT_URL
            }

        }


/*
        Registering broadcast receiver in onCreate()  as
        DownloadManager sends ACTION_DOWNLOAD_COMPLETE broadcast action
        when download complete

 */
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


//setting on clickListener on custom Button
        custom_button.setOnClickListener {
            if(URL==null)
            {
                Toast.makeText(this,"Please Select an option first", Toast.LENGTH_SHORT).show()
                custom_button.updateButtonState(ButtonState.Clicked)
                getNotificationManagerInstance(applicationContext).cancelNotification()
                return@setOnClickListener
            }

            //cancel any previous notification
            getNotificationManagerInstance(applicationContext).cancelNotification()
            //download files
            download()
        }

        //creating channel for notification
        createChannel(CHANNEL_ID,applicationContext.getString(R.string.myChannelName))

   }

    fun getNotificationManagerInstance(appContext: Context) : NotificationManager{
        val nm = ContextCompat.getSystemService(appContext,NotificationManager::class.java)
                as NotificationManager
        return nm
    }
    /*
    Our app will be notified by system using this intent
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //Fetching the download id received with the broadcast
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //checking if the received broadcast is for our enqueued download by
            //matching download id

            if(downloadID == id)
            {
                custom_button.updateButtonState(ButtonState.Clicked)
                Toast.makeText(context,"Download Complete",Toast.LENGTH_SHORT).show()
                val nm = getNotificationManagerInstance(applicationContext)

                nm.sendNotification(applicationContext.getString(R.string.notification_description),applicationContext, CHANNEL_ID)


            }
            else{
                Toast.makeText(context,"Download Failed",Toast.LENGTH_SHORT).show()
                custom_button.updateButtonState(ButtonState.Completed)
            }
        }
    }

    /*
    Downloading files using DownloadManager,
    prepare DownloadManager.Request which contains all the information to request
    a new download ,URI is the only required only parameter , if file destination isn't provided
    shared volume will be used (space of which can be reclaimed by system)
     */
    @SuppressLint("Range")
    private fun download() {

        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        downloadID = downloadManager.enqueue(request) // enqueue puts the download request in the queue.
        custom_button.updateButtonState(ButtonState.Loading)


        /*enqueue request returns a unique long id which will act as identifier
        for the download,
        * */
/*
        //using query method
        var finishDownload = false
        var progress : Int

        while (!finishDownload) {
           val cursor =  downloadManager.query( DownloadManager.Query().setFilterById(downloadID));
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                     DownloadManager.STATUS_FAILED-> {
                        finishDownload = true;
                        break;
                    }
                    DownloadManager.STATUS_RUNNING-> {
                        val total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        if (total >= 0) {
                            val downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            progress =  ((downloaded * 100L) / total).toInt();
                            // Don't forget to do the division in long to get more digits rather than double.
                            //  publishProgress((int) ((downloaded * 100L) / total));
                        }
                        break;
                    }
                    DownloadManager.STATUS_SUCCESSFUL-> {
                        progress = 100;
                       // publishProgress(100);
                        finishDownload = true;
                        break;
                    }
                }
            }
        }*/
    }




    companion object {

        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"

        private const val CHANNEL_ID = "channelId"
    }



    fun createChannel(channelId :String,channelName :String){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)

            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(false)

            val nm= getNotificationManagerInstance(applicationContext)
            nm.createNotificationChannel(notificationChannel)


        }
    }

}



//Copied from https://medium.com/@aungkyawmyint_26195/downloading-file-properly-in-android-d8cc28d25aca
//To understand the download Manager
//This is query method which can be embedded in download function further for progress value

/*

boolean finishDownload = false;
int progress;
while (!finishDownload) {
  Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadID));
  if (cursor.moveToFirst()) {
    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
    switch (status) {
      case DownloadManager.STATUS_FAILED: {
        finishDownload = true;
        break;
      }
      case DownloadManager.STATUS_PAUSED:
        break;
      case DownloadManager.STATUS_PENDING:
        break;
      case DownloadManager.STATUS_RUNNING: {
        final long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        if (total >= 0) {
          final long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
          progress = (int) ((downloaded * 100L) / total);
          // if you use downloadmanger in async task, here you can use like this to display progress.
          // Don't forget to do the division in long to get more digits rather than double.
          //  publishProgress((int) ((downloaded * 100L) / total));
        }
        break;
      }
      case DownloadManager.STATUS_SUCCESSFUL: {
        progress = 100;
        // if you use aysnc task
        // publishProgress(100);
        finishDownload = true;
        break;
      }
    }
  }
}
 */