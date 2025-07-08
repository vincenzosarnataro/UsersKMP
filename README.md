# 📱 Users App

**Users App** is a Kotlin Multiplatform (KMP) project that showcases a simple user listing and detail feature, following **Clean Architecture** principles. The app shares business logic across platforms while delivering a modern, responsive UI using **Compose Multiplatform**.

## 🏗️ Project Structure

The app is built using **Clean Architecture**, with clear separation of concerns:

- **Domain Layer**: Business logic and models (shared).
- **Data Layer**: API communication and repositories (shared).
- **Presentation Layer**: UI, ViewModels, and Navigation (shared).

## 🚀 Features

- ✅ Users List Screen
- ✅ User Detail Screen
- ✅ Modern UI with Compose Multiplatform
- ✅ Modular Clean Architecture structure
- ✅ Multiplatform networking and dependency injection

## 🛠️ Tech Stack

Here are the main technologies and libraries used:

| Tool/Library             | Description                                             |
|--------------------------|---------------------------------------------------------|
| **Kotlin Multiplatform**  | Share code across Android, iOS, Desktop, and more.      |
| **Compose Multiplatform** | Declarative UI for Android, Desktop, and more.          |
| **Ktor**                 | Asynchronous HTTP client for network requests.           |
| **Ktorfit**              | Retrofit-like HTTP client for Ktor (type-safe APIs).     |
| **Koin**                 | Dependency Injection framework for Kotlin Multiplatform. |
| **Navigation-Compose**   | Declarative navigation between screens in Compose.       |
| **Coil**                 | Image loading library for Compose. |

## 📦 Modules

- `:domain` — Models & Use Cases
- `:data` — Repositories & API Services
- `:presentation` — UI Screens, ViewModels, Navigation

## 📄 How to Run

1. Clone the repository.
2. Open the project in [IntelliJ IDEA](https://www.jetbrains.com/idea/) or Android Studio.
3. Run the app on supported platforms:
  - Android Emulator or Device.
  - iOS.
4. Ensure all dependencies are synced.

## ✅ Highlights
- Scalable, maintainable architecture.
- Clear separation between UI and business logic.
- Fully asynchronous network layer.


