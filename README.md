<div align="center">

<img src="app/src/main/res/drawable/logo_hunter.png" alt="Hunter Logo" height="200" />

# Hunter

### *Your Career Companion*

> A premium Android app for tracking job applications, managing interviews, and visualizing your career progress.

<p>
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Backend-Firebase-FFCA28?style=flat&logo=firebase&logoColor=black" alt="Firebase" />
  <img src="https://img.shields.io/badge/Design-Material%203-818CF8?style=flat&logo=materialdesign&logoColor=white" alt="Material Design" />
  <img src="https://img.shields.io/badge/IDE-Android%20Studio-3DDC84?style=flat&logo=android-studio&logoColor=white" alt="Android Studio" />
</p>

</div>

---

## About The Project

**Hunter** helps job seekers stay organized and motivated throughout their search. Track every application, prepare for interviews, manage your CVs, and visualize your progress — all in one app. Built with a dark premium theme, custom gradient animations, and a glassmorphism-inspired UI.

---

## Demo Videos

<div align="center">
  <table style="width:100%">
    <tr>
      <td align="center"><b>Splash + Login</b><br><br>
        <video src="https://github.com/ofekfanian/Hunter/raw/main/demos/splash_login.mp4" width="250" controls></video>
      </td>
      <td align="center"><b>Job List</b><br><br>
        <video src="https://github.com/ofekfanian/Hunter/raw/main/demos/job_list_scroll.mp4" width="250" controls></video>
      </td>
      <td align="center"><b>Add New Job</b><br><br>
        <video src="https://github.com/ofekfanian/Hunter/raw/main/demos/add_new_job.mp4" width="250" controls></video>
      </td>
    </tr>
    <tr>
      <td align="center"><b>Job Details</b><br><br>
        <video src="https://github.com/ofekfanian/Hunter/raw/main/demos/job_details.mp4" width="250" controls></video>
      </td>
      <td align="center"><b>Add Interview</b><br><br>
        <video src="https://github.com/ofekfanian/Hunter/raw/main/demos/add_interview.mp4" width="250" controls></video>
      </td>
      <td align="center"><b>CV Manager</b><br><br>
        <video src="https://github.com/ofekfanian/Hunter/raw/main/demos/cv_manager.mp4" width="250" controls></video>
      </td>
    </tr>
    <tr>
      <td align="center"><b>Community</b><br><br>
        <video src="https://github.com/ofekfanian/Hunter/raw/main/demos/community_new_question.mp4" width="250" controls></video>
      </td>
      <td align="center"><b>Statistics</b><br><br>
        <video src="https://github.com/ofekfanian/Hunter/raw/main/demos/statistics.mp4" width="250" controls></video>
      </td>
      <td align="center"><b>Profile</b><br><br>
        <video src="https://github.com/ofekfanian/Hunter/raw/main/demos/profile.mp4" width="250" controls></video>
      </td>
    </tr>
  </table>
</div>

---

## Features

* **Job Tracker:** Track applications with status management — Applied, Interview, Offer, Rejected.
* **Interview Manager:** Schedule interviews linked to specific applications with alarm reminders.
* **CV Manager:** Upload, store, and share multiple CV files via Firebase Cloud Storage.
* **Community Board:** Browse and share interview questions and career tips with other users.
* **Statistics Dashboard:** Interactive charts with Vico, success rates, insights and activity tracking.
* **Favorites:** Save and quickly access your most interesting opportunities.
* **Speed Apply:** Quickly log a new application with minimal input.
* **Profile:** Manage personal details, career goals, LinkedIn and GitHub links.

---

## Technical Implementation

### 1. Architecture & UI
* **AppCompatActivity + ViewBinding**: Clean, type-safe access to UI components across all screens.
* **Adapter Callback Pattern**: Interface-based click handling for all RecyclerView adapters.
* **XML Animations**: Custom animation files for smooth transitions and polished interactions.
* **Gradient Drawables**: Custom gradient XML files for the premium dark theme.

### 2. Backend & Data
* **Firebase Firestore**: Real-time cloud database for jobs, interviews, CVs, and community questions.
* **Firebase Auth**: Google Sign-In and Email authentication via FirebaseUI.
* **Firebase Cloud Storage**: Secure upload and retrieval of CV files.
* **Vico 2.0**: Per-bar colored charts for application statistics visualization.

---

## How to Run

1. Clone this repository.
2. Open the project in **Android Studio** (Hedgehog or newer).
3. Add your `google-services.json` to the `app/` directory.
4. Build and run (min API 26, JDK 17+).

---

<div align="center">
    <b>Created by Ofek Fanian</b>
</div>
