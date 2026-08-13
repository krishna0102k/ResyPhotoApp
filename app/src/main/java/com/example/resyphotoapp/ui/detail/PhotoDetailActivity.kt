package com.example.resyphotoapp.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.resyphotoapp.databinding.ActivityPhotoDetailBinding
import com.example.resyphotoapp.util.ImageDownloader
import com.example.resyphotoapp.util.ImageOrientation
import com.example.resyphotoapp.util.ImageUrlBuilder
import com.example.resyphotoapp.util.determineImageOrientation
import java.util.concurrent.Executors

class PhotoDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoDetailBinding
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val statusBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.setPadding(view.paddingLeft, statusBarInsets.top, view.paddingRight, view.paddingBottom)
            windowInsets
        }

        val photoId = intent.getIntExtra(EXTRA_PHOTO_ID, -1)
        val author = intent.getStringExtra(EXTRA_AUTHOR)
        val width = intent.getIntExtra(EXTRA_WIDTH, 0)
        val height = intent.getIntExtra(EXTRA_HEIGHT, 0)

        if (photoId < 0 || author == null || width <= 0 || height <= 0) {
            Log.e("PhotoDetailActivity", "Invalid Intent extras provided")
            finish()
            return
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.authorText.text = author

        configureContentPosition(width, height)

        binding.availableContentArea.post {
            val availableWidth = binding.availableContentArea.width
            if (availableWidth > 0) {
                loadImage(
                    photoId = photoId,
                    originalWidth = width,
                    originalHeight = height,
                    availableWidth = availableWidth
                )
            }
        }
    }

    private fun configureContentPosition(width: Int, height: Int) {
        val layoutParams = binding.contentContainer.layoutParams as FrameLayout.LayoutParams

        when (determineImageOrientation(width, height)) {
            ImageOrientation.LANDSCAPE -> {
                layoutParams.gravity = Gravity.CENTER_VERTICAL
            }
            ImageOrientation.PORTRAIT -> {
                layoutParams.gravity = Gravity.TOP
            }
        }
        
        binding.contentContainer.layoutParams = layoutParams
    }

    private fun calculateRequestedHeight(
        requestedWidth: Int,
        originalWidth: Int,
        originalHeight: Int
    ): Int {
        if (originalWidth == 0) return requestedWidth
        return (requestedWidth.toDouble() * originalHeight.toDouble() / originalWidth.toDouble())
            .toInt()
            .coerceAtLeast(1)
    }

    private fun loadImage(
        photoId: Int,
        originalWidth: Int,
        originalHeight: Int,
        availableWidth: Int
    ) {
        binding.imageProgressBar.visibility = View.VISIBLE
        binding.photoImage.visibility = View.INVISIBLE
        binding.imageErrorText.visibility = View.GONE

        val requestedWidth = availableWidth
        val requestedHeight = calculateRequestedHeight(
            requestedWidth = requestedWidth,
            originalWidth = originalWidth,
            originalHeight = originalHeight
        )

        val imageUrl = ImageUrlBuilder.build(
            id = photoId,
            width = requestedWidth,
            height = requestedHeight
        )

        executor.execute {
            try {
                val bitmap = ImageDownloader.download(imageUrl)

                runOnUiThread {
                    if (isDestroyed || isFinishing) {
                        bitmap.recycle()
                        return@runOnUiThread
                    }

                    binding.imageProgressBar.visibility = View.GONE
                    binding.imageErrorText.visibility = View.GONE
                    binding.photoImage.visibility = View.VISIBLE
                    binding.photoImage.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                Log.e("PhotoDetailActivity", "Failed to load image", e)

                runOnUiThread {
                    if (isDestroyed || isFinishing) {
                        return@runOnUiThread
                    }

                    binding.imageProgressBar.visibility = View.GONE
                    binding.photoImage.visibility = View.INVISIBLE
                    binding.imageErrorText.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_PHOTO_ID = "PHOTO_ID"
        const val EXTRA_AUTHOR = "AUTHOR"
        const val EXTRA_WIDTH = "WIDTH"
        const val EXTRA_HEIGHT = "HEIGHT"
    }
}
