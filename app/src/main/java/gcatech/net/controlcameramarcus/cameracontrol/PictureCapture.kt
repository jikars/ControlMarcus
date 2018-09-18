package gcatech.net.controlcameramarcus.cameracontrol

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import gcatech.net.controlcameramarcus.cameracontrol.model.Picture
import java.io.File
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.FileOutputStream
import java.io.InputStream
import java.util.stream.Stream


@Suppress("UNCHECKED_CAST")
class PictureCapture {



    companion object {
        private  var pictures : ArrayList<Picture>? = null
        private lateinit var plate : String
        private lateinit var activity: AppCompatActivity
        private  lateinit var  mIndicator : String
        const val PHOTOACCIDENT_ONE = "Evidencia_{0}_1"
        const val PHOTOACCIDENT_TWO = "Evidencia_{0}_2"
        const val PHOTOACCIDENT_THREE = "Evidencia_{0}_3"
        const val PHOTOACCIDENT_FOUR = "Evidencia_{0}_4"
        const  val PHOTOACCIDENT_FREE = "Evidencia_{0}_5"
        const  val CAMERA_REQUEST = 200
        const  val SELECT_PICTURE = 300
        private var dialog : PictureDialog? = null
        fun  start(activity : AppCompatActivity,plate :String,mIndicator :String){
            this.plate = plate
            this.mIndicator = mIndicator
            this.activity = activity
            val dialog = PictureDialog.newInstance(pictures)
            dialog.show(activity.supportFragmentManager,PictureDialog::class.java.name)
        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            dialog?.onActivityResult(requestCode,resultCode,data)
            if(resultCode == Activity.RESULT_OK && requestCode == PictureCapture.CAMERA_REQUEST){
                processPictureFromCamera(data)
            }else if(resultCode == Activity.RESULT_OK && requestCode == PictureCapture.SELECT_PICTURE){
                processPictureFromGalery(data)
            }
        }



        private  fun processPictureFromCamera( data: Intent?){
            if(data?.getSerializableExtra(CameraActivity.PICTURE_KEY)!= null){
                pictures = data.getSerializableExtra(CameraActivity.PICTURE_KEY) as   ArrayList<Picture>?
            }
        }

        private  fun processPictureFromGalery( data: Intent?){
            val mArrayUri = ArrayList<Uri>()
            if(data?.data != null){
                mArrayUri.add(data.data)
            } else if (data?.clipData != null) {
                for (i in 0 until data.clipData.itemCount) {
                    mArrayUri.add(data.clipData.getItemAt(i).uri)
                }
            }
            var imgBitmap : Bitmap
            var inputStream : InputStream?
            var  file : File?
            var output : FileOutputStream
            if(pictures == null){
                pictures = ArrayList()
            }
            var cont =1
            mArrayUri.forEach{
                inputStream = activity.contentResolver.openInputStream(it)
                imgBitmap = BitmapFactory.decodeStream(inputStream)
                file = getOutputMediaFile(activity,getPictureFromIndex(cont))
                inputStream?.close()
                output = FileOutputStream(file)
                imgBitmap.compress(Bitmap.CompressFormat.JPEG, 60, output)
                pictures?.add(Picture(file?.absolutePath!!,getTypePictureFromInxed(cont)))
                output.close()
                if(cont == 5){
                    return
                }
                cont ++
            }
        }


        fun getTypePictureFromInxed(indexPicture : Int) : CameraActivity.TypeFile {
            lateinit var type : CameraActivity.TypeFile
            when(indexPicture){
                1 -> {type =  CameraActivity.TypeFile.FrontSide}
                2 ->{type = CameraActivity.TypeFile.RightSide}
                3 ->{type = CameraActivity.TypeFile.LeftSide}
                4 ->{type = CameraActivity.TypeFile.PostSide}
                5 ->{type = CameraActivity.TypeFile.FreeSide}
            }
            return  type
        }


        fun getPictureFromIndex(indexPicture : Int) : String{
            var nameBase = ""
            when(indexPicture){
                1 -> {nameBase = PHOTOACCIDENT_ONE}
                2 ->{nameBase = PHOTOACCIDENT_TWO}
                3 ->{nameBase = PHOTOACCIDENT_THREE}
                4 ->{nameBase = PHOTOACCIDENT_FOUR}
                5 ->{nameBase = PHOTOACCIDENT_FREE}
            }
           return  nameBase.replace("{0}", plate) + ".png"
        }

        fun getOutputMediaFile(mContext: Context, imageName: String): File {
            var storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            storageDir = File(storageDir, "filesGost")
            val image: File
            if (!storageDir.exists())
                storageDir.mkdir()
            image = File(storageDir, imageName)
            return image
        }
    }
}