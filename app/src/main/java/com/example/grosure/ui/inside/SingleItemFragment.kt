package com.example.grosure.ui.inside

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.Item
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SingleItemFragment : Fragment(){
    lateinit var code: String
    lateinit var imageView: ImageView
    var imageUrl = ""
    var isFile = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: GroSureViewModel by activityViewModels()

        val itemName = requireArguments().getString("name")
        var item : Item? = null
        for (ite in model.currentUserItems.value!!){
            if (ite.itemName == itemName){
                item = ite
            }
        }
        isFile = item!!.isFile
        imageUrl = item.itemPicture

        imageView = requireView().findViewById<ImageView>(R.id.productImageSingle)

        if (item.isFile) {
            Picasso.get().load(File(item.itemPicture)).into(imageView)
        }
        else{
            Picasso.get().load(item.itemPicture).into(imageView)
        }

        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompanyEdit).editText!!.setText(item.brand)
        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductNameEdit).editText!!.setText(item.itemName)
        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPriceEdit).editText!!.setText(String.format("%.2f",item.itemPrice))

        requireView().findViewById<Button>(R.id.fragment_success_button_add_productEDIT).setOnClickListener(){
            item.isFile = isFile
            item.itemPicture = imageUrl
            item.brand = requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompanyEdit).editText!!.text.toString()
            item.itemName = requireView().findViewById<TextInputLayout>(R.id.editTextTextProductNameEdit).editText!!.text.toString()
            item.itemPrice = requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPriceEdit).editText!!.text.toString().toDouble()
            findNavController().navigate(R.id.action_singleItemFragment_to_nav_items)
        }


        imageView.setOnClickListener() {

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Change photo")
            builder.setItems(
                arrayOf(
                    "Capture photo using camera",
                    "Select from gallery"
                )
            ) { _, item ->
                when (item) {
                    0 -> {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.CAMERA),
                                101
                            )
                        }
                        askFilePermissions()
                        createImageFile()
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                            // Ensure that there's a camera activity to handle the intent
                            takePictureIntent.resolveActivity(requireContext().packageManager)
                                ?.also {
                                    // Create the File where the photo should go
                                    Log.i("100", "72")
                                }
                            val photoFile: File = File(imageUrl)
                            Log.i("4", photoFile.absolutePath)
//                // Continue only if the File was successfully created
                            photoFile.also {
                                val photoURI: Uri = FileProvider.getUriForFile(
                                    requireContext(),
                                    "com.example.grosure.fileprovider",
                                    it
                                )
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            }
                            Log.i("23", "100")
                            startActivityForResult(takePictureIntent, 0)
                        }
                    }
                    1 -> {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.CAMERA),
                                101
                            )
                        }
                        askFilePermissions()
                        val selectPhoto =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(selectPhoto, 1)
                    }
                }
            }
            builder.show()
        }
    }


    fun askFilePermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 105)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 106)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }
            else{
                Toast.makeText(requireContext(), "Camera is needed to submit image", Toast.LENGTH_LONG).show()
            }
        }
    }




    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        Log.i("12", "100")
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //        val storageDir: File =  applicationContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        Log.i("53", storageDir.absolutePath)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            imageUrl = absolutePath.toUri().toString()
            Log.i("2", imageUrl)
        }
    }

    private fun getFileExt(content: Uri): String{
        var mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(requireActivity().contentResolver.getType(content))!!
    }

    fun getPath(context: Context, uri: Uri): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = context.getContentResolver().query(uri, proj, null, null, null)!!
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(column_index)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0){
            if (resultCode == Activity.RESULT_OK){
                data!!
                var f = File(imageUrl)
                Picasso.get().load(f).into(imageView)

                //                val myBitmap = BitmapFactory.decodeFile(currentPhotoPath)
                //                        imageView.setImageBitmap(myBitmap)
                val model: GroSureViewModel by activityViewModels()
//                var temp = model.currentUser.value!!
//                var temp2 = User(model.currentUser.value!!.username, model.currentUser.value!!.password, currentPhotoPath, model.currentUser.value!!.notifs)
//                model.userList.value!!.remove(temp)
//                model.userList.value!!.add(temp2)
//                model.currentUser.value = temp2
//                writeUsers()
                isFile = true
                var mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.setData(imageUrl.toUri())
                requireActivity().sendBroadcast(mediaScanIntent)
            }
        }
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                var uriContentUri =  data!!.data!!
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val fileName = storageDir.path + "JPEG_" + timeStamp + "." + getFileExt(uriContentUri)
                imageView.setImageURI(uriContentUri)
                isFile = true

                imageUrl  = this!!.getPath(requireContext(), uriContentUri)
                //                val myBitmap = BitmapFactory.decodeFile(currentPhotoPath)
                //                        imageView.setImageBitmap(myBitmap)
                val model: GroSureViewModel by activityViewModels()
//                var temp = model.currentUser.value!!
//                var temp2 = User(model.currentUser.value!!.username, model.currentUser.value!!.password, currentPhotoPath, model.currentUser.value!!.notifs)
//                model.userList.value!!.remove(temp)
//                model.userList.value!!.add(temp2)
//                model.currentUser.value = temp2
//                writeUsers()
            }

        }
    }
}