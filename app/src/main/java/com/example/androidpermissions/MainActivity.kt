package com.example.androidpermissions

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import com.example.androidpermissions.databinding.ActivityMainBinding
import com.example.jdrodi.BaseActivity
import com.example.jdrodi.utilities.showPermissionsAlert
import com.example.jdrodi.utilities.toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


private const val REQ_ADD_IMAGE_TO_ALBUM = 2
private const val REQ_DOWNLOAD_FILE = 3
private const val REQ_PICK_FILE = 4
private const val REQ_CREATE_WRITE_REQUEST = 5
private const val REQ_ALL_FILES_ACCESS_PERMISSION = 6

val storagePermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

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
            binding.btnAddImageToAlbum -> {
                checkPermissions(REQ_ADD_IMAGE_TO_ALBUM)
            }
            binding.btnDownloadFile -> {
                checkPermissions(REQ_DOWNLOAD_FILE)
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

            REQ_ADD_IMAGE_TO_ALBUM -> {

            }
            REQ_DOWNLOAD_FILE -> {
                downloadFile()
            }
            REQ_PICK_FILE -> {

            }
            REQ_CREATE_WRITE_REQUEST -> {

            }
            REQ_ALL_FILES_ACCESS_PERMISSION -> {

            }
        }
    }


    private fun downloadFile() {
        val download = DownloadFileFromURL(mContext, object : DownloadFileFromURL.DownloadListener {
            override fun onSuccess(path: String) {
                toast("File is downloaded successfully at $path")
            }
            override fun onFailure(error: String) {
                toast(error)
            }
        })
        download.execute()
    }


}