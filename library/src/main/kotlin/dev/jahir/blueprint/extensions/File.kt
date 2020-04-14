package dev.jahir.blueprint.extensions

import android.graphics.Bitmap
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

internal fun File.saveIcon(icon: Bitmap?): Boolean {
    icon ?: return false
    val os: FileOutputStream? = FileOutputStream(this)
    try {
        os?.use { icon.compress(Bitmap.CompressFormat.PNG, 100, it) }
    } catch (e: Exception) {
        return false
    }
    return true
}

internal fun File.saveAll(content: String?): Boolean =
    saveAll(content?.toByteArray(charset("UTF-8")))

internal fun File.saveAll(content: ByteArray?): Boolean {
    content ?: return false
    val os: FileOutputStream? = FileOutputStream(this)
    try {
        os?.use { it.write(content) }
    } catch (e: Exception) {
        return false
    }
    return true
}

internal fun File.zip(files: List<File>): File {
    if (files.isEmpty()) return this
    var out: ZipOutputStream? = null
    var ins: InputStream? = null
    try {
        out = ZipOutputStream(FileOutputStream(this))
        for (fi in files) {
            out.putNextEntry(ZipEntry(fi.name))
            ins = FileInputStream(fi)
            var read: Int = -1
            val buffer = ByteArray(2048)
            while ({ read = ins.read(buffer);read }() != -1)
                out.write(buffer, 0, read)
            try {
                ins.close()
            } catch (e: Exception) {
            }
            out.closeEntry()
        }
    } finally {
        try {
            ins?.close()
        } catch (e: Exception) {
        }
        try {
            out?.close()
        } catch (e: Exception) {
        }
    }
    return this
}