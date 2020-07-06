package com.bigbang.myhiking.ui.view.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bigbang.myhiking.R
import com.bigbang.myhiking.model.HikePost
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.add_post_fragment_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddPostFragment : Fragment() {

    companion object {
        const val fileProvider = "com.bigbang.myhiking.provider"
    }

    private var imageBitmap: Bitmap? = null
    private var fileDirectoryPath: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.add_post_fragment_layout, container, false)

    val REQ_CODE = 777
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upload_post_button.setOnClickListener {
            //Upload to firebase
            //First upload the image and then get the image uri
            imageBitmap?.let { image ->

                val bAOS: ByteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bAOS)

                val imageByteArray = bAOS.toByteArray()
                val storageReference =
                    FirebaseStorage.getInstance().reference.child("UPLOADS/${FirebaseAuth.getInstance().currentUser?.uid}")
                val imageUploadTask =
                    storageReference.putBytes(imageByteArray)

                imageUploadTask.addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        storageReference
                            .downloadUrl
                            .addOnCompleteListener { imageUriTask ->
                                if (imageUriTask.isSuccessful) {

                                    val newHikePost = HikePost().also {
                                        it.postCaption = caption_edittext.text.toString().trim()
                                        it.imageUrl = imageUriTask.result?.toString() ?: "?!!?"
                                    }
                                    FirebaseDatabase.getInstance().reference.child("POSTS")
                                        .child(FirebaseAuth.getInstance().uid?:"").push()
                                        .setValue(newHikePost)
                                    caption_edittext.text.clear()
                                    activity?.supportFragmentManager?.popBackStack()

                                } else {
                                    Log.d("TAG_X", "${imageUriTask.exception?.localizedMessage}")
                                }
                            }

                    } else {
                        Log.d("TAG_X", "${task.exception?.localizedMessage}")
                    }
                }
            }
        }

        imageView.setOnClickListener {
            //Open camera, take a picture and use that picture
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            context?.let { ctx ->
                if (cameraIntent.resolveActivity(ctx.packageManager) != null) { //This device has a camera
                    try {
                        val file = temporaryImage()
                        file?.let { imageFile ->
                            context?.let { ctx ->
                                val imageUri = FileProvider.getUriForFile(
                                    ctx,
                                    fileProvider,
                                    imageFile
                                )
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                                startActivityForResult(cameraIntent, REQ_CODE)
                            }
                        }
                    } catch (exception: IOException) {
                        Log.d("TAG_X", "${exception.localizedMessage}")
                    }
                }
            }
        }
    }


    private fun temporaryImage(): File? {
        val dateStamp = SimpleDateFormat("yyyy_MM_dd_mm_ss", Locale.US).format(Date())
        val fileName = "$dateStamp${FirebaseAuth.getInstance().currentUser?.uid}"
        var fileDirectory: File? = null

        context?.let {
            fileDirectory = it.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        }
        fileDirectory?.let {
            val imageFile = File.createTempFile(
                fileName,
                ".jpg",
                it
            )
            fileDirectoryPath = imageFile.absolutePath ?: ""
            return imageFile
        }

        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE) {
            val bitmap = BitmapFactory.decodeFile(fileDirectoryPath)
            imageBitmap = bitmap
            context?.let {
                Glide.with(it)
                    .applyDefaultRequestOptions(RequestOptions().centerCrop())
                    .load(bitmap)
                    .into(imageView)
            }
        }
    }
}