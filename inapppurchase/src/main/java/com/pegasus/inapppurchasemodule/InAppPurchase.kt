package com.pegasus.inapppurchasemodule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.pegasus.inapppurchasemodule.constants.Constant
import com.pegasus.inapppurchasemodule.util.OnSuccessResponse


class InAppPurchase {
    companion object {
        var type :String ?= null
        private var mBillingClient: BillingClient? = null
        private var onSuccessResponse: OnSuccessResponse? = null

        fun billingSetup(context: Context, onSuccessResponseListener: OnSuccessResponse) {

            onSuccessResponse = onSuccessResponseListener

            mBillingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases().setListener { billingresult, purchases ->
                    //sending result and data to method
                    when (type) {
                        Constant.IN_APP_PURCHASE -> {
                            onInAppPurchaseUpdate(billingresult, purchases, context)
                        }
                        Constant.ONE_TIME_PURCHASE -> {
                            onOneTimePurchaseUpdate(billingresult, purchases, context)
                        }
                        else -> {
                            onSubsPurchaseUpdate(billingresult, purchases, context)
                        }
                    }
                }.build()

            //Establish a connection to Google Play
            mBillingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(
                    billingResult: BillingResult,
                ) {
                    if (billingResult.responseCode ==
                        BillingClient.BillingResponseCode.OK
                    ) {
                        Log.i("InAppPurchase", "OnBillingSetupFinish connected")
                        onSuccessResponse?.onBillingConnectionSuccess()

                    } else {
                        Log.i("InAppPurchase", "OnBillingSetupFinish failed")
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.i("InAppPurchase", "OnBillingSetupFinish connection lost")
                }
            })
        }

        fun queryInAppProduct(productID: List<String>) {
            Log.e("InAppPurchase", "inside query in App product")

            val productList = ArrayList<Product>()
            productID.forEach { id ->
                productList.add(
                    Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build())
            }
            val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            mBillingClient?.queryProductDetailsAsync(
                queryProductDetailsParams,
                ProductDetailsResponseListener { billingResult, productDetailsList ->
                    try {
                        if (productDetailsList.isNotEmpty()) {
                            onSuccessResponse?.getInAppProductList(productDetailsList)
                        } else {
                            onSuccessResponse?.onError("onProductDetailsResponse: No products")
                            Log.e("InAppPurchase", "onProductDetailsResponse: No products")
                        }
                    } catch (e: Exception) {
                        Log.e("InAppPurchase", e.localizedMessage)
                    }
                }
            )
        }

        fun queryOneTimeProduct(productID: List<String>) {
            Log.e("InAppPurchase", "inside query in App product")

            val productList = ArrayList<Product>()
            productID.forEach { id ->
                productList.add(
                    Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build())
            }
            val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            mBillingClient?.queryProductDetailsAsync(
                queryProductDetailsParams,
                ProductDetailsResponseListener { billingResult, productDetailsList ->
                    try {
                        if (productDetailsList.isNotEmpty()) {
                            onSuccessResponse?.getOneTimeProductList(productDetailsList)
                        } else {
                            onSuccessResponse?.onError("onProductDetailsResponse: No products")
                            Log.e("InAppPurchase", "onProductDetailsResponse: No products")
                        }
                    } catch (e: Exception) {
                        Log.e("InAppPurchase", e.localizedMessage)
                    }
                }
            )
        }

        fun querySubsProduct(productID: List<String>) {
            Log.e("InAppPurchase", "inside query in App product")
            val productList = ArrayList<Product>()
            productID.forEach { id ->
                productList.add(
                    Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build())
            }
            val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            mBillingClient?.queryProductDetailsAsync(
                queryProductDetailsParams,
                ProductDetailsResponseListener { billingResult, productDetailsList ->
                    try {
                        if (productDetailsList.isNotEmpty()) {
                            onSuccessResponse?.getSubsProductList(productDetailsList)
                        } else {
                            onSuccessResponse?.onError("onProductDetailsResponse: No products")
                            Log.e("InAppPurchase", "onProductDetailsResponse: No products")
                        }
                    } catch (e: Exception) {
                        Log.e("InAppPurchase", e.localizedMessage)
                    }
                }
            )
        }

        //Launch the purchase flow
        fun launchInAppPurchaseFlow(
            productDetails: ProductDetails,
            activity: Activity,
        ) {
            type = Constant.IN_APP_PURCHASE
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf<ProductDetailsParams>(
                        ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )).build()

