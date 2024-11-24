# Fitness Trainer App

This is a **Fitness Trainer App** built using **Jetpack Compose**. The app allows users to browse and interact with workout videos, track their progress, and manage their profiles. It integrates Firebase for backend functionalities and ExoPlayer for video playback.

---

## Features

### 1. **Workout Progress Tracking**
   - Watch workout videos and track progress in real-time.
   - Automatically saves workout progress to Firebase.
   - Visual representation of progress with a circular progress indicator.

### 2. **Workout Video Player**
   - Plays videos hosted on Firebase Storage using ExoPlayer.
   - Pause, resume, and restart functionality for videos.
   - Updates progress dynamically while the video is playing.

### 3. **Profile Management**
   - View and edit user profile information.
   - Change password functionality.
   - Logout feature.

### 4. **Firebase Integration**
   - Firebase Authentication: Manage user accounts and authentication.
   - Firebase Realtime Database: Store and retrieve workout progress and user data.
   - Firebase Storage: Host and stream workout videos.

---


## Technologies Used

### Frontend:
- **Jetpack Compose**: Modern UI toolkit for building native Android UIs.
- **Material 3**: For building accessible and aesthetically pleasing designs.

### Backend:
- **Firebase**:
  - **Authentication**: User login and management.
  - **Realtime Database**: To store workout progress and user data.
  - **Storage**: To host workout video content.

### Video Playback:
- **ExoPlayer**: Advanced video playback library for Android.

---

## Project Structure

### Key Modules:
1. **Profile Screen**:
    - Displays user details.
    - Navigation to "Edit Profile" and "Change Password".
    - Fetches and displays workout progress.

2. **Workout Screen**:
    - Fetches workout details.
    - Plays workout videos with ExoPlayer.
    - Tracks and saves progress in real-time.

---

### Important Files:
- **WorkoutScreen.kt**: Manages the workout screen UI and functionality.
- **ProfileScreen.kt**: Displays user profile and workout progress.
- **WorkoutViewModel.kt**: Handles data operations for the workout screen.

---

### Firebase Setup:
- `fetchUserProgressWithNames`: Fetches user progress and workout names from Firebase.
- `fetchVideoUrl`: Retrieves video URLs from Firebase Storage.

---

## Usage Guide

### **Workout Video Progress Tracking**
1. Navigate to a workout.
2. Start the video playback using the **Resume** button.
3. Watch the video, and the progress will update in real-time.
4. Pause or restart the workout using the provided controls.

### **User Profile**
1. View your profile information on the profile screen.
2. Edit your profile or change your password.
3. View your progress for each workout.

---

