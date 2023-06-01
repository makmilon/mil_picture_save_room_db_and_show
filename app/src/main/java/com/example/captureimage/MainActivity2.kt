package com.example.captureimage

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.captureimage.databinding.ActivityMain2Binding
import com.example.captureimage.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var appDatabase: AppDatabase
    private lateinit var imageDao: ImageDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = AppDatabase.getInstance(applicationContext)
        imageDao = appDatabase.imageDao()

        // Fetch images from the database
        lifecycleScope.launch {
            val images = imageDao.getAllImages()
            if (images.isNotEmpty()) {
                val imageEntity = images.first()
                val imageData = imageEntity.imageData
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                binding.imageView2.setImageBitmap(bitmap)
                binding.textView.text=bitmap.toString()
            }
        }

    }
}