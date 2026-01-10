package com.example.carinspection.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.carinspection.data.drive.DriveUploadWorker
import com.example.carinspection.data.excel.ExcelDailyInspectionWriter
import com.example.carinspection.data.model.DailyInspection
import java.io.File
import java.time.LocalDate

class InspectionRepository(
    private val context: Context,
    private val excelWriter: ExcelDailyInspectionWriter
) {
    fun appendDailyInspection(inspection: DailyInspection) {
        val file = File(context.filesDir, DAILY_MASTER_FILE)
        excelWriter.appendInspection(file, inspection)
        enqueueUpload(
            file = file,
            mimeType = MIME_EXCEL,
            folderName = DAILY_FOLDER,
            targetName = DAILY_MASTER_FILE,
            overwrite = true
        )
    }

    fun uploadWeeklyPhoto(file: File, stepLabel: String, driverName: String, date: LocalDate) {
        val safeDriver = driverName.trim().replace("\\s+".toRegex(), "")
        val name = "${date}_${stepLabel}_${safeDriver}.jpg"
        enqueueUpload(
            file = file,
            mimeType = MIME_JPEG,
            folderName = WEEKLY_FOLDER,
            targetName = name,
            overwrite = false
        )
    }

    private fun enqueueUpload(
        file: File,
        mimeType: String,
        folderName: String,
        targetName: String,
        overwrite: Boolean
    ) {
        val data = Data.Builder()
            .putString(DriveUploadWorker.KEY_FILE_PATH, file.absolutePath)
            .putString(DriveUploadWorker.KEY_MIME_TYPE, mimeType)
            .putString(DriveUploadWorker.KEY_FOLDER_NAME, folderName)
            .putString(DriveUploadWorker.KEY_TARGET_NAME, targetName)
            .putBoolean(DriveUploadWorker.KEY_OVERWRITE, overwrite)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<DriveUploadWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    fun isOnline(): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivity.activeNetwork ?: return false
        val capabilities = connectivity.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        private const val DAILY_MASTER_FILE = "daily_master.xlsx"
        private const val DAILY_FOLDER = "Daily Inspections"
        private const val WEEKLY_FOLDER = "Weekly Inspections"
        private const val MIME_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        private const val MIME_JPEG = "image/jpeg"
    }
}
