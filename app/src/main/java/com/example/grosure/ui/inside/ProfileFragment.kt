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
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.viewModels
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.grosure.GroSureViewModel
import com.example.grosure.R
import com.example.grosure.model.Item
import com.example.grosure.model.Trip
import com.example.grosure.model.User
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


class ProfileFragment : Fragment(){
    private lateinit var imageView: ImageView
    lateinit var currentPhotoPath: String
    private lateinit var navController: NavController


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val model: GroSureViewModel by activityViewModels()

//        currentPhotoPath = model.getUri()

    val a = inflater.inflate(R.layout.fragment_profile, container, false)
        return a
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val model: GroSureViewModel by activityViewModels()

        imageView = requireView().findViewById(R.id.profileImageView)
//        currentPhotoPath = model.getUri()
        if (currentPhotoPath != ""){
            imageView.setImageURI(currentPhotoPath.toUri()
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()


        val model: GroSureViewModel by activityViewModels()

        requireView().findViewById<TextView>(R.id.userIDTextView).text = model.currentUser.value!!.username


        imageView = requireView().findViewById(R.id.profileImageView)
        currentPhotoPath = ""
        if (currentPhotoPath != ""){
            imageView.setImageURI(currentPhotoPath.toUri()
            )
        }

        if (model.currentUser.value!!.profilePicture != "nil"){
            Picasso.get().load(File(model.currentUser.value!!.profilePicture)).into(imageView)

        }
        Log.i("run", "run")
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
                                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 101)
                                }
                                askFilePermissions()
                                createImageFile()
                                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {takePictureIntent ->
                                    // Ensure that there's a camera activity to handle the intent
                                    takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                                        // Create the File where the photo should go
                                        Log.i("100", "72")
                                    }
                                    val photoFile: File = File(currentPhotoPath)
                                    Log.i("4",photoFile.absolutePath)
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
                                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 101)
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

