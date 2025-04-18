# 🏋️ Приложение для индивидуальных тренировок

![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-blue.svg)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.4.0-green.svg)
![Material 3](https://img.shields.io/badge/Material%203-1.0.0-purple.svg)
![Firebase](https://img.shields.io/badge/Firebase-30.0.0-orange.svg)


> Современное мобильное приложение для планирования и отслеживания индивидуальных тренировок, разработанное с использованием передовых технологий Android.

## 📑 Содержание

- [Обзор](#-обзор)
- [Основные функции](#-основные-функции)
- [Технический стек](#-технический-стек)
- [Архитектура](#-архитектура)
- [Особенности интерфейса](#-особенности-интерфейса)
- [Экраны приложения](#-экраны-приложения)
- [Новые функции и улучшения](#-новые-функции-и-улучшения)
- [Установка и запуск](#-установка-и-запуск)
- [Требования](#-требования)
- [Структура проекта](#-структура-проекта)
- [Руководство пользователя](#-руководство-пользователя)
- [Вклад в проект](#-вклад-в-проект)
- [Контакты](#-контакты)

## 🔍 Обзор

Приложение для индивидуальных тренировок представляет собой комплексное решение для фитнес-энтузиастов, которое помогает планировать, отслеживать и анализировать тренировки. Благодаря интеграции с Firebase, приложение обеспечивает синхронизацию данных между устройствами и надежное хранение информации о прогрессе пользователя.

## 🚀 Основные функции

<table>
  <tr>
    <td>
      <h3>🔐 Аутентификация</h3>
      <ul>
        <li>Регистрация и вход</li>
        <li>Восстановление пароля</li>
        <li>Профили пользователей</li>
      </ul>
    </td>
    <td>
      <h3>📊 Отслеживание прогресса</h3>
      <ul>
        <li>Сохранение результатов</li>
        <li>Визуализация прогресса</li>
        <li>История тренировок</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td>
      <h3>📚 Каталог тренировок</h3>
      <ul>
        <li>Различные категории</li>
        <li>Детальные описания</li>
        <li>Видео-инструкции</li>
      </ul>
    </td>
    <td>
      <h3>🍎 Питание и вода</h3>
      <ul>
        <li>Учет калорий</li>
        <li>Анализ макроэлементов</li>
        <li>Отслеживание воды</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td>
      <h3>📈 Статистика</h3>
      <ul>
        <li>Графики и диаграммы</li>
        <li>Аналитика тренировок</li>
        <li>Система достижений</li>
      </ul>
    </td>
    <td>
      <h3>🌓 Темная/светлая тема</h3>
      <ul>
        <li>Переключение тем</li>
        <li>Сохранение предпочтений</li>
        <li>Адаптивный дизайн</li>
      </ul>
    </td>
  </tr>
</table>

## 💻 Технический стек

### Frontend
- **Kotlin** - основной язык программирования
- **Jetpack Compose** - современный UI toolkit
- **Material 3** - дизайн-система с поддержкой динамических цветов
- **Navigation Compose** - навигация между экранами
- **DataStore** - хранение пользовательских настроек

### Backend
- **Firebase Authentication** - управление пользователями
- **Firebase Realtime Database** - хранение данных о прогрессе
- **Firebase Storage** - хранение видео-контента

### Архитектура и инструменты
- **MVVM** - архитектурный паттерн
- **Coroutines** - асинхронное программирование
- **Flow** - реактивное программирование
- **Hilt** - внедрение зависимостей
- **ExoPlayer** - воспроизведение видео

## 🏗 Архитектура

Приложение построено на основе архитектуры MVVM (Model-View-ViewModel), что обеспечивает четкое разделение ответственности между компонентами:

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│     UI      │◄───┤  ViewModel  │◄───┤ Repository  │◄───┤ Data Source │
│  (Compose)  │    │             │    │             │    │  (Firebase) │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

- **UI (View)**: Jetpack Compose компоненты, отвечающие за отображение данных
- **ViewModel**: Управляет данными UI и обрабатывает бизнес-логику
- **Repository**: Абстрагирует источники данных и предоставляет чистый API
- **Data Source**: Firebase и локальное хранилище

## 🎨 Особенности интерфейса

- **Material 3** - современный дизайн с поддержкой темной и светлой темы
- **Черно-серо-зеленая цветовая схема** - стильный и современный дизайн
- **Анимации** - плавные переходы и анимации для улучшения UX
- **Адаптивный дизайн** - поддержка различных размеров экранов

## 📱 Экраны приложения

| Экран | Описание |
|-------|----------|
| **Splash Screen** | Экран загрузки с анимацией |
| **Авторизация** | Вход и регистрация пользователей |
| **Главный экран** | Категории тренировок и рекомендации |
| **Экран тренировки** | Детали и выполнение тренировки |
| **Профиль** | Информация о пользователе и настройки |
| **Статистика** | Графики и аналитика тренировок |
| **Питание и вода** | Отслеживание питания и потребления воды |

## 🆕 Новые функции и улучшения

### 1. **Обновленный дизайн Material 3**
- Полностью переработанный интерфейс с использованием Material 3
- Черно-серо-зеленая цветовая схема для современного и стильного вида
- Улучшенная читаемость текста и контрастность элементов
- Анимированные переходы и интерактивные элементы

### 2. **Статистика тренировок**
- Визуализация прогресса с помощью графиков и диаграмм
- Отслеживание ключевых показателей: количество тренировок, часы, калории, километры
- Круговая диаграмма типов тренировок для анализа предпочтений
- Система достижений для мотивации пользователя

### 3. **Отслеживание питания и воды**
- Учет потребления калорий с разбивкой по приемам пищи
- Анализ макроэлементов (белки, жиры, углеводы)
- Отслеживание потребления воды с визуальным индикатором
- Возможность добавления новых приемов пищи с детальной информацией
- Советы по питанию для оптимизации тренировочного процесса

### 4. **Система переключения темы**
- Возможность выбора между светлой и темной темой
- Сохранение выбранной темы с использованием DataStore
- Автоматическое применение темы при запуске приложения
- Стильный переключатель темы с анимацией

## 🔧 Установка и запуск

```bash
# Клонирование репозитория
git clone https://github.com/your-username/your-repo.git

# Переход в директорию проекта
cd your-repo

# Открытие проекта в Android Studio
# или
./gradlew build
```

## 📋 Требования

- Android 6.0 (API level 24) или выше
- Поддержка Jetpack Compose
- Минимум 2 ГБ оперативной памяти
- 100 МБ свободного места на устройстве

## 📂 Структура проекта

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/application/apps_for_individual_train/
│   │   │   ├── data/           # Слой данных (репозитории, модели)
│   │   │   ├── di/             # Внедрение зависимостей
│   │   │   ├── domain/         # Бизнес-логика и use cases
│   │   │   ├── screen/         # UI экраны
│   │   │   │   ├── auth/       # Экраны авторизации
│   │   │   │   ├── main/       # Главный экран
│   │   │   │   ├── nutrition/  # Экран питания
│   │   │   │   ├── profile/    # Экран профиля
│   │   │   │   ├── statistics/ # Экран статистики
│   │   │   │   └── workout/    # Экран тренировок
│   │   │   ├── ui/             # UI компоненты
│   │   │   │   └── theme/      # Темы и стили
│   │   │   └── util/           # Утилиты и расширения
│   │   └── res/                # Ресурсы (изображения, строки)
│   └── test/                   # Тесты
└── build.gradle                # Конфигурация сборки
```

### Ключевые модули:
1. **Profile Screen**:
   - Displays user details.
   - Navigation to "Edit Profile" and "Change Password".
   - Fetches and displays workout progress.
   - Theme switching functionality.

2. **Workout Screen**:
   - Fetches workout details.
   - Plays workout videos with ExoPlayer.
   - Tracks and saves progress in real-time.

3. **Statistics Screen**:
   - Displays workout statistics with charts and graphs.
   - Shows achievements and progress over time.

4. **Nutrition Screen**:
   - Tracks food intake and water consumption.
   - Displays macronutrient breakdown.
   - Allows adding new meals with detailed information.

### Важные файлы:
- **WorkoutScreen.kt**: Manages the workout screen UI and functionality.
- **ProfileScreen.kt**: Displays user profile and workout progress.
- **StatisticsScreen.kt**: Shows workout statistics and achievements.
- **NutritionScreen.kt**: Handles nutrition and water tracking.
- **ThemeSwitcher.kt**: Manages theme switching functionality.
- **Theme.kt**: Defines color schemes for light and dark themes.

## 📖 Руководство пользователя

### **Отслеживание прогресса тренировок**
1. Перейдите к тренировке.
2. Начните воспроизведение видео с помощью кнопки **Продолжить**.
3. Смотрите видео, и прогресс будет обновляться в реальном времени.
4. Приостановите или перезапустите тренировку с помощью предоставленных элементов управления.

### **Профиль пользователя**
1. Просмотрите информацию о своем профиле на экране профиля.
2. Редактируйте свой профиль или измените пароль.
3. Просматривайте свой прогресс для каждой тренировки.
4. Переключайте тему приложения между светлой и темной.

### **Статистика и питание**
1. Просматривайте статистику тренировок на экране статистики.
2. Отслеживайте потребление воды и питание на экране питания.
3. Добавляйте новые приемы пищи с подробной информацией.
4. Следите за потреблением макроэлементов и калорий.

## 🤝 Вклад в проект

Мы приветствуем вклад в развитие проекта! Если вы хотите внести свой вклад, пожалуйста, следуйте этим шагам:

1. Форкните репозиторий
2. Создайте ветку для вашей функции (`git checkout -b feature/amazing-feature`)
3. Зафиксируйте ваши изменения (`git commit -m 'Add some amazing feature'`)
4. Отправьте изменения в ваш форк (`git push origin feature/amazing-feature`)
5. Откройте Pull Request


<p align="center">
  Разработано с ❤️ CorryWilliams
</p>

