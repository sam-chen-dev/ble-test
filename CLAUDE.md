# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single unit test class
./gradlew test --tests "com.samchendev.blecompose.ExampleUnitTest"
```

## Architecture

This is an Android BLE (Bluetooth Low Energy) scanner and connector app built with Jetpack Compose.

**Layer structure:**
- `di/` — Single Koin `appModule` wiring all singletons and ViewModels
- `managers/` — Two independent BLE stacks:
  - Scan: `BleController` (interface) → `BleControllerImpl` (Android BLE scan API) → `BleManager` (deduplicates results, exposes `StateFlow<List<BluetoothDevice>>`)
  - Connect: `GattController` (interface, also defines `ConnectionState` enum) → `GattControllerImpl` (`BluetoothGatt` + `BluetoothGattCallback`) → `BleConnectManager` (thin wrapper, exposes `connectionState` flow)
- `bleScan/` — Scan feature: list scanned devices, tap to navigate to connect screen
- `bleConnect/` — Connect feature: shows device address/status, Connect/Disconnect buttons; `BleConnectViewModel` receives `deviceAddress` and `deviceName` via Koin `parametersOf`; calls `BleConnectManager.close()` in `onCleared()`
- `navigation/` — Navigation3 setup: `AppNav.kt` defines `NavKey` types and `EntryProviderScope` extension functions; `NavDisplay.kt` wires the back stack with slide transitions

**Key patterns:**
- UiState data classes carry action lambdas (`onConnectTrigger`, `onDisconnectTrigger`, etc.) so screens call `uiState.onXxx()` directly
- `toStateFlow()` from `util-kotlin` converts a `Flow` to `StateFlow` with a scope and initial value — used in every ViewModel
- Navigation uses **Navigation3** (`androidx.navigation3`), not the older `NavController`/`NavHost` API — entries are `EntryProviderScope` extension functions called inside `entryProvider { }`
- Navigation callbacks (`onDeviceClick`, `onBack`) are passed into screens as lambdas from the entry functions in `AppNav.kt`, keeping navigation out of ViewModels
- ViewModels with constructor parameters use Koin's `viewModel { params -> ... }` in the module and `koinViewModel(parameters = { parametersOf(...) })` at the call site
- DI uses **Koin 4.x** with `koin-androidx-compose`; ViewModels injected via `koinViewModel()`
- `util-kotlin` provides `Button(text, onClick, modifier, isEnabled)` and `IconButton(drawableRes, contentDescription, onClick)` — note the `isEnabled` parameter name (not `enabled`)

**BLE notes:**
- `BleControllerImpl` hard-filters by `TARGET_ADDRESS = "DD:88:00:00:09:3D"` — remove or parameterise to scan all devices
- `GattControllerImpl.close()` calls both `disconnect()` and `close()` on the `BluetoothGatt` immediately; this is safe because it's only called from `onCleared()`
- `BLUETOOTH_CONNECT` / `BLUETOOTH_SCAN` permissions are requested in `BleScanScreen` before scanning; the connect screen assumes they're already granted

**Permission chain in `BleScanScreen`:** Bluetooth permission → Location permission → Enable Bluetooth → Enable GPS → start scan. All helpers (`isBluetoothPermissionGranted`, `launchBluetoothPermission`, etc.) come from `util-kotlin`.

## Key Config

- `minSdk = 29`, `targetSdk = 37`, `compileSdk = 37`
- Kotlin `2.4.0`, AGP `9.2.1`, Compose BOM `2026.06.00`
- Package: `com.samchendev.blecompose`
