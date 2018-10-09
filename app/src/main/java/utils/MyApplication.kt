package utils

import android.app.Application
import java.io.File


class MyApplication : Application() {

    lateinit var instance: MyApplication

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun clearApplicationData() {
        val cacheDirectory = cacheDir
        val applicationDirectory = File(cacheDirectory.parent)
        if (applicationDirectory.exists()) {
            val fileNames = applicationDirectory.list()
            for (fileName in fileNames) {
                if (fileName != "lib") {
                    deleteFile(File(applicationDirectory, fileName))
                }
            }
        }
    }

    fun deleteFile(file: File?): Boolean {
        var deletedAll = true
        if (file != null) {
            if (file.isDirectory) {
                val children = file.list()
                for (i in children.indices) {
                    deletedAll = deleteFile(File(file, children[i])) && deletedAll
                }
            } else {
                deletedAll = file.delete()
            }
        }

        return deletedAll
    }

}