package com.example.resyphotoapp.ui.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resyphotoapp.R
import com.example.resyphotoapp.data.PhotoRepository
import com.example.resyphotoapp.data.model.Photo
import com.example.resyphotoapp.databinding.ActivityPhotoListBinding
import com.example.resyphotoapp.ui.detail.PhotoDetailActivity
import java.util.concurrent.Executors

class PhotoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoListBinding
    private val repository = PhotoRepository()
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var adapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val statusBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.setPadding(view.paddingLeft, statusBarInsets.top, view.paddingRight, view.paddingBottom)
            windowInsets
        }

        binding.toolbar.title = getString(R.string.app_name)

        adapter = PhotoAdapter { photo ->
            openPhoto(photo)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.photoRecyclerView.layoutManager = layoutManager
        binding.photoRecyclerView.adapter = adapter
        binding.photoRecyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        binding.photoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                binding.scrollToTopButton.visibility = if (
                    layoutManager.findFirstVisibleItemPosition() > 7
                ) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })

        binding.scrollToTopButton.setOnClickListener {
            binding.photoRecyclerView.scrollToPosition(0)
        }

        binding.retryButton.setOnClickListener {
            loadPhotos()
        }

        loadPhotos()
    }

    private fun loadPhotos() {
        binding.progressBar.visibility = View.VISIBLE
        binding.errorContainer.visibility = View.GONE
        binding.photoRecyclerView.visibility = View.GONE
        binding.scrollToTopButton.visibility = View.GONE

        executor.execute {
            try {
                val photos = repository.getPhotos()
                
                runOnUiThread {
                    if (isDestroyed || isFinishing) return@runOnUiThread
                    
                    binding.progressBar.visibility = View.GONE
                    
                    if (photos.isEmpty()) {
                        binding.errorText.text = getString(R.string.no_photos_available)
                        binding.errorContainer.visibility = View.VISIBLE
                        binding.retryButton.visibility = View.GONE
                    } else {
                        binding.photoRecyclerView.visibility = View.VISIBLE
                        adapter.submitList(photos)
                    }
                }
            } catch (e: Exception) {
                Log.e("PhotoListActivity", "Failed to load photos", e)
                
                runOnUiThread {
                    if (isDestroyed || isFinishing) return@runOnUiThread
                    
                    binding.progressBar.visibility = View.GONE
                    binding.photoRecyclerView.visibility = View.GONE
                    
                    binding.errorText.text = getString(R.string.unable_to_load_photos)
                    binding.errorContainer.visibility = View.VISIBLE
                    binding.retryButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun openPhoto(photo: Photo) {
        val intent = Intent(
            this,
            PhotoDetailActivity::class.java
        ).apply {
            putExtra(PhotoDetailActivity.EXTRA_PHOTO_ID, photo.id)
            putExtra(PhotoDetailActivity.EXTRA_AUTHOR, photo.author)
            putExtra(PhotoDetailActivity.EXTRA_WIDTH, photo.width)
            putExtra(PhotoDetailActivity.EXTRA_HEIGHT, photo.height)
        }

        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdownNow()
    }
}
