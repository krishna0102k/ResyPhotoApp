# Resy Photo App

Small Android app built for the Resy take-home assignment.

The app loads photo metadata from Lorem Picsum, displays the filenames in a list, and opens a detail screen showing the selected image and its author.

## Implementation

* Kotlin + XML Views
* RecyclerView for the photo list
* `HttpURLConnection` for API and image requests
* `org.json` for parsing the list response
* `BitmapFactory` for image decoding
* ViewBinding for view access

No third-party libraries are used.

The project is intentionally kept simple since the assignment only requires two screens.

## Image behavior

Images are requested using the dimensions returned by the API while preserving the original aspect ratio.

To avoid downloading unnecessarily large images, the requested width is based on the available screen width and the height is calculated from the original dimensions.

Landscape images are vertically centered with the author shown underneath.

Portrait images are displayed from the top with the author directly below.

## Testing

Unit tests cover:

* Portrait vs. landscape image detection
* Picsum image URL generation

Run the tests with:

```bash
./gradlew testDebugUnitTest
```

Build the project with:

```bash
./gradlew assembleDebug
```

## Requirements

* Android phone
* Portrait orientation
* Internet connection
