package com.pegasus.inapppurchasemodule.util

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

interface OnSuccessResponse {

    fun onBillingConnectionSuccess()
    fun getInAppProductList(productDetailsList: MutableList<ProductDetails>)
    fun getOneTimeProductList(productDetailsList: MutableList<ProductDetails>)
    fun getSubsProductList(productDetailsList: MutableList<ProductDetails>)
    fun onPurchaseSuccess(purchaseData: Purchase)
    fun onPurchaseError(error:String)
    fun onError(message:String)
}