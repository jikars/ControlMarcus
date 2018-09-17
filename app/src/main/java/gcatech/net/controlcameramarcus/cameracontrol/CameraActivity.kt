package gcatech.net.controlcameramarcus.cameracontrol

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.ImageView
import gcatech.net.controlcameramarcus.R
import kotlinx.android.synthetic.main.activity_camera.*
import net.gcatech.cameramanager.callback.INotifyCameraView
import android.os.Handler
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import gcatech.net.controlcameramarcus.cameracontrol.model.Picture
import java.io.File
import java.io.FileOutputStream


class CameraActivity : AppCompatActivity(),INotifyCameraView {

    private var initCamera : Boolean = false
    private var currentData: ByteArray? = null
    private var mIndicator : Int = 0
    private lateinit var imageFileName  : String
    private  lateinit var  mFile : File
    private lateinit var  currentType : TypeFile
    private var pictures = ArrayList<Picture>()
    private lateinit var currentImageView : ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_camera)
        if(pictures.count() == 0){
            mIndicator = 0
            arrowFrontSide.visibility = View.VISIBLE
            imageFileName =  PictureCapture.getPictureFromIndex(0)
            mFile = PictureCapture.getOutputMediaFile(this, imageFileName)
            currentType = TypeFile.FrontSide
            currentImageView = btnFrontSide
        }

        captureImage.setOnClickListener{
            camera.getPicture()
        }
    }


    override fun onResume() {
        super.onResume()
        if(!initCamera){
            initCamera = true
            val imgGhost = ImageView(this)
            imgGhost.layoutParams  = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imgGhost.background = ContextCompat.getDrawable(this, R.drawable.fantasma)
            contentGhost.addView(imgGhost)
            camera.initCamera(true,this,null)
        }

        camera.startCamera()
        Handler().postDelayed({ camera.activeFlash() }, 1500)
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.permissionProcess(requestCode,permissions)
    }


    override fun onPause() {
        super.onPause()
        camera.stopCamera()
    }

    override fun notifyCameraCapture(data: ByteArray) {
        setupImageDisplay(data)
    }


    //Este codigo es idedentico al de oncreate
    private fun configurePreviousPhotoData(iconToReplace: ImageView, nextVisibleArrow: ImageView, nextIcon: Int, pos: Int, type: TypeFile) {
        mIndicator = nextIcon
        loadPicture(iconToReplace, File(pictures[pos].path))
        nextVisibleArrow.visibility = View.VISIBLE
        imageFileName = PictureCapture.getPictureFromIndex(pos)
        mFile = PictureCapture.getOutputMediaFile(this, imageFileName)
        currentType = type
    }


    private fun configure(ivSelected: ImageView, nextVisibleArrow: ImageView, nextPos: Int, fileName: String, type: TypeFile) {
       // currentImageView = ivSelected
        mIndicator = nextPos
        nextVisibleArrow.visibility = View.VISIBLE
        imageFileName = PictureCapture.getPictureFromIndex(nextPos)
        mFile = PictureCapture.getOutputMediaFile(this, imageFileName)
        currentType = type
    }


    //Se debne cerrar con kotlin nuevamente
    private fun setupImageDisplay(data: ByteArray){
        camera.stopCamera()
        GenericAsync({
            val currentBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            val output = FileOutputStream(mFile)
            currentBitmap.compress(Bitmap.CompressFormat.JPEG, 60, output)
        },{}).execute()
    }


    private fun loadPicture(iv: ImageView, file: File) {

    }



    enum class TypeFile {
        FrontSide, RightSide, LeftSide, PostSide, FreeSide, FrontDocSide, PostDocSide, signatureA, signatureB
    }
}