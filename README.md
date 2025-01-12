# Flashlight App

This Android app allows users to control their device's flashlight with added features like blinking functionality and adjustable blink speed using a seek bar. It provides a user-friendly interface for seamless operation.

## Features

- Turn the flashlight **ON/OFF** with a simple toggle button.
- Enable **Blink Mode**, where the flashlight blinks at adjustable speeds.
- Use a **SeekBar** to fine-tune the blinking frequency.
- Includes a **Widget** for quick access to flashlight controls, including toggle and blink speed adjustments.
- Elegant UI with gradient buttons and dynamic text colors for a modern look.

## Screenshots

**𝐂𝐨𝐦𝐢𝐧𝐠 𝐒𝐨𝐨𝐧**

## Technologies Used

- **Kotlin** for Android app development.
- **XML** for designing the app's UI.
- **Camera2 API** for flashlight control.
- **SeekBar** for user-friendly frequency adjustments.
- **Widgets** for home screen functionality.
- **Handler and Looper** for managing the blinking mechanism.
- **Gradients** for dynamic UI updates.

## Prerequisites

- Android Studio installed.
- Basic knowledge of Android components like `CameraManager`, `Handler`, and `AppWidgetProvider`.

## Architecture

### Flashlight Control
The app uses Android's `Camera2 API` to control the flashlight. It checks for hardware compatibility and toggles the flashlight state using `setTorchMode`. 

- **Blinking Mechanism:** 
  - A `Handler` posts a `Runnable` to toggle the flashlight ON/OFF at intervals defined by the seek bar's progress.

### Widgets
A custom widget provides users with quick access to the flashlight features:
- **Toggle Flashlight**
- **Increase/Decrease Blink Speed**

The widget listens for broadcast intents, processes user actions, and updates the UI.

## Learn More

For insights into the Camera2 API and widget integration, refer to Android’s official documentation:
- [Camera2 API Guide](https://developer.android.com/reference/android/hardware/camera2/package-summary)
- [App Widgets](https://developer.android.com/guide/topics/appwidgets/overview)

For a detailed guide, check out the Medium article:
- **Medium Article:** [How to Build a Flashlight App with Advanced Features](https://medium.com/@abhisheksuman413/mastering-android-flashlight-app-with-kotlin-and-xml-a0551adbf9b5)

## Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/abhisheksuman413/Flash-Light
   ```

2. Open the project in Android Studio.

3. Run the app on a device with a flashlight.

## Future Improvements

- Add support for custom themes.
- Introduce voice control for toggling flashlight modes.
- Optimize power usage for prolonged use in blink mode.
