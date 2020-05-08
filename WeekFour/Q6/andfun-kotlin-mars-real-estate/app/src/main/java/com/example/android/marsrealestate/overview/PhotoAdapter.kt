/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.databinding.LinearViewItemBinding
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.DataTruncation

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class PhotoAdapter(val onClickListener: OnCilckListener)
    :ListAdapter<DataItem,RecyclerView.ViewHolder>(DiffCallback){
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_VIEW_TYPE_HEADER ->  HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM ->MarsPropertyViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.MarsPropertyItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class MarsPropertyViewHolder(private var binding: LinearViewItemBinding)
        : RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent: ViewGroup): MarsPropertyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LinearViewItemBinding.inflate(layoutInflater,parent, false)
                return MarsPropertyViewHolder(binding)
            }
        }

        fun bind(marsProperty: MarsProperty){
            binding.property = marsProperty
            binding.executePendingBindings()
        }
    }
    class HeaderViewHolder(view: View):RecyclerView.ViewHolder(view){
        companion object{
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header,parent, false)
                return HeaderViewHolder(view)
            }
        }
    }


    companion object DiffCallback: DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is MarsPropertyViewHolder -> {
                val property = getItem(position) as DataItem.MarsPropertyItem
                holder.itemView.setOnClickListener { onClickListener.onClick(property.marsProperty) }
                holder.bind(property.marsProperty)
            }
        }
    }

    class OnCilckListener(val clickListener:(marsProperty:MarsProperty)-> Unit){
        fun onClick(marsProperty:MarsProperty) = clickListener(marsProperty)
    }

    fun addHeaderAndSubmitList(list:List<MarsProperty>?){
        adapterScope.launch {
            val items =
            when(list){
                null ->listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.MarsPropertyItem(it) }
            }
            withContext(Dispatchers.Main){
                submitList(items)
            }
        }
    }
}

sealed class DataItem(){
    data class MarsPropertyItem(val marsProperty: MarsProperty):DataItem(){
        override val id = marsProperty.id
    }
    object Header: DataItem(){
        override val id = Long.MIN_VALUE.toString()
    }
    abstract val id:String
}
