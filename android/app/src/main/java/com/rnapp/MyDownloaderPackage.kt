//package com.rnapp
//
//import com.facebook.react.TurboReactPackage
//import com.facebook.react.bridge.NativeModule
//import com.facebook.react.bridge.ReactApplicationContext
//import com.facebook.react.module.model.ReactModuleInfo
//import com.facebook.react.module.model.ReactModuleInfoProvider
//
//class MyDownloaderPackage : TurboReactPackage() {
//    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
//        return if (name == MyDownloaderModule.NAME) {
//            MyDownloaderModule(reactContext)
//        } else {
//            null
//        }
//    }
//
//    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider? {
//        return ReactModuleInfoProvider {
//            val moduleInfos: MutableMap<String, ReactModuleInfo> = HashMap()
//            moduleInfos[MyDownloaderModule.NAME] = ReactModuleInfo(
//                MyDownloaderModule.NAME,
//                MyDownloaderModule.NAME,
//                false,  // canOverrideExistingModule
//                false,  // needsEagerInit
//                true,  // hasConstants
//                false,  // isCxxModule
//                true // isTurboModule
//            )
//            moduleInfos
//        }
//    }
//}