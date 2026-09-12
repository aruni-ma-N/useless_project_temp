package com.example.uselessuninstall.manager

import com.example.uselessuninstall.model.AppInfo
import kotlin.random.Random

/**
 * Modular component contract for randomly selecting an application from candidate apps.
 *
 * Selection philosophy:
 * - Purely random selection using standard pseudo-random number generator.
 * - Does NOT analyze or inspect app usefulness.
 * - Does NOT check app usage frequency or screen time.
 * - Does NOT check app size or storage footprint.
 * - Does NOT inspect when the app was installed.
 * - Does NOT score, prioritize, or rank applications.
 *
 * Randomness itself is the sole criterion.
 */
interface RandomAppSelector {

    /**
     * Randomly selects an application from the provided [apps] list.
     *
     * @param apps The candidate pool of [AppInfo] items.
     * @return A randomly selected [AppInfo], or null if [apps] is empty.
     */
    fun selectRandomApp(apps: List<AppInfo>): AppInfo?

    /**
     * Operator overload allowing the selector to be invoked directly as a function: `selector(apps)`.
     *
     * @param apps The candidate pool of [AppInfo] items.
     * @return A randomly selected [AppInfo], or null if [apps] is empty.
     */
    operator fun invoke(apps: List<AppInfo>): AppInfo? = selectRandomApp(apps)

    companion object : RandomAppSelector {
        private val defaultInstance = DefaultRandomAppSelector()

        /**
         * Convenience method to select a random app without needing to instantiate a selector.
         */
        override fun selectRandomApp(apps: List<AppInfo>): AppInfo? {
            return defaultInstance.selectRandomApp(apps)
        }
    }
}

/**
 * Standard implementation of [RandomAppSelector] using Kotlin's standard random functionality.
 *
 * @property random The [Random] instance used for selection (defaults to [Random.Default]).
 */
class DefaultRandomAppSelector(
    private val random: Random = Random.Default
) : RandomAppSelector {

    /**
     * Selects an application randomly using Kotlin's standard [Random] library.
     *
     * @param apps Candidate list of user-installed [AppInfo] items.
     * @return A randomly chosen [AppInfo] from [apps], or null if [apps] is empty.
     */
    override fun selectRandomApp(apps: List<AppInfo>): AppInfo? {
        return apps.randomOrNull(random)
    }
}
