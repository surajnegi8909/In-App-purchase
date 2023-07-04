package com.pegasus.inapppurchasewithlibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.pegasus.inapppurchasewithlibrary.R

class PurchaseAdapter(private val data:  MutableList<ProductDetails>,
                      private val onItemClicked : OnItemClicked, val type:String): RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName = itemView.findViewById<TextView>(R.id.txtName)
        val txtPrice = itemView.findViewById<TextView>(R.id.txtPrice)
        val container = itemView.findViewById<CardView>(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.customlayout,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = data[position].name
        if (type == "inapp"){
            holder.txtPrice.text = data[position].oneTimePurchaseOfferDetails?.formattedPrice
        }
        else{
            holder.txtPrice.text = data[position].subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice
        }

        holder.container.setOnClickListener {
            onItemClicked.onClick(data[position],type)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    interface OnItemClicked{
      fun onClick(productDetails: ProductDetails,type: String)
    }
}