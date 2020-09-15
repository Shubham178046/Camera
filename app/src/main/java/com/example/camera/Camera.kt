package com.example.camera

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.util.*


@Suppress("DEPRECATION")
class Camera : AppCompatActivity() {
    val MY_READ_EXTERNAL_REQUEST : Int = 1
    var flag =1
    private var selectedImagePath = ""
    var IMAGE_WIDTH : Int?=null
    val TAG = "CAMERA"
    var OutletFname : String?=null
    internal var imagePath: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        if (
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
               checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_READ_EXTERNAL_REQUEST)
            }
        }
        btnSelectPhoto.setOnClickListener {
            if(flag == 0)
            {
                Toast.makeText(applicationContext,"Please Allow Camera Permission", Toast.LENGTH_LONG).show()
            }
           else
            {
                selectImage()
            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode === MY_READ_EXTERNAL_REQUEST) {
            if (grantResults[0] === PackageManager.PERMISSION_GRANTED && grantResults[1] === PackageManager.PERMISSION_GRANTED && grantResults[2] === PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, " permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                flag =0
            }
        }
    }
    private fun selectImage() {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val generator = Random()
                var n = 10000
                n = generator.nextInt(n)
                 OutletFname = "Image-$n.jpg"
                Log.d("FileSaveTime", "SelectImage: "+OutletFname)
                val root = Environment.getExternalStorageDirectory().toString()
                val f = File(root + "/capture_photo")
                f.mkdirs()
                val file = File(f, OutletFname)
                val photoURI = FileProvider.getUriForFile(
                    applicationContext,
                    getApplicationContext().getPackageName().toString() + ".provider",
                    file
                )
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, 1)
            } else if (options[item] == "Choose from Gallery") {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, ""), 2)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode === Activity.RESULT_OK) {
            Log.d("Camera1", "onActivityResult: " + "Camera Open")
            if (requestCode === 1) {
                Log.d("Camera", "onActivityResult: " + "Camera Open")
                /*var f =
                    File(Environment.getExternalStorageDirectory().toString())*/
                val root = Environment.getExternalStorageDirectory().toString()
                var f = File(root + "/capture_photo")
                for (temp in f.listFiles()) {
                    if (temp.name == OutletFname) {
                        f = temp
                        break
                    }

                }
                try {
                    val bitmap: Bitmap
                    val bitmapOptions = BitmapFactory.Options()
                    bitmap = BitmapFactory.decodeFile(
                        f.absolutePath,
                        bitmapOptions
                    )
                    IMAGE_WIDTH = bitmap.width
                    Log.d(TAG, "RotateImage: "+ rotateImageByCameraOrientation(applicationContext,bitmap))
                    Log.d("Bitmap", "onActivityResult: "+bitmap)
                    meterimage.setImageBitmap(rotateImageByCameraOrientation(applicationContext,bitmap))


                    /*val root = Environment.getExternalStorageDirectory().toString()
                    Log.d("PathName", "onActivityResult: "+root)

                    val myDir = File(root + "/capture_photo")
                    myDir.mkdirs()
                    val generator = Random()
                    var n = 10000
                    n = generator.nextInt(n)
                    val OutletFname = "Image-$n.jpg"
                    Log.d("FileSaveTime", "onActivityResult: "+OutletFname)
                    var files = File(myDir, OutletFname)
                    if (files.exists()) files.delete()
                    try {
                        val out = FileOutputStream(files)
                        Log.d("File Save", "onActivityResult: "+ "File Save Succesfull")
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        imagePath = files.absolutePath
                        out.flush()
                        out.close()


                    } catch (e: Exception) {
                        e.printStackTrace()

                    }*/

                  /*  val path = (Environment
                        .getExternalStorageDirectory()
                        .toString() + File.separator
                            + "Phoenix" + File.separator + "default")
                    f.delete()
                    var outFile: OutputStream? = null
                    val file =
                        File(path, System.currentTimeMillis().toString() + ".jpg")
                    try {
                        outFile = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile)
                        outFile.flush()
                        outFile.close()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }*/
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode === 2 && resultCode == Activity.RESULT_OK) {
                /*val selectedImage: Uri = data!!.getData()!!
                Log.d("Selected Image", "onActivityResult: "+selectedImage)
                val filePath =
                    arrayOf(MediaStore.Images.Media.DATA)
                val c: Cursor? =
                    contentResolver.query(selectedImage, filePath, null, null, null)
                c!!.moveToFirst()
                val columnIndex: Int = c.getColumnIndex(filePath[0])
                val picturePath: String = c.getString(columnIndex)
                Log.d("Selected Image", "onActivityResult: "+picturePath)
                c.close()
                Log.d("path of image", picturePath + "")
                val bitmap: Bitmap
                bitmap = BitmapFactory.decodeFile(picturePath)
                IMAGE_WIDTH = 1080
                val rotateImage = rotateImageByCameraOrientation(applicationContext,bitmap)*/
                Log.d(TAG, "onActivityResult: "+data!!.getData())
                selectedImagePath = getAbsolutePath(data!!.getData())!!
                meterimage.setImageBitmap(decodeFile(selectedImagePath))
                //meterimage.setImageBitmap(rotateImageByCameraOrientation(applicationContext,bitmap))
               // meterimage.setImageBitmap(rotateImage)
            }
        }
    }

    fun decodeFile(path: String): Bitmap? {
        try {
            // Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, o)
            // The new size we want to scale to
            val REQUIRED_SIZE = 70

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) scale *= 2

            // Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeFile(path, o2)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }
    fun getAbsolutePath(uri: Uri?): String? {
        val projection = arrayOf<String>(MediaStore.MediaColumns.DATA)
        @SuppressWarnings("deprecation")
        val cursor = managedQuery(uri, projection, null, null, null)
        return if (cursor != null) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }
    private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
        Log.d(TAG, "rotateImage: "+ "Function Call")
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0,img.width ,img.height , matrix, true)
        Log.d(TAG, "rotateImage: "+rotatedImg)
        Log.d(TAG, "rotateImageSize: "+rotatedImg.width  + " "+rotatedImg.height )
        return rotatedImg
    }

    fun rotateImageByCameraOrientation(context: Context, img: Bitmap?): Bitmap? {
        Log.d(TAG, "rotateImageByCameraOrientation: "+ "Calling Sucessfull")
      //  Log.e(TAG, "Window rotation--> ${(context as Camera)?.windowManager?.defaultDisplay?.rotation}")
        if (null != img) {
            val image = scaleBitmap(img)
            val manager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
            } else {
                TODO("VERSION.SDK_INT < LOLLIPOP")
            }
            var orientation = 0
            return try {
                val cameraId = manager!!.cameraIdList[0]
                val characteristics = manager.getCameraCharacteristics(cameraId)
                orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!

                if(image.width < image.height) {
                    orientation = 0
                }

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(image, 90)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(image, 180)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(image, 270)
                    90 -> rotateImage(image, 90)
                    180 -> rotateImage(image, 180)
                    270 -> rotateImage(image, 270)
                    else -> rotateImage(image, 0)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in rotateImageByCameraOrientation", e)
                null
            }
        } else {
            Log.d(TAG, "rotateImageByCameraOrientation: "+"Null Value Received")
            return null
        }
    }

    private fun scaleBitmap(bm: Bitmap): Bitmap {
        Log.d(TAG, "scaleBitmap: "+"ScaleBitmap Function Call")
        var bm = bm
        var width = bm.width
        var height = bm.height

        Log.v(TAG, "Width and height are $width--$height")
        Log.d(TAG, "scaleBitmap: "+IMAGE_WIDTH)

        if (width > height) {
// landscape
            val ratio = width.toFloat() / IMAGE_WIDTH!!
            width = IMAGE_WIDTH!!
            height = (height / ratio).toInt()
        } else if (height > width) {
// portrait
            val ratio = height.toFloat() / IMAGE_WIDTH!!
            height = IMAGE_WIDTH!!
            width = (width / ratio).toInt()
        } else {
// square
            height = IMAGE_WIDTH!!
            width = IMAGE_WIDTH!!
        }

        Log.v(TAG, "after scaling Width and height are $width--$height")

        bm = Bitmap.createScaledBitmap(bm, width, height, true)
        return bm
    }
}