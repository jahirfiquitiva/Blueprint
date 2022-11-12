package dev.jahir.blueprint.extensions

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import dev.jahir.frames.extensions.resources.getUri
import java.io.File
import java.io.FileOutputStream

private fun Context.getUriFromResource(id: Int): Uri? {
    return Uri.parse(
        "${ContentResolver.SCHEME_ANDROID_RESOURCE}://" +
                "${resources.getResourcePackageName(id)}/" +
                "${resources.getResourceTypeName(id)}/" + resources.getResourceEntryName(id)
    )
}

fun Bitmap.getUri(context: Context, name: String, extension: String = ".png"): Uri? {
    val iconFile = File(context.cacheDir, name + extension)
    val fos: FileOutputStream?
    try {
        fos = FileOutputStream(iconFile)
        compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()

        var uri = iconFile.getUri(context)
        if (uri == null) uri = context.getUriFromResource(context.drawableRes(name))
        if (uri == null)
            uri = Uri.parse(
                "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/" +
                        "${context.drawableRes(name)}"
            )
        return uri
    } catch (e: Exception) {
        return null
    }
}