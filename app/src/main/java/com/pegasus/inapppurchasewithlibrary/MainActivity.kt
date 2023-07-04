package com.pegasus.inapppurchasewithlibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.pegasus.inapppurchasewithlibrary.inapppurchase.InAppPurchaseActivity
import com.pegasus.inapppurchasewithlibrary.subscription.SubscriptionActivity

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(Intent(this, InAppPurchaseActivity::class.java))
        }

        findViewById<Button>(R.id.btnsubs).setOnClickListener {
            startActivity(Intent(this, SubscriptionActivity::class.java))
        }

        findViewById<Button>(R.id.btntestinapppurchase).setOnClickListener {
            startActivity(Intent(this, InAppPurchaseActivity::class.java)
                .putExtra("test","test"))
        }

        findViewById<Button>(R.id.btnOneTimePurchase).setOnClickListener {
            startActivity(Intent(this, InAppPurchaseActivity::class.java)
                .putExtra("onetime",true))
        }
    }
}