package com.example.uselessuninstall.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.example.uselessuninstall.data.InstalledAppRetriever
import com.example.uselessuninstall.manager.RandomAppSelector
import com.example.uselessuninstall.manager.UninstallManager
import com.example.uselessuninstall.model.AppInfo
import com.example.uselessuninstall.ui.state.UninstallerUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import sun.misc.Unsafe

class RandomUninstallerViewModelTest {

    private val realApp1 = AppInfo("Real App 1", "com.real.app1")
    private val realApp2 = AppInfo("Real App 2", "com.real.app2")
    private val candidatePool = listOf(realApp1, realApp2)

    private fun createTestScope(): CoroutineScope = CoroutineScope(Dispatchers.Unconfined)

    private fun createDummyContext(): Context {
        val field = Unsafe::class.java.getDeclaredField("theUnsafe")
        field.isAccessible = true
        val unsafe = field.get(null) as Unsafe
        return unsafe.allocateInstance(ContextWrapper::class.java) as Context
    }

    private class FakeRetriever(
        var appsToReturn: List<AppInfo> = emptyList(),
        var throwException: Boolean = false
    ) : InstalledAppRetriever {
        override fun getInstalledUserApps(): List<AppInfo> {
            if (throwException) {
                throw RuntimeException("Simulated PackageManager failure")
            }
            return appsToReturn
        }
    }

    private class FakeSelector(var appToSelect: AppInfo? = null) : RandomAppSelector {
        override fun selectRandomApp(apps: List<AppInfo>): AppInfo? {
            return appToSelect ?: apps.firstOrNull()
        }
    }

    private class FakeUninstallManager : UninstallManager {
        var requestedPackage: String? = null
        var shouldSucceed: Boolean = true
        var uninstallCalledCount: Int = 0

        override fun createUninstallIntent(packageName: String): Intent {
            throw UnsupportedOperationException()
        }

        override fun requestUninstall(context: Context, packageName: String): Boolean {
            uninstallCalledCount++
            requestedPackage = packageName
            return shouldSucceed
        }
    }

