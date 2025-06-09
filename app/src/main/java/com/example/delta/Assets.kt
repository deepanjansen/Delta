package com.example.delta


import android.content.Context
import java.io.File
import java.io.FileOutputStream

class Assets(private val context: Context) {

    fun syncAssets(): String {
        val assetManager = context.assets
        val assetPath = "model3" // this is the folder name in assets/
        val outDir = File(context.filesDir, assetPath)
        copyAssetFolder(assetManager, assetPath, outDir.absolutePath)
        return outDir.absolutePath
    }

    private fun copyAssetFolder(assetManager: android.content.res.AssetManager, srcPath: String, dstPath: String) {
        val files = assetManager.list(srcPath) ?: return
        val dstDir = File(dstPath)
        if (!dstDir.exists()) dstDir.mkdirs()

        for (file in files) {
            val srcFile = "$srcPath/$file"
            val dstFile = "$dstPath/$file"
            if (assetManager.list(srcFile)?.isNotEmpty() == true) {
                copyAssetFolder(assetManager, srcFile, dstFile)
            } else {
                val inputStream = assetManager.open(srcFile)
                val outFile = File(dstFile)
                val outputStream = FileOutputStream(outFile)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
            }
        }
    }
}