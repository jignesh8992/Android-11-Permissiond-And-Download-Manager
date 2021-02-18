package com.example.androidpermissions.callback

interface DownloadListener {
    fun onSuccess(path: String)
    fun onFailure(error: String)
}