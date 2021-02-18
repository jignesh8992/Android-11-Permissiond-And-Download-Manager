package com.example.androidpermissions.asynctask

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.androidpermissions.callback.DownloadListener
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/*val fileUrl = "http://guolin.tech/android.txt"
val fileName = "android.txt"*/

const val srcURL = "https://www.pexels.com/video/1828452/download/?search_query=&tracking_id=1altbyzu6rp"
const val fileName = "Pexels_Videos_1828452.mp4"

lateinit var destURL: String


private lateinit var outputPath: String
private lateinit var error: String


/**
 * DownloadFileFromURLTask.kt - Download file from the URL to the external storage with support of Android 11
 * @author:  Jignesh N Patel
 * @date: 18-Feb-2021 10:32 AM
 */

class DownloadFileFromURLTask(val mContext: Context, private val outputDir: String, private val downloadListener: DownloadListener) : AsyncTask<String?, Int?, Boolean>() {
    private var progressDialog: ProgressDialog? = null
    private val TAG = javaClass.simpleName


    init {
        // initialization of ProgressDialog
        progressDialog = ProgressDialog(mContext)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog!!.setCancelable(false)
        progressDialog!!.setTitle("Downloading")
    }


    /**
     * Before starting download show ProgressDialog with download percentage
     */
    override fun onPreExecute() {
        super.onPreExecute()
        Log.i(TAG, "Downloading started")
        progressDialog!!.show()
        progressDialog!!.progress = 0
    }

    /**
     * Downloading file in doInBackground
     */
    override fun doInBackground(vararg f_url: String?): Boolean {
        Log.i(TAG, "Downloading source url: $srcURL")
        try {
            var total: Long = 0
            var count = 0
            val url = URL(srcURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val buffer = ByteArray(1024)
            // getting file length
            val lenghtOfFile = connection.contentLength

            // input stream to read file
            val inputStream = connection.inputStream
            // Output stream to write file
            val outputStream: OutputStream?
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {

                // first we create app name folder direct to the root directory
                destURL = Environment.getExternalStorageDirectory().path + File.separator + outputDir
                var desFile = File(destURL)
                if (!desFile.exists()) {
                    desFile.mkdir()
                }

                // once the app name directory created we create download directory inside app directory
                destURL = destURL + File.separator + Environment.DIRECTORY_DOWNLOADS
                desFile = File(destURL)
                if (!desFile.exists()) {
                    desFile.mkdir()
                }

                destURL = destURL + File.separator + fileName

                // final output path
                outputPath = destURL

                Log.i(TAG, "Downloading destination url: $destURL")
                Log.i(TAG, "Downloading -> Below Android 11")
                outputStream = FileOutputStream(destURL)
                while (inputStream.read(buffer).also { count = it } != -1) {
                    total += count.toLong()
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress((total * 100 / lenghtOfFile).toInt())

                    // writing data to file
                    outputStream.write(buffer, 0, count)
                }
                // flushing output
                outputStream.flush()
                // closing streams
                outputStream.close()
                inputStream.close()
            } else {
                Log.i(TAG, "Downloading -> Android 11 or Above")
                val bis = BufferedInputStream(inputStream)
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)

                var desDirectory = Environment.DIRECTORY_DOWNLOADS
                // If you want to create custom directory inside Download directory only
                desDirectory = desDirectory + File.separator + outputDir
                val desFile = File(desDirectory)
                if (!desFile.exists()) {
                    desFile.mkdir()
                }
                // final output path
                outputPath = desDirectory + File.separator + fileName

                Log.i(TAG, "Downloading destination directory: $desDirectory")

                values.put(MediaStore.MediaColumns.RELATIVE_PATH, desDirectory)
                //  values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                val uri = mContext.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    outputStream = mContext.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        val bos = BufferedOutputStream(outputStream)
                        var bytes = bis.read(buffer)
                        while (bytes.also { count = it } != -1) {
                            total += count.toLong()
                            // publishing the progress....
                            // After this onProgressUpdate will be called
                            publishProgress((total * 100 / lenghtOfFile).toInt())
                            // writing data to file
                            bos.write(buffer, 0, count)
                            bos.flush()
                            bytes = bis.read(buffer)
                        }
                        bos.close()
                    }
                }
                bis.close()
            }

            return !isCancelled
        } catch (e: Exception) {
            error = e.toString()
            Log.e(TAG, "Exception: $e")
        }
        return false
    }

    override fun onProgressUpdate(vararg progress: Int?) {
        super.onProgressUpdate(*progress)
        val percentage = progress[0]!!
        // setting progress percentage
        Log.i(TAG, "Downloading progress : $percentage")
        progressDialog!!.progress = percentage
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     */
    override fun onPostExecute(success: Boolean) {
        Log.i(TAG, "onPostExecute")
        // dismiss the dialog after the file was downloaded
        progressDialog!!.dismiss()
        if (success) {
            downloadListener.onSuccess(outputPath)
        } else {
            downloadListener.onFailure(error)
        }

    }

}