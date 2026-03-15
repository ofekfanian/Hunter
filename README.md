<p align="center">
  <img src="app/src/main/res/drawable/logo_hunter.png" width="120" alt="Hunter Logo"/>
</p>

<h1 align="center">Hunter</h1>

<p align="center">
  <b>Your Career Companion</b><br/>
  A sleek Android app to track job applications, interviews, and career progress.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-blue?logo=android" alt="Platform"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Backend-Firebase-orange?logo=firebase" alt="Firebase"/>
  <img src="https://img.shields.io/badge/Min%20SDK-26-green" alt="Min SDK"/>
  <img src="https://img.shields.io/badge/Target%20SDK-35-green" alt="Target SDK"/>
</p>

---

## About

**Hunter** helps job seekers stay organized and motivated throughout their job search. Track every application, prepare for interviews, manage CVs, and visualize your progress — all in one beautiful, dark-themed app.

## Features

- **Job Tracker** — Add, edit, and manage job applications with status tracking (Applied, Interview, Offer, Rejected)
- **Interview Manager** — Schedule and track interviews linked to specific job applications
- **CV Manager** — Upload, store, and share multiple CV files via Firebase Storage
- **Community Q&A** — Browse and post interview questions and tips shared by the community
- **Statistics Dashboard** — Visualize your job search with interactive charts, success rate, and insights
- **Favorites** — Save and quickly access your most interesting job opportunities
- **Speed Apply** — Quickly log a new application with minimal input
- **Profile** — Manage your personal details, career goals, and linked accounts

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Android Views + ViewBinding |
| Backend | Firebase Firestore |
| Auth | Firebase Authentication (Google + Email) |
| Storage | Firebase Cloud Storage |
| Charts | Vico 2.0 |
| Design | Material Design 3, custom gradients & animations |

## Architecture

- Pure **Activity-based** architecture with `AppCompatActivity` + ViewBinding
- Direct **Firebase Firestore** calls (no repository layer — simple and lean)
- **Adapter callback pattern** with interfaces for RecyclerView interactions
- **Constants object** for Firestore collection names and Intent extras
- XML-based animations and gradient drawables for a polished UI

## Screenshots

> Coming soon

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17+
- A Firebase project with Firestore, Authentication, and Storage enabled

### Setup

1. Clone the repository
   ```bash
   git clone https://github.com/YOUR_USERNAME/Hunter.git
   ```

2. Add your `google-services.json` to the `app/` directory

3. Build and run on an emulator or device (min API 26)

## Project Structure

```
app/src/main/java/com/ofek/hunter/
├── activities/       # All screens (AppCompatActivity + ViewBinding)
├── adapters/         # RecyclerView adapters with callback interfaces
├── fragments/        # List fragments (Jobs, Interviews, Community)
├── models/           # Data classes (JobApplication, Interview, CVFile, etc.)
└── utilities/        # Constants, AnimationHelper, NavigationHelper, etc.
```

## License

This project is for educational purposes.

---

<p align="center">
  Built with Kotlin & Firebase
</p>
