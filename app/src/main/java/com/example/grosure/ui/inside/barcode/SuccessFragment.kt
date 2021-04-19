package com.example.grosure.ui.inside.barcode

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.Item
import com.example.grosure.model.User
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_success.view.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*


class SuccessFragment : Fragment() {

    lateinit var code: String
    lateinit var imageView: ImageView
    var imageUrl = ""
    var isFile = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_success, container, false)

        code = requireArguments().get("code") as String

        v.fragment_success_text_view_title.text = "Product barcode: " + code.toString()

        v.fragment_success_button_back_to_scanner.setOnClickListener {
            findNavController().navigateUp()
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val model: GroSureViewModel by activityViewModels()


        var url = "https://barcode.monster/api/${code}"
        imageUrl =
            (ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + requireContext().getResources().getResourcePackageName(R.drawable.index)
                    + '/' + requireContext().getResources().getResourceTypeName(R.drawable.index)
                    + '/' + requireContext().getResources().getResourceEntryName(R.drawable.index))



        imageView = requireView().findViewById(R.id.productImage)

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

            val stringRequest =
                StringRequest(Request.Method.GET, url, object : Response.Listener<String?> {
                    override fun onResponse(response: String?) {
                        try {
                            Log.i("WTF", "WT2F2")
                            val jsonObject = JSONObject(response)
                            if (response != null) {
                                Log.i("responce", response)
                            }

                            val availableFields = mutableListOf<String>()

                            var keys = jsonObject.keys()

                            while (keys.hasNext()) {
                                availableFields += keys.next()
                            }


                            if ("image_url" in availableFields) {
                                if (jsonObject.getString("image_url") == "") {
                                    Picasso.get().load(R.drawable.index)
                                            .into(requireView().findViewById<ImageView>(R.id.productImage))
                                }
                                else {
                                    Picasso.get().load(jsonObject.getString("image_url").toString())
                                            .into(requireView().findViewById<ImageView>(R.id.productImage))

                                    imageUrl = jsonObject.getString("image_url")
                                }
                            } else {
                                requireView().findViewById<ImageView>(R.id.productImage)
                                    .setImageResource(R.drawable.index)
                            }

                            if ("company" in availableFields) {
                                requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).editText!!.setText(
                                    jsonObject.getString("company")
                                )


                                if (jsonObject.getString("company") == "") {
                                    requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).editText!!.setHint(
                                        "no manufacturer found"
                                    )
                                }

                            } else {
                                requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).editText!!.setHint(
                                    "no manufacturer found"
                                )
                            }

                            if ("description" in availableFields) {
                                requireView().findViewById<TextInputLayout>(R.id.editTextTextProductName).editText!!.setText(
                                    jsonObject.getString("description")
                                )

                                if (jsonObject.getString("description") == "") {
                                    requireView().findViewById<TextInputLayout>(R.id.editTextTextProductName).editText!!.setHint(
                                        "No Product name found"
                                    )
                                }

                            } else {
                                requireView().findViewById<TextInputLayout>(R.id.editTextTextProductName).editText!!.setHint(
                                    "No Product name found"
                                )
                            }

                            requireView().findViewById<ProgressBar>(R.id.progressBar2).visibility =
                                View.GONE

                        } catch (e: Exception) {
                            Log.i("WTF", "WTF12")
                        }
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {

                        Log.i("WTF", "WTF22")
                        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).visibility =
                            View.GONE
                        requireView().findViewById<TextInputLayout>(R.id.editTextTextProductName).visibility =
                            View.GONE
                        requireView().findViewById<TextView>(R.id.fragment_success_text_view_name)
                            .setText("Use manual addition for now")
                        requireView().findViewById<TextView>(R.id.fragment_success_text_view_company)
                            .setText("No Internet Connection found")
                        requireView().findViewById<ImageView>(R.id.productImage)
                            .setImageResource(R.drawable.index)
                        requireView().findViewById<ProgressBar>(R.id.progressBar2).visibility =
                            View.GONE

                    }
                })

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            stringRequest.retryPolicy = DefaultRetryPolicy(
                45000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            requestQueue.add(stringRequest)

            requireView().findViewById<Button>(R.id.fragment_success_button_add_product)
                .setOnClickListener {
                    try {
                        Log.i(
                            "a",
                            requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPrice).editText!!.toString()
                        )
                        model.currentUserItems.value!!.plusAssign(
                            Item(
                                requireView().findViewById<TextInputLayout>(
                                    R.id.editTextTextProductName
                                ).editText!!.text.toString(),
                                requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).editText!!.text.toString(),
                                requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPrice).editText!!.text.toString()
                                    .toDouble(),
                                imageUrl.toString(),
                                isFile,
                                model.currentUser.value!!
                            )
                        )
                        model.itemList.value!!.plusAssign(
                            Item(
                                requireView().findViewById<TextInputLayout>(
                                    R.id.editTextTextProductName
                                ).editText!!.text.toString(),
                                requireView().findViewById<TextInputLayout>(R.id.editTextTextProductCompany).editText!!.text.toString(),
                                requireView().findViewById<TextInputLayout>(R.id.editTextTextProductPrice).editText!!.text.toString()
                                    .toDouble(),
                                imageUrl.toString(),
                                isFile,
                                model.currentUser.value!!
                            )
                        )
                        findNavController().navigate(R.id.action_successFragment_to_nav_items)
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Make sure you filled out all the fields correctly.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


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