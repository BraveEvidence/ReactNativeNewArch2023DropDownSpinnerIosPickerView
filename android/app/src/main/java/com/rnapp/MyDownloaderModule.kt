//package com.rnapp
//
//import android.app.DownloadManager
//import android.content.*
//import android.database.Cursor
//import android.net.Uri
//import android.os.Build
//import android.os.Environment
//import android.provider.DocumentsContract
//import android.provider.MediaStore
//import android.provider.OpenableColumns
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.FileProvider
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.coroutineScope
//import com.facebook.react.bridge.Promise
//import com.facebook.react.bridge.ReactApplicationContext
//import com.facebook.react.bridge.ReactContext
//import com.rtnmydownloader.NativeMyDownloaderSpec
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.BufferedInputStream
//import java.io.File
//import java.io.FileInputStream
//import java.io.FileOutputStream
//import kotlin.math.min
//
//
//class MyDownloaderModule(val context: ReactApplicationContext): NativeMyDownloaderSpec(context) {
//
//
//    override fun getName(): String {
//        return NAME
//    }
//
//    private val data = "_data"
//    private val downloadDir = Environment.getExternalStoragePublicDirectory(
//        Environment.DIRECTORY_DOWNLOADS
//    )
//    private var downloadManager: DownloadManager? = null
//    private var downloadID: Long = 0
//    private val lifecycle: Lifecycle by lazy {
//        ((context as ReactContext).currentActivity as AppCompatActivity).lifecycle
//    }
//    private var pdfDownloaderPromise: Promise? = null
//    private var fileName: String? = null
//
//    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
//                val fileUrl: Uri? = downloadManager?.getUriForDownloadedFile(downloadID)
//                lifecycle.coroutineScope.launch {
//                    withContext(Dispatchers.IO) {
//                        val path = getRealPathFromURI(fileUrl)
//                        copyFileToDownloads(File(path), fileName!!)
//                    }
//                }
//            }
//        }
//    }
//
//    private suspend fun copyFileToDownloads(downloadedFile: File, fileName: String): Uri? {
//        val resolver = currentActivity?.contentResolver
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val contentValues = ContentValues().apply {
//                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf") ///This should be changed according to your file
//                put(
//                    MediaStore.MediaColumns.SIZE,
//                    Integer.parseInt((downloadedFile.length() / 1024).toString())
//                )
//            }
//            resolver?.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
//        } else {
//            val authority = "${currentActivity?.packageName}.provider"
//            val destinyFile = File(downloadDir, fileName)
//            currentActivity?.let { FileProvider.getUriForFile(it, authority, destinyFile) }
//        }?.also { downloadedUri ->
//            resolver?.openOutputStream(downloadedUri).use { outputStream ->
//                val brr = ByteArray(1024)
//                var len: Int
//                val bufferedInputStream =
//                    BufferedInputStream(FileInputStream(downloadedFile.absoluteFile))
//                while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
//                    outputStream?.write(brr, 0, len)
//                }
//                outputStream?.flush()
//                bufferedInputStream.close()
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        currentActivity,
//                        "Download complete",
//                        Toast.LENGTH_LONG
//                    ).show()
//
//                    currentActivity?.unregisterReceiver(onDownloadComplete)
//                    pdfDownloaderPromise?.resolve(
//                        "Success"
//                    )
//                }
//            }
//        }
//    }
//
//    override fun downloadFile(
//        url: String?,
//        filename: String?,
//        extension: String?,
//        promise: Promise?
//    ) {
//        fileName = "${filename}.${extension}"
//        currentActivity?.registerReceiver(
//            onDownloadComplete,
//            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//        )
//        pdfDownloaderPromise = promise
//        download(url!!)
//    }
//
//    private fun download(url: String) {
//        try {
//            val uri = Uri.parse(url)
//            val request = DownloadManager.Request(uri)
//
//            request.setAllowedNetworkTypes(
//                DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
//            )
//
//            request.setNotificationVisibility(
//                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
//            )
//
//            request.setDestinationInExternalFilesDir(
//                currentActivity,
//                Environment.DIRECTORY_DOCUMENTS,
//                fileName
//            )
//
//            downloadManager =
//                currentActivity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//
//            downloadID = downloadManager!!.enqueue(request)
//        } catch (e: Exception) {
//            currentActivity?.unregisterReceiver(onDownloadComplete)
//            pdfDownloaderPromise?.reject(e.message, e.message)
//        }
//    }
//
//    private fun isExternalStorageDocument(uri: Uri): Boolean {
//        return "com.android.externalstorage.documents" == uri.authority
//    }
//
//    private fun getRealPathFromURI(uri: Uri?): String? {
//        when {
//            // DocumentProvider
//            DocumentsContract.isDocumentUri(currentActivity, uri) -> {
//                when {
//                    // ExternalStorageProvider
//                    uri?.let { isExternalStorageDocument(it) } == true -> {
//                        val docId = DocumentsContract.getDocumentId(uri)
//                        val split = docId.split(":").toTypedArray()
//                        val type = split[0]
//                        // This is for checking Main Memory
//                        return if ("primary".equals(type, ignoreCase = true)) {
//                            if (split.size > 1) {
//                                Environment.getExternalStorageDirectory()
//                                    .toString() + "/" + split[1]
//                            } else {
//                                Environment.getExternalStorageDirectory().toString() + "/"
//                            }
//                            // This is for checking SD Card
//                        } else {
//                            "storage" + "/" + docId.replace(":", "/")
//                        }
//                    }
//                    uri?.let { isDownloadsDocument(it) } == true -> {
//                        val fileName = currentActivity?.let { getFilePath(it, uri) }
//                        if (fileName != null) {
//                            return Environment.getExternalStorageDirectory()
//                                .toString() + "/Download/" + fileName
//                        }
//                        var id = DocumentsContract.getDocumentId(uri)
//                        if (id.startsWith("raw:")) {
//                            id = id.replaceFirst("raw:".toRegex(), "")
//                            val file = File(id)
//                            if (file.exists()) return id
//                        }
//                        val contentUri = ContentUris.withAppendedId(
//                            Uri.parse("content://downloads/public_downloads"),
//                            java.lang.Long.valueOf(id)
//                        )
//                        return currentActivity?.let { getDataColumn(it, contentUri, null, null) }
//                    }
//                    uri?.let { isMediaDocument(it) } == true -> {
//                        val docId = DocumentsContract.getDocumentId(uri)
//                        val split = docId.split(":").toTypedArray()
//                        val type = split[0]
//                        val contentUri: Uri?
//                        when (type) {
//                            "image" -> {
//                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                            }
//                            "video" -> {
//                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                            }
//                            "audio" -> {
//                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//                            }
//                            else -> {
//                                // non-media files i.e documents and other files
//                                contentUri = MediaStore.Files.getContentUri("external")
//                                val selection =
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                        MediaStore.MediaColumns.RELATIVE_PATH + "=?"
//                                    } else {
//                                        "_id=?"
//                                    }
//                                val selectionArgs = arrayOf(Environment.DIRECTORY_DOCUMENTS)
//                                return currentActivity?.let {
//                                    getMediaDocumentPath(
//                                        it,
//                                        contentUri,
//                                        selection,
//                                        selectionArgs
//                                    )
//                                }
//                            }
//                        }
//                        val selection = "_id=?"
//                        val selectionArgs = arrayOf(split[1])
//                        return currentActivity?.let {
//                            getDataColumn(it, contentUri, selection, selectionArgs)
//                        }
//                    }
//                    uri?.let { isGoogleDriveUri(it) } == true -> {
//                        return currentActivity?.let { getDriveFilePath(uri, it) }
//                    }
//                    else -> {
//                        currentActivity?.unregisterReceiver(onDownloadComplete)
//                        pdfDownloaderPromise?.reject(
//                            "Unknown Directory.",
//                            "Unknown Directory."
//                        )
//                    }
//                }
//            }
//            "content".equals(uri?.scheme, ignoreCase = true) -> {
//                // Return the remote address
//                return if (uri?.let { isGooglePhotosUri(it) } == true) {
//                    uri.lastPathSegment
//                } else {
//                    currentActivity?.let {
//                        getDataColumn(
//                            it,
//                            uri,
//                            null,
//                            null
//                        )
//                    }
//                }
//            }
//            "file".equals(uri?.scheme, ignoreCase = true) -> {
//                return uri?.path
//            }
//        }
//        return null
//    }
//
//    private fun getDataColumn(
//        context: Context,
//        uri: Uri?,
//        selection: String?,
//        selectionArgs: Array<String>?
//    ): String? {
//        var cursor: Cursor? = null
//        val column = data
//        val projection = arrayOf(column)
//        try {
//            if (uri == null) return null
//            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
//            if (cursor != null && cursor.moveToFirst()) {
//                val index = cursor.getColumnIndexOrThrow(column)
//                return cursor.getString(index)
//            }
//        } finally {
//            cursor?.close()
//        }
//        return null
//    }
//
//    private fun getMediaDocumentPath(
//        context: Context,
//        uri: Uri?,
//        selection: String?,
//        selectionArgs: Array<String>?
//    ): String? {
//        var cursor: Cursor? = null
//        val column = data
//        val projection = arrayOf(column)
//        try {
//            if (uri == null) return null
//            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
//            if (cursor != null && cursor.moveToFirst()) {
//                val index = cursor.getColumnIndexOrThrow(column)
//                return cursor.getString(index)
//            }
//        } finally {
//            cursor?.close()
//        }
//        return null
//    }
//
//    private fun getFilePath(context: Context, uri: Uri?): String? {
//        var cursor: Cursor? = null
//        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
//        try {
//            if (uri == null) return null
//            cursor = context.contentResolver.query(
//                uri,
//                projection,
//                null,
//                null,
//                null
//            )
//            if (cursor != null && cursor.moveToFirst()) {
//                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
//                return cursor.getString(index)
//            }
//        } finally {
//            cursor?.close()
//        }
//        return null
//    }
//
//    private fun getDriveFilePath(uri: Uri, context: Context): String? {
//        val returnCursor = context.contentResolver.query(
//            uri,
//            null,
//            null,
//            null,
//            null
//        )
//        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//        returnCursor.moveToFirst()
//        val name = returnCursor.getString(nameIndex)
//        returnCursor.close()
//
//        val file = File(context.cacheDir, name)
//        try {
//            val inputStream = context.contentResolver.openInputStream(uri)
//            val outputStream = FileOutputStream(file)
//            var read = 0
//            val maxBufferSize = 1 * 1024 * 1024
//            val bytesAvailable = inputStream!!.available()
//
//            val bufferSize = min(bytesAvailable, maxBufferSize)
//            val buffers = ByteArray(bufferSize)
//            while (inputStream.read(buffers).also { read = it } != -1) {
//                outputStream.write(buffers, 0, read)
//            }
//            inputStream.close()
//            outputStream.close()
//        } catch (e: Exception) {
//            currentActivity?.unregisterReceiver(onDownloadComplete)
//            pdfDownloaderPromise?.reject(e.message, e.message)
//        }
//        return file.path
//    }
//
//    private fun isDownloadsDocument(uri: Uri): Boolean {
//        return "com.android.providers.downloads.documents" == uri.authority
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is MediaProvider.
//     */
//    private fun isMediaDocument(uri: Uri): Boolean {
//        return "com.android.providers.media.documents" == uri.authority
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is Google Photos.
//     */
//    private fun isGooglePhotosUri(uri: Uri): Boolean {
//        return "com.google.android.apps.photos.content" == uri.authority
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is Google Photos.
//     */
//    private fun isGoogleDriveUri(uri: Uri): Boolean {
//        return (
//                "com.google.android.apps.docs.storage" == uri.authority ||
//                        "com.google.android.apps.docs.storage.legacy" == uri.authority
//                )
//    }
//
//    companion object {
//        const val NAME = "RTNMyDownloader"
//    }
//
//
//}