package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

    val nId = intent.getIntExtra( getString(R.string.INTENT_EXTRA_NOTIFICATION_ID),-1)
        val fileName = intent.getStringExtra(getString(R.string.INTENT_FILENAME_ID))
        val status = intent.getStringExtra(getString(R.string.INTENT_STATUS_ID))

        findViewById<TextView>(R.id.fileName).text = fileName
        findViewById<TextView>(R.id.statusText).text = status

        if(nId!=-1)
        {
            val nm = ContextCompat.getSystemService(this,NotificationManager::class.java) as NotificationManager
            nm.cancel(nId)

            Log.i("myTag","Notification Id $nId, $fileName, $status")
        }

        findViewById<Button>(R.id.okButton).setOnClickListener {
            this@DetailActivity.finish()
        }
    }

}






