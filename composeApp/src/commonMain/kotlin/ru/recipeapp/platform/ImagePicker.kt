package ru.recipeapp.platform

interface ImagePicker {
    fun launch()
}

expect fun rememberImagePicker(
    onImagePicked: (ByteArray) -> Unit
): ImagePicker
