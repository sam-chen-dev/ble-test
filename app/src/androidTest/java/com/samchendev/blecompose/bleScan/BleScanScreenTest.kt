package com.samchendev.blecompose.bleScan

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class BleScanScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun scanButton_isDisplayed() {
        composeTestRule.setContent {
            BleScanContent(
                uiState = BleScanUiState(
                    scannedDevices = emptyList(),
                    isScanning = false,
                    onStartScanTrigger = {},
                    onStopScanTrigger = {}
                ),
                onScanClick = {},
                onDeviceClick = {}
            )
        }

        composeTestRule.onNodeWithText("Scan").assertIsDisplayed()
    }
}