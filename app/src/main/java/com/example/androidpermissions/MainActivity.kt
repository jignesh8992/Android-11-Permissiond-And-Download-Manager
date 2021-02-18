package com.example.androidpermissions

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.view.drawToBitmap
import com.example.androidpermissions.asynctask.DownloadFileFromURLTask
import com.example.androidpermissions.asynctask.SaveBitmapTask
import com.example.androidpermissions.callback.DownloadListener
import com.example.androidpermissions.databinding.ActivityMainBinding
import com.example.jdrodi.BaseActivity
import com.example.jdrodi.utilities.showPermissionsAlert
import com.example.jdrodi.utilities.toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


private const val REQ_ADD_IMAGE_TO_ALBUM = 1
private const val REQ_SAVE_BITMAP = 2
private const val REQ_DOWNLOAD_FILE = 3
private const val REQ_PICK_FILE = 4
private const val REQ_CREATE_WRITE_REQUEST = 5
private const val REQ_ALL_FILES_ACCESS_PERMISSION = 6

val storagePermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
private const val outputDir = "Android11Permissions"


/**
 * MainActivity.kt - Android 11 Permission and Storage manager demo
 * @author:  Jignesh N Patel
 * @date: 18-Feb-2021 08:55 AM
 */

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun getContext(): Activity {
        return this@MainActivity
    }

    override fun initActions() {

    }

    override fun initData() {

    }

    override fun onClick(view: View) {
        super.onClick(view)
        when (view) {
            binding.btnDownloadFile -> {
                checkPermissions(REQ_DOWNLOAD_FILE)
            }
            binding.btnSaveBitmap -> {
                checkPermissions(REQ_SAVE_BITMAP)
            }
            binding.btnAddImageToAlbum -> {
                checkPermissions(REQ_ADD_IMAGE_TO_ALBUM)
            }
            binding.btnPickFile -> {
                checkPermissions(REQ_PICK_FILE)
            }
            binding.btnWriteRequest -> {
                checkPermissions(REQ_CREATE_WRITE_REQUEST)
            }
            binding.btnManageExternalStorage -> {
                checkPermissions(REQ_ALL_FILES_ACCESS_PERMISSION)
            }
        }
    }

    private fun checkPermissions(reqType: Int) {
        Dexter.withContext(mContext)
            .withPermissions(*storagePermission)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        when {
                            report.areAllPermissionsGranted() -> {
                                performAction(reqType)
                            }
                            report.isAnyPermissionPermanentlyDenied -> {
                                showPermissionsAlert("")
                            }
                            else -> {
                                toast("Required Permissions not granted")
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    // Remember to invoke this method when the custom rationale is closed
                    // or just by default if you don't want to use any custom rationale.
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                toast(it.name)
            }
            .check()
    }

    private fun performAction(reqType: Int) {
        when (reqType) {

            REQ_DOWNLOAD_FILE -> {
                downloadFile()
            }
            REQ_SAVE_BITMAP -> {
                saveBitmap()
            }
            REQ_ADD_IMAGE_TO_ALBUM -> {

            }
            REQ_PICK_FILE -> {

            }
            REQ_CREATE_WRITE_REQUEST -> {

            }
            REQ_ALL_FILES_ACCESS_PERMISSION -> {

            }
        }
    }

    // [START saveBitmap]
    private fun saveBitmap() {
        val bitmap = binding.linearParent.drawToBitmap()
        val saveBitmap = SaveBitmapTask(mContext, bitmap, outputDir, object : DownloadListener {
            override fun onSuccess(path: String) {
                toast("Bitmap is saved successfully at $path")
            }

            override fun onFailure(error: String) {
                toast(error)
            }
        })
        saveBitmap.execute()
    }
    // [END saveBitmap]

    // [START downloadFile]
    private fun downloadFile() {
        val download = DownloadFileFromURLTask(mContext, outputDir, object : DownloadListener {
            override fun onSuccess(path: String) {
                toast("File is downloaded successfully at $path")
            }

            override fun onFailure(error: String) {
                toast(error)
            }
        })
        download.execute()
    }
    // [END downloadFile]

}