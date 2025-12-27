package ru.recipeapp.platform

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberImagePicker(
    onImagePicked: (ByteArray) -> Unit
): ImagePicker {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { readBytes(context, it) }?.let(onImagePicked)
    }

    return object : ImagePicker {
        override fun launch() = launcher.launch("image/*")
    }
}

private fun readBytes(context: Context, uri: Uri): ByteArray? =
    try {
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
    } catch (_: Throwable) {
        null
    }
