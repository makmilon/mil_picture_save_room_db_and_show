package com.example.captureimage

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.captureimage.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var imageDao: ImageDao

    private lateinit var imageUri: Uri

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
        if (captured) {
            val imageData = readImageData(imageUri)

            if (imageData != null) {
                binding.imageView.setImageURI(imageUri)
                val imageEntity = ImageEntity(imageData = imageData)
                lifecycleScope.launch {
                    imageDao.insertImage(imageEntity)
                    Toast.makeText(this@MainActivity, "Image saved to database", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@MainActivity, "Failed to read image data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@MainActivity, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = AppDatabase.getInstance(applicationContext)
        imageDao = appDatabase.imageDao()

        imageUri = createImageUri()

        binding.imageView.setOnClickListener {
            contract.launch(imageUri)
        }

        binding.button2.setOnClickListener {
            startActivity(Intent(this@MainActivity, MainActivity2::class.java))
        }
    }

    private fun createImageUri(): Uri {
        val imageFile = File(applicationContext.filesDir, "create_photo.png")
        return FileProvider.getUriForFile(
            applicationContext,
            "com.example.captureimage.fileProvider",
            imageFile
        )
    }

    private fun readImageData(imageUri: Uri): ByteArray? {
        return try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            bytes
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
