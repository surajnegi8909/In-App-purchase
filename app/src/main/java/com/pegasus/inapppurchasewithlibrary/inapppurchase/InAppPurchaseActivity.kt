package com.pegasus.inapppurchasewithlibrary.inapppurchase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.pegasus.inapppurchasemodule.InAppPurchase
import com.pegasus.inapppurchasemodule.util.OnSuccessResponse
import com.pegasus.inapppurchasewithlibrary.R
import com.pegasus.inapppurchasewithlibrary.adapter.PurchaseAdapter

class InAppPurchaseActivity : AppCompatActivity(), OnSuccessResponse,
    PurchaseAdapter.OnItemClicked {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: PurchaseAdapter
    var type: String? = null
    var isOneTime: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_app_purchase)

        type = intent.getStringExtra("test")
        isOneTime = intent.getBooleanExtra("onetime", false)

        InAppPurchase.billingSetup(this, this)
        recyclerView = findViewById(R.id.inappPurchase)
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onBillingConnectionSuccess() {
        if (type.isNullOrEmpty()) {
            if (isOneTime) {
                InAppPurchase.queryOneTimeProduct(listOf("one_time"))
            } else {
                InAppPurchase.queryInAppProduct(listOf("low_products",
                    "medium_products",
                    "high_products"))
            }
        } else {
            InAppPurchase.queryInAppProduct(listOf("android.test.purchased"))
        }
    }

    override fun getInAppProductList(productDetailsList: MutableList<ProductDetails>) {

        Log.e("InAppPurchase", "onProductDetailsResponse $productDetailsList")
        try {
            runOnUiThread {
                adapter = PurchaseAdapter(productDetailsList, this, "inapp")
                recyclerView.adapter = adapter
            }
        } catch (e: Exception) {
            Log.e("InAppPurchase", "in object ${e.localizedMessage}")
        }
    }

    override fun getOneTimeProductList(productDetailsList: MutableList<ProductDetails>) {
        Log.e("InAppPurchase", "onProductDetailsResponse $productDetailsList")
        try {
            runOnUiThread {
                adapter = PurchaseAdapter(productDetailsList, this, "inapp")
                recyclerView.adapter = adapter
            }
        } catch (e: Exception) {
            Log.e("InAppPurchase", "in object ${e.localizedMessage}")
        }
    }

    override fun getSubsProductList(productDetailsList: MutableList<ProductDetails>) {
    }

    override fun onPurchaseSuccess(purchaseData: Purchase) {
        Log.e("InAppPurchase ", "purchase data -> $purchaseData")
        runOnUiThread {
            Toast.makeText(this, "Successfully Purchased", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPurchaseError(error: String) {
        Log.e("InAppPurchase ", "purchase data -> $error")
        runOnUiThread {
            Toast.makeText(this, "Error : $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(productDetails: ProductDetails, type: String) {
        if (isOneTime) {
            InAppPurchase.launchOneTimePurchaseFlow(productDetails,this)
        } else {
            InAppPurchase.launchInAppPurchaseFlow(productDetails, this)
        }
    }
}