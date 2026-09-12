package com.example.uselessuninstall.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.uselessuninstall.model.AppInfo

/**
 * Interface defining the contract for retrieving installed applications.
 *
 * Responsible ONLY for providing the pool of user-installed applications.
 * Does not analyze usage, rank, or select applications.
 */
interface InstalledAppRetriever {
    /**
     * Retrieves all user-installed applications from the device, excluding system applications.
     *
     * @return List of [AppInfo] representing available user apps.
     */
    fun getInstalledUserApps(): List<AppInfo>
}

/**
 * Android [PackageManager]-based implementation for retrieving installed user applications.
 *
 * @param context Android [Context] used to query package information.
 */
class AndroidInstalledAppRetriever(
    private val context: Context
) : InstalledAppRetriever {

    private val packageManager: PackageManager = context.packageManager
    private val currentAppPackageName: String = context.packageName

    override fun getInstalledUserApps(): List<AppInfo> {
        val installedApplications: List<ApplicationInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }

        return installedApplications
            .filter { appInfo -> isUserApp(appInfo, currentAppPackageName) }
            .map { appInfo ->
                AppInfo(
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    packageName = appInfo.packageName,
                    appIcon = packageManager.getApplicationIcon(appInfo)
                )
            }
    }

    companion object {
        /**
         * Determines whether an application is a user-installed application.
         *
         * An application is considered user-installed if:
         * 1. It is not this application itself ([currentAppPackageName]).
         * 2. It does not have the [ApplicationInfo.FLAG_SYSTEM] flag set (excluding pre-installed/system apps).
         *
         * @param appInfo The [ApplicationInfo] of the app to evaluate.
         * @param currentAppPackageName The package name of this app to exclude.
         * @return True if the app is a user-installed application, false otherwise.
         */
        fun isUserApp(appInfo: ApplicationInfo, currentAppPackageName: String = ""): Boolean {
            if (appInfo.packageName == currentAppPackageName) {
                return false
            }
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            return !isSystem
        }
    }
}
