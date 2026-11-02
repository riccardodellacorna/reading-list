# Reading List

An Android app to search books, save them to a local library and track
their reading status (to read / reading / read).

Built as a personal project to practice modern Android development,
with a strong focus on **automated testing** and **dependency injection**.

## Features

- Search books through the [Open Library API](https://openlibrary.org/developers/api)
- Save books to a local library (offline-first)
- Track reading status
- Book detail screen

> Work in progress: features are added step by step.

## Tech stack

| Area | Choice |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, unidirectional data flow, `StateFlow` |
| Dependency injection | Koin |
| Networking | Retrofit, OkHttp, kotlinx.serialization |
| Local database | Room (with tested migrations) |
| Async | Kotlin Coroutines, Flow |
| Navigation | Navigation Compose |

## Testing

JUnit, MockK, Turbine, kotlinx-coroutines-test, MockWebServer,
Room in-memory, Compose UI tests.

## Getting started

1. Clone the repository
   ```bash
   git clone https://github.com/riccardodellacorna/reading-list.git
   ```
2. Open it with Android Studio (latest stable)
3. Run the `app` configuration on an emulator or device (API 26+)

To run the unit tests:

```bash
./gradlew test
```

## Roadmap

- [x] Repository setup
- [ ] Project structure, Material 3 theme, Koin
- [ ] Data layer (API + Room) with tests
- [ ] ViewModels with tests
- [ ] Compose UI
- [ ] Room migration test, Koin graph verification, coverage report
- [ ] CI with GitHub Actions

## License

Distributed under the MIT License. See [LICENSE](LICENSE) for details.