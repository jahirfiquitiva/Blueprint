package dev.jahir.blueprint.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.util.DisplayMetrics
import androidx.annotation.StringRes
import dev.jahir.blueprint.BuildConfig
import dev.jahir.blueprint.R
import dev.jahir.frames.extensions.context.currentVersionCode
import dev.jahir.frames.extensions.context.currentVersionName
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.context.toast

/**
 * Created by Allan Wang on 2017-06-20.
 *
 * Helper tool to call an email intent with device information
 */
internal class EmailBuilder(
    private val email: String,
    private val subject: String,
    private var message: String = "Write here."
) {
    var formatAsHtml: Boolean = false
    private var attachment: Uri? = null

    fun addAttachment(uri: Uri?) {
        uri ?: return
        attachment = uri
    }

    /**
     * Optional handler to update the created intent
     */
    var extras: Intent.() -> Unit = {}

    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    private fun getIntent(context: Context): Intent {
        val nl = if (formatAsHtml) "<br/>" else "\n" // New Line
        val intent = Intent(Intent.ACTION_SEND)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            .putExtra(Intent.EXTRA_SUBJECT, subject)

        val emailBuilder = StringBuilder()
        emailBuilder.append(message).append("$nl$nl")

        val installer =
            context.packageManager.getInstallerPackageName(context.packageName) ?: "None"

        val deviceItems = mutableMapOf(
            "OS Version" to "${System.getProperty(
                "os.version"
            )} (${Build.VERSION.INCREMENTAL})",
            "OS API Level" to Build.VERSION.SDK_INT,
            "Device (Manufacturer)" to "${Build.DEVICE} (${Build.MANUFACTURER})",
            "Model (Product)" to "${Build.MODEL} (${Build.PRODUCT})",
            "Blueprint Version" to BuildConfig.DASHBOARD_VERSION,
            "App Version" to "${context.currentVersionCode} (${context.currentVersionName}) from $installer"
        )
        if (context is Activity) {
            val metric = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(metric)
            deviceItems["Screen Dimensions"] = "${metric.widthPixels} x ${metric.heightPixels}"
        }
        deviceItems.forEach { (k, v) -> emailBuilder.append("$k: $v$nl") }

        val content = if (formatAsHtml) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                Html.fromHtml(emailBuilder.toString(), Html.FROM_HTML_MODE_LEGACY)
            else Html.fromHtml(emailBuilder.toString())
        } else emailBuilder.toString()

        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.type = "text/plain"
        return intent
    }

    fun buildIntent(context: Context): Intent? {
        var resultIntent: Intent? = null
        val intent = getIntent(context)
        intent.extras()
        val packageName = intent.resolveActivity(context.packageManager)?.packageName
        packageName?.let {
            val attachment = this.attachment
            if (attachment != null) {
                context.grantUriPermission(
                    packageName, attachment, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                intent.putExtra(Intent.EXTRA_STREAM, attachment)
            }
            resultIntent = Intent.createChooser(intent, context.string(R.string.send_using))
        } ?: { context.toast(R.string.error) }()
        return resultIntent
    }

    /**
     * Create the intent and send the request when possible
     * If a stream uri is added, it will automatically be flagged to pass on read permissions
     */
    fun execute(context: Context) {
        context.startActivity(buildIntent(context))
    }
}

fun Context.sendEmail(
    @StringRes emailId: Int,
    @StringRes subjectId: Int
) = sendEmail(string(emailId), string(subjectId))

fun Context.sendEmail(
    @StringRes emailId: Int,
    @StringRes subjectId: Int,
    @StringRes messageId: Int
) = sendEmail(string(emailId), string(subjectId), string(messageId))

fun Context.sendEmail(email: String, subject: String) {
    EmailBuilder(email, subject).execute(this@sendEmail)
}

fun Context.sendEmail(email: String, subject: String, message: String) {
    EmailBuilder(email, subject, message).execute(this@sendEmail)
}