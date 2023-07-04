# In App purchase / Subscription module

Features:
Get list of In-App purchase and Subscription products.

To import this module, First download it.
Now open your project in which you want to use this module.

Goto File -> New -> Import Module
Select the source directory of the Module you want to import and click Finish.
Open Project Structure Dialog (You can open the PSD by selecting File > Project Structure) and from 
the left panel click on Dependencies.
Open the Dependencies tab.
Click on <All Module> now in right side you will see add dependencies tab.
Click the (+) icon from Dependencies section and click Module Dependency.
Add Module Dependency dialog will open, Now select app module and click Ok.
Then you will see another Add Module Dependency dialog and it will show you inapppurchase module. 
Click on CheckBox then click ok.
Open your build.gradle file and check that the module is now listed under dependencies as shown below. 
implementation project(path: ':inapppurchase')

After Successfully importing inapppurchase module, Now let's implement it.

Create app in https://play.google.com/console
Upload apk to alpha testing.
Click on app then goto Products (Add In-app purchase or Subscription).

Now open AndroidManifest.xml file 
Paste these permission outside the application tag.

<uses-permission android:name="android.permission.INTERNET"/>

build.gradle (module level) implement this because we will use some classes from billing library
    
    implementation 'com.android.billingclient:billing:5.0.0'

Open the Activity/fragment in which you want to implement In-App Purchase or Subscription.

implement OnSuccessResponse interface and import all function.

Inside onCreate function.

    InAppPurchase.billingSetup(this, this)

//here we are initializing the BillingClient and passing context and onSuccessResponseListener.

[1 : In-App Purchase]

After establish a successful connection from Google Play, It will call onBillingConnectionSuccess()

["Non-Consumable"]
Products which once purchased cannot be purchased again and is permanently associated with user’s 
Google Play account . For example, removal of ads, once user paid certain amount for removing ads,
you want that to stay with user. On re-installing, changing device these products will not be lost.

Here you have to pass list of string i.e., productID

    override fun onBillingConnectionSuccess() {
            InAppPurchase.queryOneTimeProduct(listOf("low_products")
    }

If the product_ID exist in in-app purchase, It it will call

        getOneTimeProductList(productDetailsList: MutableList<ProductDetails>)
and it will give you all details related to product ids. You can show data as you like.
else it will return a message in

        onError(message: String).

When we click on low product then we have to pass it on this function.
here we are passing single productDetail and activity.

        InAppPurchase.launchOneTimePurchaseFlow(productDetails, this)

If the payment is success you will get a callback in

        onPurchaseSuccess(purchaseData: Purchase) 
where purchaseData contains info about purchased item. You can handle data as you like.



["Consumable"]
Products which can be repurchased, such as hints or in-game coins and is temporarily associated 
with users. Once purchased, it will be in owned state and to make it available for purchase again 
you will have to send a consumption request to Google Play.

Here you have to pass list of string i.e., productID

    override fun onBillingConnectionSuccess() {
            InAppPurchase.queryInAppProduct(listOf("low_products","medium_products","high_products"))
    }

If the product_ID exist in in-app purchase, It it will call 
        
        getInAppProductList(productDetailsList: MutableList<ProductDetails>)
and it will give you all details related to product ids. You can show data as you like.
else it will return a message in 

        onError(message: String).

When we click on low product then we have to pass it on this function.
here we are passing single productDetail and activity.

        InAppPurchase.launchInAppPurchaseFlow(productDetails, this)

If the payment is success you will get a callback in 

        onPurchaseSuccess(purchaseData: Purchase) 
where purchaseData contains info about purchased item. You can handle data as you like.


[2 : In-App Subscription]

After establish a successful connection from Google Play, It will call onBillingConnectionSuccess()

Here you have to pass list of string i.e., productID

    override fun onBillingConnectionSuccess() {
            InAppPurchase.querySubsProduct(listOf("sub_3_days", "sub_2"))
    }

If the product_ID exist in in-app purchase, It it will call 

        getSubsProductList(productDetailsList: MutableList<ProductDetails>)
and it will give you all details related to product ids. You can show data as you like.
else it will return a message in 

        onError(message: String)

When we click on low product then we have to pass it on this function.
here we are passing single productDetail and activity.
for Subscription we also need offer token, We can get it from productDetails as shown below

     val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken
            if (offerToken != null) {
                InAppPurchase.launchSubsPurchaseFlow(productDetails, offerToken, this)
            }

If the payment is success you will get a callback in 

        onPurchaseSuccess(purchaseData: Purchase) 
where purchaseData contains info about purchased item. You can handle data as you like.