    @Test
    fun testInitialLoadRetrievesAppsSuccessfully() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        assertEquals(2, viewModel.installedApps.value.size)
        assertTrue(viewModel.installedApps.value.contains(realApp1))
        assertTrue(viewModel.installedApps.value.contains(realApp2))
        assertTrue("UI state should be Home after successful load", viewModel.uiState.value is UninstallerUiState.Home)
    }

    @Test
    fun testNoEligibleAppsFoundTransitionsToError() {
        val retriever = FakeRetriever(appsToReturn = emptyList())
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        assertTrue(viewModel.installedApps.value.isEmpty())
        val state = viewModel.uiState.value
        assertTrue("UI state should be Error when no apps are found", state is UninstallerUiState.Error)
        assertTrue((state as UninstallerUiState.Error).message.contains("No eligible applications"))
    }

    @Test
    fun testStartAnalysisWithEmptyAppListShowsError() {
        val retriever = FakeRetriever(appsToReturn = emptyList())
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()

        val state = viewModel.uiState.value
        assertTrue("UI state should remain Error when attempting analysis with empty pool", state is UninstallerUiState.Error)
    }

    @Test
    fun testRetrievalExceptionTransitionsToErrorGracefully() {
        val retriever = FakeRetriever(throwException = true)
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        val state = viewModel.uiState.value
        assertTrue("UI state should be Error when retrieval throws", state is UninstallerUiState.Error)
        assertTrue((state as UninstallerUiState.Error).message.contains("System scan failure"))
    }

    @Test
    fun testStartAnalysisSelectsRealAppAndTransitionsToAnalyzing() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        val selector = FakeSelector(appToSelect = realApp2)
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            appSelector = selector,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()

        val selected = viewModel.selectedApp.value
        assertNotNull(selected)
        assertEquals("Real App 2", selected?.appName)
        assertEquals("com.real.app2", selected?.packageName)

        val state = viewModel.uiState.value
        assertTrue("UI state should be Analyzing during scan", state is UninstallerUiState.Analyzing)
        assertEquals(realApp2, (state as UninstallerUiState.Analyzing).app)
    }

    @Test
    fun testOnAnalysisSequenceCompletedTransitionsToRandomApp() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        val selector = FakeSelector(appToSelect = realApp1)
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            appSelector = selector,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()
        viewModel.onAnalysisSequenceCompleted()

        val state = viewModel.uiState.value
        assertTrue("UI state should be RandomApp after analysis sequence", state is UninstallerUiState.RandomApp)
        assertEquals(realApp1, (state as UninstallerUiState.RandomApp).app)
    }

    @Test
    fun testConfirmationAndDismissalFlow() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            appSelector = FakeSelector(appToSelect = realApp1),
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()
        viewModel.onAnalysisSequenceCompleted()

        viewModel.requestConfirmation()
        assertTrue(viewModel.uiState.value is UninstallerUiState.Confirming)

        viewModel.dismissConfirmation()
        assertTrue(viewModel.uiState.value is UninstallerUiState.RandomApp)
    }

    @Test
    fun testCountdownDoesNotTriggerUninstall() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        val uninstallManager = FakeUninstallManager()
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            appSelector = FakeSelector(appToSelect = realApp1),
            uninstallManager = uninstallManager,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()
        viewModel.onAnalysisSequenceCompleted()
        viewModel.startCountdown()

        assertTrue(viewModel.uiState.value is UninstallerUiState.Countdown)
        // Verify UninstallManager has NOT been called automatically
        assertEquals("Countdown must NOT trigger uninstall", 0, uninstallManager.uninstallCalledCount)
        assertNull(uninstallManager.requestedPackage)
    }

    @Test
    fun testExecuteUninstallOnlyCalledUponExplicitConfirmation() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        val uninstallManager = FakeUninstallManager()
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            appSelector = FakeSelector(appToSelect = realApp1),
            uninstallManager = uninstallManager,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()
        viewModel.onAnalysisSequenceCompleted()
        viewModel.startCountdown()

        assertEquals("No uninstall before explicit action", 0, uninstallManager.uninstallCalledCount)

        val dummyContext = createDummyContext()

        // User explicitly taps the final action button
        viewModel.executeUninstall(dummyContext)

        assertEquals("UninstallManager must be called exactly once upon explicit confirmation", 1, uninstallManager.uninstallCalledCount)
        assertEquals("com.real.app1", uninstallManager.requestedPackage)

        val state = viewModel.uiState.value
        assertTrue("UI state should be Result after dispatching uninstall", state is UninstallerUiState.Result)
        assertTrue((state as UninstallerUiState.Result).isSuccess)
    }

    @Test
    fun testExecuteUninstallWithNoSelectedAppInfoTransitionsToError() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        val dummyContext = createDummyContext()
        viewModel.executeUninstall(dummyContext)

        val state = viewModel.uiState.value
        assertTrue("Must transition to Error when no AppInfo is selected", state is UninstallerUiState.Error)
        assertTrue((state as UninstallerUiState.Error).message.contains("No application candidate selected"))
    }

    @Test
    fun testExecuteUninstallWithInvalidPackageNameTransitionsToError() {
        val invalidApp = AppInfo("Blank App", "   ")
        val retriever = FakeRetriever(appsToReturn = listOf(invalidApp))
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            appSelector = FakeSelector(appToSelect = invalidApp),
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()
        val dummyContext = createDummyContext()
        viewModel.executeUninstall(dummyContext)

        val state = viewModel.uiState.value
        assertTrue("Must transition to Error when package name is blank", state is UninstallerUiState.Error)
        assertTrue((state as UninstallerUiState.Error).message.contains("Invalid package name"))
    }

    @Test
    fun testNoActivityAvailableToLaunchIntentHandledGracefully() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        // Simulate no Activity available to handle Intent -> requestUninstall returns false
        val uninstallManager = FakeUninstallManager().apply { shouldSucceed = false }
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            appSelector = FakeSelector(appToSelect = realApp1),
            uninstallManager = uninstallManager,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()
        viewModel.onAnalysisSequenceCompleted()

        val dummyContext = createDummyContext()
        viewModel.executeUninstall(dummyContext)

        val state = viewModel.uiState.value
        assertTrue(state is UninstallerUiState.Result)
        assertFalse("Result must reflect failure when no activity can handle Intent", (state as UninstallerUiState.Result).isSuccess)
        assertTrue((state as UninstallerUiState.Result).message?.contains("No activity available") == true)
    }

    @Test
    fun testCancelToHomeClearsSelectedApp() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            appSelector = FakeSelector(appToSelect = realApp1),
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()
        assertNotNull(viewModel.selectedApp.value)

        viewModel.cancelToHome()
        assertNull(viewModel.selectedApp.value)
        assertTrue(viewModel.uiState.value is UninstallerUiState.Home)
    }

    @Test
    fun testExecuteUninstallWithExplicitAppInfoDispatchesTargetApp() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        val fakeUninstallManager = FakeUninstallManager().apply { shouldSucceed = true }
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            appSelector = FakeSelector(appToSelect = realApp1),
            uninstallManager = fakeUninstallManager,
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()
        viewModel.onAnalysisSequenceCompleted()

        val explicitApp = AppInfo("Explicit Target App", "com.explicit.target")
        val dummyContext = createDummyContext()
        viewModel.executeUninstall(dummyContext, explicitApp)

        val state = viewModel.uiState.value
        assertTrue("UI state must be Result", state is UninstallerUiState.Result)
        val resultState = state as UninstallerUiState.Result
        assertTrue("Uninstall must succeed", resultState.isSuccess)
        assertEquals("Target app must match explicit AppInfo", explicitApp, resultState.app)
        assertEquals("com.explicit.target", fakeUninstallManager.requestedPackage)
    }

    @Test
    fun testExecuteUninstallWithExplicitAppInfoWithBlankPackageNameFailsGracefully() {
        val retriever = FakeRetriever(appsToReturn = candidatePool)
        val viewModel = RandomUninstallerViewModel(
            appRetriever = retriever,
            appSelector = FakeSelector(appToSelect = realApp1),
            ioDispatcher = Dispatchers.Unconfined,
            customScope = createTestScope()
        )

        viewModel.startAnalysis()
        val blankApp = AppInfo("Blank App", "")
        val dummyContext = createDummyContext()
        viewModel.executeUninstall(dummyContext, blankApp)

        val state = viewModel.uiState.value
        assertTrue("Must transition to Error when explicit package name is blank", state is UninstallerUiState.Error)
        assertTrue((state as UninstallerUiState.Error).message.contains("Invalid package name"))
    }
}
