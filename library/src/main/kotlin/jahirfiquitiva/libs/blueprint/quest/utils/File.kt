package jahirfiquitiva.libs.blueprint.quest.utils

import android.graphics.Bitmap
import jahirfiquitiva.libs.blueprint.helpers.utils.BL
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by Allan Wang on 2016-08-20.
 */

internal fun File.wipe(): Int {
    if (!exists()) return 0
    var count = 0
    if (isDirectory) {
        val folderContent = listFiles()
        if (folderContent != null && folderContent.isNotEmpty()) {
            for (fileInFolder in folderContent) {
                count += fileInFolder.wipe()
            }
        }
    }
    delete()
    return count
}

@Throws(Exception::class)
internal fun File.saveIcon(icon: Bitmap) {
    var os: FileOutputStream? = null
    try {
        os = FileOutputStream(this)
        icon.compress(Bitmap.CompressFormat.PNG, 100, os)
        os.flush()
    } catch (e: Exception) {
        BL.e("Error", e)
    } finally {
        os?.closeQuietly()
    }
}

internal fun File.saveAll(content: String) {
    saveAll(content.toByteArray(charset("UTF-8")))
}

internal fun File.saveAll(content: ByteArray) {
    var os: OutputStream? = null
    try {
        os = FileOutputStream(this)
        os.write(content)
        os.flush()
    } catch (e: Exception) {
        BL.e("Error", e)
    } finally {
        os?.closeQuietly()
    }
}

internal fun Closeable.closeQuietly() {
    try {
        close()
    } catch (ignored: Exception) {
    }
}

@Throws(Exception::class)
internal fun File.zip(files: List<File>) {
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
            ins.closeQuietly()
            out.closeEntry()
        }
    } finally {
        ins?.closeQuietly()
        out?.closeQuietly()
    }
}