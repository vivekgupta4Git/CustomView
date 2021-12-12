package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.net.URL


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var URL : String? = null
    private lateinit var  downloadManager : DownloadManager
    private var fileName :String = "No File"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        findViewById<RadioGroup>(R.id.group1).setOnCheckedChangeListener { _, i ->
            when( i )
            {
                R.id.option1 -> {
                    URL = GLIDE_URL
                    fileName = "Glide File"
                }                R.id.option2 -> {
                URL = LOAD_APP_URL
                fileName = "Load App File"
            }
                R.id.option3 ->
                { URL = RETROFIT_URL
                    fileName = "Retrofil File"
                }
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
            custom_button.updateButtonState(ButtonState.Loading)
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
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            //getting instance of Notification Manager
            val nm = getNotificationManagerInstance(applicationContext)

            //Fetching the download id received with the broadcast
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)


        val cursor: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID!!))

            if(cursor.moveToFirst())
            {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if(status == DownloadManager.STATUS_SUCCESSFUL)
                {
                    Toast.makeText(context,"Download Complete",Toast.LENGTH_SHORT).show()
                    custom_button.updateButtonState(ButtonState.Clicked)
                    nm.sendNotification(applicationContext.getString(R.string.notification_description),applicationContext, CHANNEL_ID,fileName,"Success")

                }
                else if(status ==DownloadManager.STATUS_FAILED)
                {
                    Toast.makeText(context,"Download Failed",Toast.LENGTH_SHORT).show()
                    nm.sendNotification(applicationContext.getString(R.string.notification_description),applicationContext, CHANNEL_ID,fileName,"Failed")
                    custom_button.updateButtonState(ButtonState.Completed)
                }
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

        if(URL==null) {
            Toast.makeText(this, "Please Select an option first", Toast.LENGTH_SHORT).show()
            custom_button.updateButtonState(ButtonState.Clicked)
            return
        }
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


         downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager


                downloadID = downloadManager.enqueue(request) // enqueue puts the download request in the queue.
        /*enqueue request returns a unique long id which will act as identifier
        for the download,
        * */
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



