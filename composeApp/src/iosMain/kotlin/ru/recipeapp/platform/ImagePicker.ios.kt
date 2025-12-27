package ru.recipeapp.platform

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePicker(
    onImagePicked: (ByteArray) -> Unit
): ImagePicker = object : ImagePicker {
    override fun launch() {}
}
