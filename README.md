# Приложение для индивидуальных тренировок

Мобильное приложение для планирования и отслеживания индивидуальных тренировок с использованием современных технологий Android.

## Основные функции

- **Аутентификация пользователей**: регистрация, вход, восстановление пароля
- **Каталог тренировок**: различные категории тренировок (кардио, силовые, йога и др.)
- **Отслеживание прогресса**: сохранение результатов тренировок и отображение прогресса
- **Профиль пользователя**: персональная информация, настройки, история тренировок
- **Статистика тренировок**: графики и аналитика по тренировкам
- **Отслеживание питания и воды**: учет потребления калорий, макроэлементов и воды
- **Темная/светлая тема**: возможность переключения между темами с сохранением выбора

## Технологии

- **Kotlin**: основной язык программирования
- **Jetpack Compose**: современный инструментарий для создания пользовательского интерфейса
- **Material 3**: дизайн-система с поддержкой динамических цветов и тем
- **Firebase**: аутентификация, база данных, хранилище
- **MVVM**: архитектурный паттерн для разделения логики и представления
- **Coroutines**: асинхронное программирование
- **DataStore**: хранение настроек пользователя
- **Navigation Compose**: навигация между экранами

## Особенности интерфейса

- **Material 3**: современный дизайн с поддержкой темной и светлой темы
- **Черно-серо-зеленая цветовая схема**: стильный и современный дизайн
- **Анимации**: плавные переходы и анимации для улучшения пользовательского опыта
- **Адаптивный дизайн**: поддержка различных размеров экранов

## Экраны приложения

- **Splash Screen**: экран загрузки с анимацией
- **Авторизация**: вход и регистрация
- **Главный экран**: категории тренировок
- **Экран тренировки**: детали и выполнение тренировки
- **Профиль**: информация о пользователе и настройки
- **Статистика**: графики и аналитика тренировок
- **Питание и вода**: отслеживание питания, калорий и потребления воды

## Установка и запуск

1. Клонировать репозиторий
2. Открыть проект в Android Studio
3. Синхронизировать Gradle
4. Запустить на эмуляторе или устройстве

## Требования

- Android 6.0 (API level 24) или выше
- Поддержка Jetpack Compose

## Лицензия

MIT

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