            val responseCode =
                mBillingClient?.launchBillingFlow(activity, billingFlowParams)
            Log.e("InAppPurchase", responseCode?.responseCode.toString())

        }

        //Launch the purchase flow
        fun launchOneTimePurchaseFlow(
            productDetails: ProductDetails,
            activity: Activity,
        ) {
            type = Constant.ONE_TIME_PURCHASE
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf<ProductDetailsParams>(
                        ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )).build()

            val responseCode =
                mBillingClient?.launchBillingFlow(activity, billingFlowParams)
            Log.e("InAppPurchase", responseCode?.responseCode.toString())

        }

        //Launch the purchase flow
        fun launchSubsPurchaseFlow(
            productDetails: ProductDetails,
            offerToken: String,
            activity: Activity,
        ) {
            type = Constant.IN_APP_SUBSCRIPTION
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf<ProductDetailsParams>(
                        ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(offerToken)
                            .build()
                    )).build()

            val responseCode =
                mBillingClient?.launchBillingFlow(activity, billingFlowParams)
            Log.e("InAppPurchase", responseCode?.responseCode.toString())

        }

        private fun onInAppPurchaseUpdate(
            billingResult: BillingResult,
            purchases: MutableList<Purchase>?,
            context: Context,
        ) {
            when {
                billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null -> {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED -> {
                    if (purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(purchase)
                        }
                    }
                    // Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context,
                        Constant.USER_CANCELLED,
                        Toast.LENGTH_SHORT).show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.FEATURE_NOT_SUPPORTED, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.SERVICE_DISCONNECTED, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(purchase)
                        }
                    } // Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.SERVICE_UNAVAILABLE, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.ITEM_ALREADY_OWNED, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.ITEM_NOT_OWNED, Toast.LENGTH_SHORT).show()
                }
                else -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(purchase)
                        }
                    } // Handle any other error codes.
                    Toast.makeText(context, Constant.ERROR_DURING_PURCHASE, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        private fun onOneTimePurchaseUpdate(
            billingResult: BillingResult,
            purchases: MutableList<Purchase>?,
            context: Context,
        ) {
            when {
                billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null -> {
                    for (purchase in purchases) {
                        verifyOneTimePurchase(purchase)
                    }
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED -> {
                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifyOneTimePurchase(purchase)
                        }
                    }
                    // Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context,
                        Constant.USER_CANCELLED,
                        Toast.LENGTH_SHORT).show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifyOneTimePurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.FEATURE_NOT_SUPPORTED, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifyOneTimePurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.SERVICE_DISCONNECTED, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifyOneTimePurchase(purchase)
                        }
                    } // Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.SERVICE_UNAVAILABLE, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifyOneTimePurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.ITEM_ALREADY_OWNED, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifyOneTimePurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.ITEM_NOT_OWNED, Toast.LENGTH_SHORT).show()
                }
                else -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifyOneTimePurchase(purchase)
                        }
                    } // Handle any other error codes.
                    Toast.makeText(context, Constant.ERROR_DURING_PURCHASE, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        private fun onSubsPurchaseUpdate(
            billingResult: BillingResult,
            purchases: MutableList<Purchase>?,
            context: Context,
        ) {
            when {
                billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null -> {
                    for (purchase in purchases) {
//                        handlePurchase(purchase)
                        verifySubPurchase(purchase)
                    }
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED -> {
                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifySubPurchase(purchase)
                        }
                    }
                    // Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context,
                        Constant.USER_CANCELLED,
                        Toast.LENGTH_SHORT).show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifySubPurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.FEATURE_NOT_SUPPORTED, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifySubPurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.SERVICE_DISCONNECTED, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifySubPurchase(purchase)
                        }
                    } // Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.SERVICE_UNAVAILABLE, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifySubPurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.ITEM_ALREADY_OWNED, Toast.LENGTH_SHORT)
                        .show()
                }
                billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifySubPurchase(purchase)
                        }
                    }// Handle an error caused by a user cancelling the purchase flow.
                    Toast.makeText(context, Constant.ITEM_NOT_OWNED, Toast.LENGTH_SHORT).show()
                }
                else -> {

                    if (purchases != null) {
                        for (purchase in purchases) {
                            verifySubPurchase(purchase)
                        }
                    } // Handle any other error codes.
                    Toast.makeText(context, Constant.ERROR_DURING_PURCHASE, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        //Processing purchases / Verify Payment
        private fun handlePurchase(purchase: Purchase) {

            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            Log.d("purchase data >>> ", purchase.toString())

            val listener = object : ConsumeResponseListener {
                override fun onConsumeResponse(billingResult: BillingResult, s: String) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d("success", purchase.products[0])

                        onSuccessResponse?.onPurchaseSuccess(purchase)
                    }
                    else{
                        onSuccessResponse?.onPurchaseError(billingResult.responseCode.toString())
                    }
                }
            }
            mBillingClient?.consumeAsync(consumeParams, listener)
        }

        private fun verifyOneTimePurchase(purchases: Purchase) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.purchaseToken)
                .build()
            mBillingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    onSuccessResponse?.onPurchaseSuccess(purchases)

                    Log.d("inapp", "Purchase Token: " + purchases.purchaseToken)
                    Log.d("inapp", "Purchase Time: " + purchases.purchaseTime)
                    Log.d("inapp", "Purchase OrderID: " + purchases.orderId)
                }
                else{
                    onSuccessResponse?.onPurchaseError(billingResult.responseCode.toString())
                }
            }
        }

        private fun verifySubPurchase(purchases: Purchase) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.purchaseToken)
                .build()
            mBillingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    onSuccessResponse?.onPurchaseSuccess(purchases)

                    Log.d("inapp", "Purchase Token: " + purchases.purchaseToken)
                    Log.d("inapp", "Purchase Time: " + purchases.purchaseTime)
                    Log.d("inapp", "Purchase OrderID: " + purchases.orderId)
                }
                else{
                    onSuccessResponse?.onPurchaseError(billingResult.responseCode.toString())
                }
            }
        }

        fun unsubscribe(activity: Activity, productID: String) {
            try {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                val subscriptionUrl = ("http://play.google.com/store/account/subscriptions"
                        + "?package=" + activity.packageName
                        + "&sku=" + productID)
                intent.data = Uri.parse(subscriptionUrl)
                activity.startActivity(intent)

            } catch (e: Exception) {
                Log.w("InAppPurchase", "Unsubscribing failed.")
            }
        }
    }
}