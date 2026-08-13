# Resy Photo App

## Overview
This is a small Android application created for the Resy take-home assignment.

The application:
* loads image metadata from Lorem Picsum
* displays filenames in a list
* opens a selected image in a detail screen
* displays the image author
* preserves the original image aspect ratio
* positions landscape and portrait content according to the assignment

## Requirements Implemented
* Photo list loaded from `https://picsum.photos/list`
* API response converted into Kotlin `Photo` objects
* Filename displayed for every list item
* Selecting a row opens the detail screen
* Selected image displayed on detail screen
* Author displayed directly below image
* Landscape image + author group vertically centered
* Portrait image + author group positioned at top
* Full-width image display
* Original aspect ratio preserved
* No cropping
* No distortion
* Back navigation
* Phone portrait orientation
* Required unit tests
* No third-party libraries

## Architecture
The solution intentionally uses a lightweight architecture appropriate for the size of the take-home.

```text
PhotoListActivity
        |
        v
PhotoRepository
        |
        v
PicsumApi
        |
        v
Lorem Picsum API
```

For detail image loading:
```text
PhotoDetailActivity
        |
        v
ImageUrlBuilder
        |
        v
ImageDownloader
        |
        v
Lorem Picsum Image API
```

Responsibilities are separated without introducing unnecessary framework layers.

## Project Structure
```text
com.example.resyphotoapp
├── data
│   ├── PhotoRepository.kt
│   └── model
│       └── Photo.kt
├── network
│   └── PicsumApi.kt
├── ui
│   ├── list
│   │   ├── PhotoListActivity.kt
│   │   └── PhotoAdapter.kt
│   └── detail
│       └── PhotoDetailActivity.kt
└── util
    ├── ImageDownloader.kt
    ├── ImageOrientation.kt
    └── ImageUrlBuilder.kt
```

## Networking
* `HttpURLConnection` is used because the assignment prohibits third-party libraries.
* `org.json` is used for JSON parsing.
* network calls execute on an `ExecutorService`.
* UI updates return to the main thread.

This is a deliberate solution for the assignment constraints.

## Image Loading
* images are downloaded with `HttpURLConnection`
* responses are decoded using `BitmapFactory`
* no Glide, Coil, Picasso, or other third-party image loader is used
* requested image width is based on the available screen width
* requested height is calculated from original API dimensions
* original aspect ratio is preserved
* `adjustViewBounds=true`
* `scaleType=fitCenter`
* no image cropping or distortion

## Portrait / Landscape Logic
```text
width > height -> LANDSCAPE
otherwise -> PORTRAIT
```

Landscape:
* image + author are treated as one block
* block is vertically centered

Portrait:
* block is positioned at the top

This logic is isolated in pure Kotlin so it can be unit tested.

## Testing
Local JVM unit tests cover:
1. Portrait vs landscape determination
2. Picsum image URL generation

Current verified result:
```text
4 tests
4 passed
0 failed
```

## Build
```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

If dependencies are already cached, offline verification can be run using:
```bash
./gradlew assembleDebug --offline
./gradlew testDebugUnitTest --offline
```

## Dependencies
`No third-party libraries are used.`

Standard AndroidX components are used for normal Android application infrastructure.

## Notes
The application targets phone portrait orientation as required.