//            model.setUri(currentPhotoPath)









        requireView().findViewById<Button>(R.id.infoBtn2).setOnClickListener{
//            navController.navigate(R.id.action_profileFragment_to_infoFragment2)
        }

        requireView().findViewById<Button>(R.id.changeUsernameBtn).setOnClickListener {onChangeUsername(view) }
        requireView().findViewById<Button>(R.id.changePasswordBtn).setOnClickListener {onChangePassword(view) }
        requireView().findViewById<Button>(R.id.logOutBtn).setOnClickListener {  onLogOut(view)}
        requireView().findViewById<Button>(R.id.deleteAccountBtn).setOnClickListener { onDeleteAccount(view)}
        requireView().findViewById<Button>(R.id.back2).setOnClickListener {
            requireActivity().onBackPressed()
        }

        val nameObserver = Observer<User> { newName ->
            // Update the UI, in this case, a TextView.
            if (newName != null) {
                requireView().findViewById<TextView>(R.id.userIDTextView).setText(newName.username)
            }
        }
        model.currentUser.observe(viewLifecycleOwner, nameObserver)

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0){
            if (resultCode == Activity.RESULT_OK){
                data!!
                var f = File(currentPhotoPath)
                Picasso.get().load(f).into(imageView)

    //                val myBitmap = BitmapFactory.decodeFile(currentPhotoPath)
    //                        imageView.setImageBitmap(myBitmap)
                val model: GroSureViewModel by activityViewModels()
                var temp = model.currentUser.value!!
                var temp2 = User(model.currentUser.value!!.username, model.currentUser.value!!.password, currentPhotoPath, model.currentUser.value!!.notifs)
                model.userList.value!!.remove(temp)
                model.userList.value!!.add(temp2)
                model.currentUser.value = temp2
                writeUsers()
                var mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                var contentUri = Uri.fromFile(f)
                mediaScanIntent.setData(contentUri)
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
                currentPhotoPath = this!!.getPath(requireContext(), uriContentUri)
    //                val myBitmap = BitmapFactory.decodeFile(currentPhotoPath)
    //                        imageView.setImageBitmap(myBitmap)
                val model: GroSureViewModel by activityViewModels()
                var temp = model.currentUser.value!!
                var temp2 = User(model.currentUser.value!!.username, model.currentUser.value!!.password, currentPhotoPath, model.currentUser.value!!.notifs)
                model.userList.value!!.remove(temp)
                model.userList.value!!.add(temp2)
                model.currentUser.value = temp2
                writeUsers()
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
            currentPhotoPath = absolutePath
            Log.i("2", currentPhotoPath)
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

    private fun onChangePassword(view: View) {
        val taskEditText = EditText(view.context)
        val model: GroSureViewModel by activityViewModels()
        taskEditText.inputType = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        taskEditText.transformationMethod = PasswordTransformationMethod.getInstance()

        val dialog = AlertDialog.Builder(view.context)
                .setTitle("Change Password")
                .setMessage("")
                .setView(taskEditText)
                .setPositiveButton("Set") { dialog, arg1 ->
                    val task = taskEditText.text.toString()
                    if (task.length >= 8 && !task.contains(",")) {
                        val model: GroSureViewModel by activityViewModels()
                        model.currentUser.value!!.password = task
                        dialog.dismiss()
                        writeItems()
                        writeUsers()
                        writeTrips()
                        Toast.makeText(requireContext(), "Password for ${model.currentUser.value!!.username} has been changed.", Toast.LENGTH_LONG).show()
                    } else {
                        if (task.contains(",")){
                            val dialog = AlertDialog.Builder(view.context)
                                    .setTitle("Error")
                                    .setMessage("The password cannot contain commas please.")
                                    .setPositiveButton("Ok") { dialog, _ ->
                                        dialog.dismiss()
                                    }.create().show()
                        }
                        else {
                            val dialog = AlertDialog.Builder(view.context)
                                    .setTitle("Error")
                                    .setMessage("The password has to have at the very least 8 characters")
                                    .setPositiveButton("Ok") { dialog, _ ->
                                        dialog.dismiss()
                                    }.create().show()
                        }
                        Toast.makeText(requireContext(), "Password not updated.", Toast.LENGTH_LONG).show()}
                }
                .setNegativeButton("Cancel", null)
                .create()

        dialog.show()

    }
    private fun onChangeUsername(view: View) {
        val taskEditText = EditText(view.context)
        taskEditText.inputType = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        taskEditText.transformationMethod = PasswordTransformationMethod.getInstance()

        val dialog = AlertDialog.Builder(view.context)
                .setTitle("Change Username")
                .setMessage("")
                .setView(taskEditText)
                .setPositiveButton("Set") { dialog, arg1 ->
                    val task = taskEditText.text.toString()
                    if (task.length <= 2) {
                        val dialog = AlertDialog.Builder(view.context)
                                .setTitle("Error")
                                .setMessage("The username has to have at least 3 characters")
                                .setPositiveButton("Ok") { dialog, _ ->
                                    dialog.dismiss()
                                }.create().show()
                    }
                    else if (task.contains(",")){
                        val dialog = AlertDialog.Builder(view.context)
                                .setTitle("Error")
                                .setMessage("The username cannot contain a comma")
                                .setPositiveButton("Ok") { dialog, _ ->
                                    dialog.dismiss()
                                }.create().show()
                    }
                    else if (task.contains(" ")){
                        val dialog = AlertDialog.Builder(view.context)
                                .setTitle("Error")
                                .setMessage("The username cannot contain a empty character")
                                .setPositiveButton("Ok") { dialog, _ ->
                                    dialog.dismiss()
                                }.create().show()
                    }
                    else {
                        val model: GroSureViewModel by activityViewModels()
                        var temp = model.currentUser.value!!
                        var alreadyRegistered = false

                        for (t in model.userList.value!!) {
                            if (task == t.username) {
                                alreadyRegistered = true
                            }
                        }

                        if (alreadyRegistered) {
                            val dialog = AlertDialog.Builder(view.context)
                                    .setTitle("Error")
                                    .setMessage("Another user already has this username")
                                    .setPositiveButton("Ok") { dialog, _ ->
                                        dialog.dismiss()
                                    }.create().show()
                        } else {

                            temp.username = task
                            while (model.userList.value!!.contains(model.currentUser.value!!)) {
                                model.userList.value!!.remove(model.currentUser.value!!)
                            }
                            model.currentUser.value = temp
                            model.userList.value!!.add(temp)
                            dialog.dismiss()
                            Toast.makeText(requireContext(), "Username has been changed to ${model.currentUser.value!!.username}", Toast.LENGTH_LONG).show()
                            writeItems()
                            writeTrips()
                            writeUsers()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()

        dialog.show()


        val model: GroSureViewModel by activityViewModels()
    }

    private fun onLogOut(view: View) {
        val model: GroSureViewModel by activityViewModels()
        Toast.makeText(requireContext(),"User ${model.currentUser.value!!.username} logged out successfully" ,Toast.LENGTH_SHORT).show()
        model.currentUser.value = null
        model.loggedIn.value = false
        writeItems()
        writeUsers()
        writeTrips()
        navController!!.navigate(R.id.action_profileFragment_to_enterFragment)
    }

    private fun onDeleteAccount(view: View) {
        val model: GroSureViewModel by activityViewModels()
        Toast.makeText(requireContext(),"User ${model.currentUser.value!!.username} deleted successfully" ,Toast.LENGTH_SHORT).show()

        while (model.userList.value!!.contains(model.currentUser.value!!)) {
            model.userList.value!!.remove(model.currentUser.value!!)
        }
        model.currentUser.value = null
        model.loggedIn.value = false
        writeItems()
        writeTrips()
        writeUsers()
        navController!!.navigate(R.id.action_profileFragment_to_enterFragment)

    }
    private fun writeItems(){
        try {
            val myViewModel: GroSureViewModel by activityViewModels()
            val fOut = requireContext().openFileOutput("items", Context.MODE_PRIVATE)
            for (a: Item in myViewModel.itemList.value!!) {

                if (a.user in myViewModel.userList.value!!) {

                        fOut.write((a.itemName + "," + a.brand + "," + a.itemPrice.toString() + "," + a.itemPicture.toString() +"," + a.isFile.toString() + "," + a.user.username +"\n").toByteArray())
                    }
            }
            if (myViewModel.itemList.value!!.isEmpty()){
                fOut.write("".toByteArray())
            }
            fOut.close()

            Log.i("successfully written items", "profilefragment")
        }
        catch (e : java.lang.Exception){
        }
    }
    private fun writeUsers(){
        try {
            val myViewModel: GroSureViewModel by activityViewModels()
            val fOut = requireContext().openFileOutput("users", Context.MODE_PRIVATE)
            if (myViewModel.loggedIn.value!!) {
                fOut.write(("true" +"," +  myViewModel.currentUser.value!!.username +"\n").toByteArray())
            } else {
                fOut.write(("false" +",\n").toByteArray())
            }

            for (a: User in myViewModel.userList.value!!) {
                fOut.write((a.username + "," + a.password + "," + a.profilePicture + "," + a.notifs.toString() +"\n").toByteArray())
            }
            fOut.close()
            Log.i("successfully written users", "profilefragment")
        }
        catch (e : java.lang.Exception){
        }
    }
    private fun writeTrips(){
        try {
            val myViewModel: GroSureViewModel by activityViewModels()
            val fOut = requireContext().openFileOutput("trips", Context.MODE_PRIVATE)
            var sdf =  DateTimeFormatter.ofPattern("dd/MM/yy")
            for (a: Trip in myViewModel.events) {
                var stringBuild = a.text + "," + a.date.format(sdf) + "," + a.user.username + "," + a.price.toString() +"," + a.time
                for (item in a.itemList){
                    stringBuild += "," + item.item.itemName + "," + item.number.toString()
                }
                stringBuild+= "\n"
                fOut.write((stringBuild).toByteArray())
            }
            fOut.close()
        }
        catch (e : java.lang.Exception){
        }
    }
}