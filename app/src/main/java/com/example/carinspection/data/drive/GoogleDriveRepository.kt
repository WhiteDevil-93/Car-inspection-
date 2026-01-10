package com.example.carinspection.data.drive

import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File as LocalFile

class GoogleDriveRepository(
    private val drive: Drive
) {
    suspend fun uploadFile(
        localFile: LocalFile,
        mimeType: String,
        folderId: String,
        targetName: String
    ): String = withContext(Dispatchers.IO) {
        val metadata = File().apply {
            name = targetName
            parents = listOf(folderId)
        }
        val content = FileContent(mimeType, localFile)
        val result = drive.files().create(metadata, content)
            .setFields("id")
            .execute()
        result.id
    }

    suspend fun uploadOrUpdateFile(
        localFile: LocalFile,
        mimeType: String,
        folderId: String,
        targetName: String
    ): String = withContext(Dispatchers.IO) {
        val query = "name = '$targetName' and '$folderId' in parents and trashed = false"
        val existing = drive.files().list()
            .setQ(query)
            .setFields("files(id, name)")
            .execute()
            .files
            .firstOrNull()

        val content = FileContent(mimeType, localFile)
        if (existing != null) {
            return@withContext drive.files().update(existing.id, File(), content)
                .setFields("id")
                .execute()
                .id
        }

        val metadata = File().apply {
            name = targetName
            parents = listOf(folderId)
        }
        drive.files().create(metadata, content)
            .setFields("id")
            .execute()
            .id
    }

    suspend fun getOrCreateFolder(folderName: String): String = withContext(Dispatchers.IO) {
        val query = "mimeType = 'application/vnd.google-apps.folder' and name = '$folderName' and trashed = false"
        val existing = drive.files().list()
            .setQ(query)
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()
            .files
            .firstOrNull()

        if (existing != null) {
            return@withContext existing.id
        }

        val metadata = File().apply {
            name = folderName
            mimeType = "application/vnd.google-apps.folder"
        }
        drive.files().create(metadata)
            .setFields("id")
            .execute()
            .id
    }
}
