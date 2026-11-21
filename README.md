# CineScope

A native Android app for browsing movies from [The Movie Database (TMDB)](https://www.themoviedb.org/).
Built with Kotlin and Jetpack Compose using Clean Architecture + MVVM.

## Features

| # | User story | Where |
|---|------------|-------|
| 1 | List of official movie genres | `GenreScreen` — `GET /genre/movie/list` |
| 2 | Discover movies by genre | `MovieListScreen` — `GET /discover/movie?with_genres=` |
| 3 | Primary information about a movie | `MovieDetailScreen` — `GET /movie/{id}` |
| 4 | User reviews for a movie | `ReviewScreen` — `GET /movie/{id}/reviews` |
| 5 | YouTube trailer of the movie | Embedded player on `MovieDetailScreen` — `GET /movie/{id}/videos` |
| 6 | Endless scrolling on movies and reviews | Paging 3 (`TmdbPagingSource`) |
| 7 | Positive and negative cases | See [Handled cases](#handled-cases) and the unit tests |

## Tech stack

- **Kotlin 2.4**, **Jetpack Compose** (Material 3), **Navigation Compose** with type-safe routes
- **Paging 3** for endless scrolling
- **Koin** for dependency injection
- **Retrofit 3 + OkHttp 5 + kotlinx.serialization** for networking
- **Coil 3** for images
- **android-youtube-player** for the embedded trailer
- **JUnit 4, MockK, Turbine, kotlinx-coroutines-test, paging-testing** for unit tests
- **ktlint** and **Android lint**, run on CI via GitHub Actions

## Architecture

```
app  (presentation)                      core (domain + data)
─────────────────────────────            ─────────────────────────────────────────
Compose screens                          domain/  models, IMovieRepository,
   │ observe StateFlow / PagingData               MovieUseCase ← MovieInteractor
ViewModels ───────── MovieUseCase ─────▶ data/    MovieRepository
                                                     ├─ ApiService (Retrofit)
Navigation (type-safe routes)                        ├─ TmdbPagingSource
Koin modules                                         └─ ErrorMapper → AppError
```

- `core` has no Android UI dependencies. It exposes `Resource<T>` for one-shot requests and
  `Flow<PagingData<T>>` for paged lists.
- Every failure is mapped to a typed `AppError`: `NoInternet`, `Unauthorized`, `NotFound`, `Server` or
  `Unknown`. The UI turns each one into a user-friendly message with a retry action.
- Each screen renders an explicit state: loading, content, empty or error.

## Getting started

1. Create a TMDB account and copy the **API Read Access Token** (v4 auth) from
   <https://www.themoviedb.org/settings/api>.
2. Add it to `local.properties` in the project root. This file is git-ignored.

   ```properties
   tmdb.api.token=YOUR_TMDB_READ_ACCESS_TOKEN
   ```

   As an alternative, export `TMDB_API_TOKEN` as an environment variable. CI uses this.
3. Open the project in a recent Android Studio, or build from the command line with JDK 21:

   ```bash
   ./gradlew :app:installDebug
   ```

Requirements: JDK 21, Android SDK with platform `android-37.1`, min SDK 26.

## Commands

```bash
./gradlew testDebugUnitTest   # unit tests (core + app)
./gradlew ktlintCheck         # code style
./gradlew :app:lintDebug      # Android lint
./gradlew :app:assembleDebug  # build APK
```

## Handled cases

**Positive**
- Genres, movies, details, trailer and reviews load and display correctly.
- The next page loads automatically as the user scrolls movies or reviews.
- When a movie has several videos, the official YouTube trailer is chosen first, then a teaser, then any
  other YouTube clip.

**Negative**
- No internet connection: full-screen error with a retry button. If a later page fails, the list keeps its
  items and shows an inline retry footer.
- Missing or invalid TMDB token (HTTP 401): explains that the token must be added to `local.properties`.
- Unknown movie (HTTP 404) and server errors (5xx) each get their own message.
- Empty results: a genre with no movies or a movie with no reviews shows an empty state.
- A movie without a trailer, or a failed trailer request, still shows the rest of the detail screen.
- Missing posters, backdrops, avatars, runtimes and taglines fall back gracefully.
- TMDB returns Gravatar avatar URLs prefixed with `/`. These are normalized.
- Review text arrives with Markdown and HTML (`**bold**`, links, `<br>`, `&amp;`). It is cleaned to plain text.
- TMDB rejects pages above 500, so pagination stops there even when `total_pages` reports more.
- Double taps during navigation transitions are ignored, so no screen is pushed twice.

## Tests

The suite has 68 unit tests:

- **core:** response parsing with null and unknown fields, DTO-to-domain mapping, trailer selection,
  review text cleanup, error mapping, paging keys and limits, repository success and error paths, and the
  use case.
- **app:** loading, success, empty, error and retry for each ViewModel, the paged ViewModels, the
  formatters, and the error-message mapping.

## Project structure

```
app/src/main/java/com/example/movieapp/
├── di/                     Koin modules (use case, view models)
└── presentation/
    ├── common/             UiState, shared state views, formatters, error messages
    ├── genre/              Genre list
    ├── movies/             Discover movies by genre (paged grid)
    ├── detail/             Movie detail + YouTube trailer
    ├── review/             User reviews (paged list)
    ├── navigation/         Type-safe routes and NavHost
    └── theme/              Material 3 theme
core/src/main/java/com/example/movieapp/core/
├── data/                   Repository, paging source, error mapper, Retrofit API and DTOs
├── domain/                 Models, repository contract, use case
├── di/                     Network and repository modules
└── utils/                  DTO → domain mapper
```
