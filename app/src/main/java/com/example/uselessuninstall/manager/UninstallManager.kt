package com.example.uselessuninstall.manager

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.uselessuninstall.model.AppInfo

/**
 * Modular component contract responsible for initiating the standard Android app uninstallation flow.
 *
 * System constraints:
 * - Uses standard Android uninstallation [Intent.ACTION_DELETE].
 * - Prompts the user with Android's official system confirmation dialog.
 * - Does NOT attempt silent/background uninstallation.
 * - Does NOT require root access or device-owner / privileged APIs.
 * - Does NOT assess whether the app is useful or useless.
 * - Does NOT contain random selection logic.
 */
interface UninstallManager {

    /**
     * Constructs the standard Android uninstallation [Intent] for the given [packageName].
     *
     * The Intent is constructed with:
     * - Action: [Intent.ACTION_DELETE]
     * - Data: Uri.parse("package:$packageName")
     *
     * @param packageName The application ID/package name to uninstall.
     * @return Configured uninstallation [Intent].
     */
    fun createUninstallIntent(packageName: String): Intent

    /**
     * Constructs the standard Android uninstallation [Intent] for the given [appInfo].
     *
     * @param appInfo The [AppInfo] representing the application to uninstall.
     * @return Configured uninstallation [Intent].
     */
    fun createUninstallIntent(appInfo: AppInfo): Intent = createUninstallIntent(appInfo.packageName)

    /**
     * Requests uninstallation by launching the system confirmation prompt.
     *
     * @param context Android [Context] (preferably an [Activity]) used to resolve and launch the Intent.
     * @param packageName The application package name to uninstall.
     * @return True if the uninstall intent was resolved and launched successfully, false otherwise.
     */
    fun requestUninstall(context: Context, packageName: String): Boolean

    /**
     * Requests uninstallation of the application represented by [appInfo].
     *
     * @param context Android [Context] used to resolve and launch the Intent.
     * @param appInfo The [AppInfo] representing the application to uninstall.
     * @return True if the uninstall intent was resolved and launched successfully, false otherwise.
     */
    fun requestUninstall(context: Context, appInfo: AppInfo): Boolean =
        requestUninstall(context, appInfo.packageName)

    companion object : UninstallManager {
        const val TAG = "UninstallManager"
        const val URI_PACKAGE_SCHEME = "package"

        private val defaultInstance = AndroidUninstallManager()

        /**
         * Validates whether a package name is structurally suitable for an uninstall request.
         */
        fun isValidPackageName(packageName: String?): Boolean {
            return !packageName.isNullOrBlank()
        }

        /**
         * Helper that builds the package URI string formatted as `package:$packageName`.
         */
        fun buildPackageUriString(packageName: String): String {
            return "$URI_PACKAGE_SCHEME:$packageName"
        }

        override fun createUninstallIntent(packageName: String): Intent {
            return defaultInstance.createUninstallIntent(packageName)
        }

        override fun requestUninstall(context: Context, packageName: String): Boolean {
            return defaultInstance.requestUninstall(context, packageName)
        }
    }
}

/**
 * Helper extension to unwrap an [Activity] from an arbitrary [Context], traversing any [android.content.ContextWrapper]s.
 */
fun Context.findActivity(): Activity? {
    var current: Context? = this
    while (current is android.content.ContextWrapper) {
        if (current is Activity) {
            return current
        }
        current = current.baseContext
    }
    return null
}

/**
 * Standard implementation of [UninstallManager] delegating to the Android OS.
 */
class AndroidUninstallManager : UninstallManager {

    override fun createUninstallIntent(packageName: String): Intent {
        require(UninstallManager.isValidPackageName(packageName)) {
            "Package name must not be blank"
        }
        return Intent(Intent.ACTION_DELETE).apply {
            data = Uri.parse(UninstallManager.buildPackageUriString(packageName))
        }
    }

    override fun requestUninstall(context: Context, packageName: String): Boolean {
        if (!UninstallManager.isValidPackageName(packageName)) {
            Log.w(UninstallManager.TAG, "Cannot request uninstall: package name '$packageName' is blank or invalid")
            return false
        }

        val activity = context.findActivity()
        val launchContext = activity ?: context

        Log.i(
            UninstallManager.TAG,
            "Requesting uninstall for package='$packageName' using context=${context.javaClass.name}, resolvedActivity=${activity?.javaClass?.name}"
        )

        return try {
            val intent = createUninstallIntent(packageName)

            if (activity == null) {
                // If launching strictly outside an Activity (e.g. ApplicationContext), FLAG_ACTIVITY_NEW_TASK is mandatory.
                // When launching from an Activity, do NOT add FLAG_ACTIVITY_NEW_TASK as it disrupts the Activity task stack.
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            launchContext.startActivity(intent)
            Log.i(UninstallManager.TAG, "Successfully launched uninstall confirmation intent for package: $packageName")
            true
        } catch (e: ActivityNotFoundException) {
            Log.e(UninstallManager.TAG, "No Activity found to handle uninstallation of $packageName", e)
            false
        } catch (e: SecurityException) {
            Log.e(UninstallManager.TAG, "SecurityException while requesting uninstall for $packageName", e)
            false
        } catch (e: Exception) {
            Log.e(UninstallManager.TAG, "Failed to launch uninstall intent for $packageName", e)
            false
        }
    }
}
