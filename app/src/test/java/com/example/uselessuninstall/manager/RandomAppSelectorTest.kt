package com.example.uselessuninstall.manager

import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.model.SampleApps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class RandomAppSelectorTest {

    private val selector: RandomAppSelector = DefaultRandomAppSelector()

    @Test
    fun testEmptyListReturnsNull() {
        val emptyList = emptyList<AppInfo>()
        val result = selector.selectRandomApp(emptyList)

        assertNull("Empty list must return null", result)
    }

    @Test
    fun testSingleItemListReturnsThatItem() {
        val singleList = listOf(SampleApps.instagram)
        val result = selector.selectRandomApp(singleList)

        assertNotNull("Single-item list must not return null", result)
        assertEquals("Single-item list must return that exact item", SampleApps.instagram, result)
    }

    @Test
    fun testMultiItemListReturnsAnItemFromSuppliedList() {
        val apps = SampleApps.sampleList // Contains 6 apps
        val result = selector.selectRandomApp(apps)

        assertNotNull("Multi-item list must not return null", result)
        assertTrue(
            "Selected app '${result?.appName}' must be present in the supplied list",
            apps.contains(result)
        )
    }

    @Test
    fun testMultipleSelectionsAlwaysYieldValidMembers() {
        val apps = listOf(
            SampleApps.duolingo,
            SampleApps.candyCrush,
            SampleApps.spotify
        )

        for (i in 1..100) {
            val selected = selector.selectRandomApp(apps)
            assertNotNull("Run #$i must return a non-null item", selected)
            assertTrue(
                "Run #$i selected '${selected?.appName}' which must be in the supplied pool",
                apps.contains(selected)
            )
        }
    }

    @Test
    fun testOperatorInvokeMatchesSelectRandomApp() {
        val apps = SampleApps.sampleList
        val emptyList = emptyList<AppInfo>()

        assertNull(selector(emptyList))

        val selected = selector(apps)
        assertNotNull(selected)
        assertTrue(apps.contains(selected))
    }

    @Test
    fun testCompanionObjectConvenienceMethod() {
        val apps = listOf(SampleApps.twitter, SampleApps.reddit)

        assertNull(RandomAppSelector.selectRandomApp(emptyList()))

        val selected = RandomAppSelector.selectRandomApp(apps)
        assertNotNull(selected)
        assertTrue(apps.contains(selected))
    }

    @Test
    fun testSeededRandomIsReproducible() {
        val apps = SampleApps.sampleList

        val selectorA = DefaultRandomAppSelector(Random(12345))
        val selectorB = DefaultRandomAppSelector(Random(12345))

        val resultA = selectorA.selectRandomApp(apps)
        val resultB = selectorB.selectRandomApp(apps)

        assertEquals("Same seed must yield the identical selection", resultA, resultB)
    }
}
