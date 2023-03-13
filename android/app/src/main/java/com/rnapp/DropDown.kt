//package com.rnapp
//
//
//import android.content.Context
//import android.view.View
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import com.facebook.react.bridge.ReactContext
//import com.facebook.react.uimanager.UIManagerHelper
//import com.facebook.react.uimanager.events.EventDispatcher
//
//class DropDown(context: Context): androidx.appcompat.widget.AppCompatSpinner(context) {
//
//    var isSpinnerSelected = false
//
//    fun configureSpinner(list:ArrayList<String>){
//
//        onItemSelectedListener = object : OnItemSelectedListener {
//            override fun onItemSelected(
//                parentView: AdapterView<*>?,
//                selectedItemView: View?,
//                position: Int,
//                id1: Long
//            ) {
//                if(isSpinnerSelected){
//                    val reactContext = context as ReactContext
//                    val eventDispatcher: EventDispatcher? =
//                        UIManagerHelper.getEventDispatcherForReactTag(
//                            reactContext, id
//                        )
//                    eventDispatcher?.dispatchEvent(DropDownEvent(id, list[position]))
//                }
//                isSpinnerSelected = true
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>?) {
//                // your code here
//            }
//        }
//
//        val spinnerArrayAdapter =
//            ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, list)
//        adapter = spinnerArrayAdapter
//    }
//}