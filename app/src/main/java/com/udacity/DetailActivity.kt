package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

    val nId = intent.getIntExtra( getString(R.string.INTENT_EXTRA_NOTIFICATION_ID),-1)
        if(nId!=-1)
        {
            val nm = ContextCompat.getSystemService(this,NotificationManager::class.java) as NotificationManager
            nm.cancel(nId)

            Log.i("myTag","Notification Id $nId")
        }
    }

}






