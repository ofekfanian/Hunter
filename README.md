<div align="center">
<br/>

<p align="center">
  <img src="https://raw.githubusercontent.com/ofekfanian/Hunter/main/app/src/main/res/drawable/logo_hunter.png" alt="Hunter Logo" height="140" />
</p>

<h1 align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Orbitron&weight=900&size=42&duration=3000&pause=1000&color=818CF8&center=true&vCenter=true&width=440&height=70&lines=H+U+N+T+E+R" alt="HUNTER" />
</h1>

<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Rajdhani&weight=600&size=20&duration=4000&pause=2000&color=A78BFA&center=true&vCenter=true&width=460&lines=Stop+Searching.+Start+Hunting.;Your+Career+Pipeline%2C+Simplified." alt="Tagline" />
</p>

<p align="center">
  <sub>A premium, mobile-first career CRM built for Israeli juniors.<br/>Track applications, manage CV versions, and learn from the community.</sub>
</p>

<br/>

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black" />
  <img src="https://img.shields.io/badge/Material_3-818CF8?style=flat-square&logo=materialdesign&logoColor=white" />
  <img src="https://img.shields.io/badge/Min_SDK-26-6366F1?style=flat-square" />
  <img src="https://img.shields.io/badge/Target_SDK-35-6366F1?style=flat-square" />
</p>

<br/>

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="100%" />

</div>

<br/>

## The Problem

Most career trackers are **English-only**, **paid**, and **built for desktop**. They require filling in dozens of fields per application — nobody has time for that during an active job hunt.

**Hunter** was designed for one audience: **Israeli students and junior developers** entering the tech workforce.

<br/>

| Problem | Hunter's Answer |
|:---|:---|
| English-only platforms | Native support for the local tech ecosystem |
| Expensive premium tiers | **100% free** — built for those starting out |
| Too many fields to fill | **Speed Apply** — log a job in under 2 seconds |
| Lost track of which CV was sent | **CV Vault** — version management per application |
| Going into interviews blind | **Intel Hub** — community-shared interview questions |

<br/>

---

<br/>

## Features

<br/>

### `01` Gateway — Secure Onboarding

<div align="center">
  <table><tr><td align="center">
    <video src="https://github.com/user-attachments/assets/fbbe0ef6-0e77-4e7b-947e-fa9582050cae" width="280"></video>
    <br/><sub><b>Firebase Auth · Google Sign-In · Guided Onboarding</b></sub>
  </td></tr></table>
</div>

<br/>

### `02` The Pipeline — Job Tracking

<div align="center">
  <table><tr>
    <td align="center" width="50%">
      <video src="https://github.com/user-attachments/assets/afdb8890-4a5a-4227-9468-9130fa1db2a9" width="260"></video>
      <br/><sub><b>Status Management · Search · Infinite Scroll</b></sub>
    </td>
    <td align="center" width="50%">
      <video src="https://github.com/user-attachments/assets/b24b2b5e-7d56-4cc4-a390-d09517ed923a" width="260"></video>
      <br/><sub><b>Speed Apply · Zero-Friction Logging</b></sub>
    </td>
  </tr></table>
</div>

<br/>

### `03` CV Vault — Version Management

<div align="center">
  <table><tr><td align="center">
    <video src="https://github.com/user-attachments/assets/2c80813e-a6e2-4c9f-a50d-1ceed63d61cb" width="280"></video>
    <br/><sub><b>Upload · Tag · Map CV versions to applications</b></sub>
    <br/><br/>
    <sub>Never ask yourself <i>"which resume did I send them?"</i> again.<br/>Hunter maps each CV file (<code>Backend</code>, <code>Frontend</code>, <code>General</code>) to its job application.</sub>
  </td></tr></table>
</div>

<br/>

### `04` Intel Hub — Interview Intelligence

<div align="center">
  <table><tr><td align="center">
    <video src="https://github.com/user-attachments/assets/6f17fc6a-8b3f-4c77-b95d-bc51e842cbf4" width="280"></video>
    <br/><sub><b>Community-driven interview prep for Israeli tech companies</b></sub>
    <br/><br/>
    <sub>Interviewing at Wix? See what others were asked.<br/>Done interviewing? Share your questions to help the next person.</sub>
  </td></tr></table>
</div>

<br/>

### `05` Analytics — Progress Dashboard

<div align="center">
  <table><tr><td align="center">
    <video src="https://github.com/user-attachments/assets/2dc3c268-a651-45f5-8c3a-dd929108de4a" width="280"></video>
    <br/><sub><b>Vico Charts · Application funnel · Status breakdown</b></sub>
  </td></tr></table>
</div>

<br/>

---

<br/>

## Architecture

```
┌──────────────────────────────────────────────────────────┐
│                   HUNTER — TECH STACK                    │
├─────────────────┬─────────────────┬──────────────────────┤
│  PRESENTATION   │   DATA LAYER    │      SERVICES        │
├─────────────────┼─────────────────┼──────────────────────┤
│  Kotlin         │  Firebase Auth  │  AlarmManager        │
│  ViewBinding    │  Firestore      │  Intent Share API    │
│  Material 3     │  Cloud Storage  │  Broadcast Receivers │
│  Vico Charts    │  Real-time Sync │  Deep Linking        │
└─────────────────┴─────────────────┴──────────────────────┘
```

<br/>

### Data Model

```yaml
Firestore:
  users/
    → { profile, settings, stats }
  job_applications/
    → { userId, title, company, status, cvVersionId, timestamp }
  interviews/
    → { userId, jobId, date, reminderEnabled, location, type }
  interview_questions/
    → { companyName, question, category, anonymous, votes }

Cloud Storage:
  cv_files/{userId}/
    → backend_v2.pdf
    → frontend_v1.pdf
    → general.pdf
```

<br/>

### Project Structure

```
com.ofek.hunter/
├── activities/       # 20 screens (Splash, Main, Jobs, Interviews, CV, Community...)
├── adapters/         # 6 RecyclerView adapters
├── fragments/        # 3 fragments (Jobs, Interviews, Community)
├── models/           # JobApplication, Interview, CVFile, InterviewQuestion
├── interfaces/       # Callback contracts between adapters and activities
├── receivers/        # Interview reminder broadcast receiver
├── utilities/        # Animation, DateTime, Navigation, Validation helpers
└── App.kt            # Application entry point & notification channels
```

<br/>

---

<br/>

<div align="center">

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="100%" />

<br/><br/>

<img src="https://readme-typing-svg.demolab.com?font=Orbitron&weight=700&size=20&duration=3000&pause=1000&color=818CF8&center=true&vCenter=true&width=500&height=40&lines=Designed+%26+Developed+by+Ofek+Fanian" alt="Author" />

<br/>

<sub>Afeka College of Engineering · UI Development — Final Project</sub>

<br/><br/>

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/ofek-fanian/)
&nbsp;
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/ofekfanian)

<br/>

</div>
