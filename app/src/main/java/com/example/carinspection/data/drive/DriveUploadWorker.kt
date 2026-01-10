package com.example.carinspection.data.drive

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DriveUploadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val filePath = inputData.getString(KEY_FILE_PATH) ?: return@withContext Result.failure()
        val mimeType = inputData.getString(KEY_MIME_TYPE) ?: return@withContext Result.failure()
        val folderName = inputData.getString(KEY_FOLDER_NAME) ?: return@withContext Result.failure()
        val targetName = inputData.getString(KEY_TARGET_NAME) ?: return@withContext Result.failure()
        val overwrite = inputData.getBoolean(KEY_OVERWRITE, false)

        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
            ?: return@withContext Result.retry()

        val drive = DriveServiceFactory.create(applicationContext, account)
        val repository = GoogleDriveRepository(drive)
        val folderId = repository.getOrCreateFolder(folderName)
        if (overwrite) {
            repository.uploadOrUpdateFile(File(filePath), mimeType, folderId, targetName)
        } else {
            repository.uploadFile(File(filePath), mimeType, folderId, targetName)
        }
        Result.success()
    }

    companion object {
        const val KEY_FILE_PATH = "file_path"
        const val KEY_MIME_TYPE = "mime_type"
        const val KEY_FOLDER_NAME = "folder_name"
        const val KEY_TARGET_NAME = "target_name"
        const val KEY_OVERWRITE = "overwrite"
    }
}
