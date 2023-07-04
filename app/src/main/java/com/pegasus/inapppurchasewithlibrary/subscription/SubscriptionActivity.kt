package com.pegasus.inapppurchasewithlibrary.subscription

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.pegasus.inapppurchasemodule.InAppPurchase
import com.pegasus.inapppurchasemodule.util.OnSuccessResponse
import com.pegasus.inapppurchasewithlibrary.R
import com.pegasus.inapppurchasewithlibrary.adapter.PurchaseAdapter

class SubscriptionActivity : AppCompatActivity(), OnSuccessResponse, PurchaseAdapter.OnItemClicked {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: PurchaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        InAppPurchase.billingSetup(this, this)
        recyclerView = findViewById(R.id.subscription)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.unsubscribe).setOnClickListener {
            InAppPurchase.unsubscribe(this,"sub_5")
        }
    }

    override fun onBillingConnectionSuccess() {
        InAppPurchase.querySubsProduct(listOf("sub_3_days", "sub_2", "sub_4","sub_5"))
    }

    override fun getInAppProductList(productDetailsList: MutableList<ProductDetails>) {
    }

    override fun getOneTimeProductList(productDetailsList: MutableList<ProductDetails>) {

    }

    override fun getSubsProductList(productDetailsList: MutableList<ProductDetails>) {
        try {
            Log.e("InAppPurchase", "onProductDetailsResponse $productDetailsList")
            runOnUiThread {
                adapter = PurchaseAdapter(productDetailsList, this, "subs")
                recyclerView.adapter = adapter
            }
        } catch (e: Exception) {
            Log.e("InAppPurchase", "in object ${e.localizedMessage}")
        }
    }

    override fun onPurchaseSuccess(purchaseData: Purchase) {
        runOnUiThread {
            Toast.makeText(this,"Successfully Purchased", Toast.LENGTH_SHORT).show()
        }
        Log.e("InAppPurchase ", "purchase data -> $purchaseData")
    }

    override fun onPurchaseError(error: String) {
        runOnUiThread {
            Toast.makeText(this,"Error : $error", Toast.LENGTH_SHORT).show()
        }
        Log.e("InAppPurchase ", "purchase data -> $error")
    }

    override fun onClick(productDetails: ProductDetails, type: String) {
        if (type == "inapp") {
            InAppPurchase.launchInAppPurchaseFlow(productDetails, this)
        } else {
            val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken
            if (offerToken != null) {
                InAppPurchase.launchSubsPurchaseFlow(productDetails, offerToken, this)
            }
        }
    }
    override fun onError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}