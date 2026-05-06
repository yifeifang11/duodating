# Double Take

**Double Take** is an Android application designed to make dating less intimidating by focusing on double dates. Instead of meeting strangers alone, you pair up with a friend to create a "Duo" profile and match with other pairs for a fun, social experience.

## 🚀 Features

- **Dual-User Profiles**: Create your own profile and then link up with a friend to form a "Duo".
- **Discovery Feed**: Swipe through other duos in your area. The discovery logic automatically excludes your own duo and people you've already interacted with.
- **Real-time Messaging**: Once two duos "like" each other, a group chat is created for all four participants.
- **Push Notifications**: Stay updated with real-time notifications for new messages, powered by Firebase Cloud Functions.
- **Rich Media**: Upload and showcase profile photos using Firebase Storage.
- **Prompts & Bio**: Express your personality through customizable prompts and bios.

## 🛠 Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 design system.
- **Language**: [Kotlin](https://kotlinlang.org/)
- **Asynchronous Programming**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html).
- **Backend**: 
    - **Firebase Authentication**: Secure user sign-in and account management.
    - **Cloud Firestore**: Real-time NoSQL database for users, duos, and chats.
    - **Firebase Storage**: Hosting for profile images.
    - **Cloud Functions**: Server-side logic for notifications and data triggers.
    - **Firebase Cloud Messaging (FCM)**: Reliable delivery of push notifications.
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/compose/) for efficient image rendering.
- **Architecture**: MVVM (Model-View-ViewModel) for a clean separation of concerns.

## 📦 Getting Started

### Prerequisites

- Android Studio (Latest stable version recommended)
- A Firebase Project

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/DuoDating.git
   ```

2. **Firebase Configuration**:
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with the package name `es.uc3m.duodating`.
   - Download the `google-services.json` file and place it in the `app/` directory.
   - Enable **Authentication** (Email/Password), **Firestore**, and **Storage**.

3. **Cloud Functions**:
   - Navigate to the `functions/` folder.
   - Run `npm install`.
   - Deploy functions using the Firebase CLI: `firebase deploy --only functions`.

4. **Build and Run**:
   - Open the project in Android Studio.
   - Sync Gradle files.
   - Run the app on an emulator or physical device.

*Developed for the Mobile Applications course at UC3M.*
