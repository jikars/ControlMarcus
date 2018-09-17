package gcatech.net.controlcameramarcus.cameracontrol

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import java.io.File

class PictureCapture {



    companion object {

        private lateinit var plate : String
        const val PHOTOACCIDENT_ONE = "Evidencia_{0}_1"
        const val PHOTOACCIDENT_TWO = "Evidencia_{0}_2"
        const val PHOTOACCIDENT_THREE = "Evidencia_{0}_3"
        const val PHOTOACCIDENT_FOUR = "Evidencia_{0}_4"
        const  val PHOTOACCIDENT_FREE = "Evidencia_{0}_5"
        const  val CAMERA_REQUEST = 200
        const  val SELECT_PICTURE = 200
        private var dialog : PictureDialog? = null
        fun  start(activity : AppCompatActivity,plate :String){
            this.plate = plate
            val dialog = PictureDialog.newInstance()
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

        }

        private  fun processPictureFromGalery( data: Intent?){

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