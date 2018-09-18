@file:Suppress("UNCHECKED_CAST")

package gcatech.net.controlcameramarcus.cameracontrol

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
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
import com.bumptech.glide.signature.StringSignature
import gcatech.net.controlcameramarcus.cameracontrol.model.Picture
import java.io.File
import java.io.FileOutputStream


class CameraActivity : AppCompatActivity(),INotifyCameraView, View.OnClickListener {


    private var initCamera : Boolean = false
    private var currentData: ByteArray? = null
    private  var mIndicator : Int = 0
    private lateinit var imageFileName  : String
    private  lateinit var  mFile : File
    private lateinit var  currentType : TypeFile
    private var pictures :  ArrayList<Picture>? = ArrayList()
    private lateinit var currentImageView : ImageView
    private lateinit var mCurrentPicture : Picture
    private var isPictureProcessing = false
    private var isPreviewing = false
    private lateinit var  imgGhost : ImageView
    private var previousDataExists: Boolean = false


    companion object {
        const val PICTURE_KEY = "PICTUREKEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_camera)


        if(pictures == null || pictures?.count() == 0){
            mIndicator = 1
            arrowFrontSide.visibility = View.VISIBLE
            imageFileName =  PictureCapture.getPictureFromIndex(1)
            mFile = PictureCapture.getOutputMediaFile(this, imageFileName)
            currentType = TypeFile.FrontSide
            currentImageView = btnFrontSide
        }

        captureImage.setOnClickListener{
                camera.getPicture()
        }

        saveButton.setOnClickListener{
            if(currentData != null){
                mCurrentPicture = Picture(mFile.absolutePath,currentType)
                checkCurrent()
                loadPicture(currentImageView, File(mCurrentPicture.path))
                reviewList()
                pictures!!.add(mCurrentPicture)
                if (pictures!!.count() >= 4) {
                    btnOk.visibility = View.VISIBLE
                }
                isPictureProcessing = false
                rlPicturePreview.visibility = View.GONE
                llCameraContent.visibility = View.VISIBLE
                isPreviewing = false
                startCamera()
            }
        }

        btnFrontSide.setOnClickListener(this)
        btnRightSide.setOnClickListener(this)
        btnLeftSide.setOnClickListener(this)
        btnPostSide.setOnClickListener(this)
        btnFreeSide.setOnClickListener(this)
        btnOk.setOnClickListener(this)
        btnCancelPic.setOnClickListener(this)

        if(intent.getSerializableExtra(PICTURE_KEY) != null){
            pictures = intent.getSerializableExtra(PICTURE_KEY) as ArrayList<Picture>?
        }
        if(pictures == null){
            pictures = ArrayList()
        }
        previousDataExists =pictures?.count() !!>0
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(PICTURE_KEY,pictures )
        super.onSaveInstanceState(outState)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if(intent.getSerializableExtra(PICTURE_KEY) != null){
            pictures = savedInstanceState?.getSerializable(PICTURE_KEY) as ArrayList<Picture>?
        }
        if(pictures == null){
            pictures = ArrayList()
        }
        previousDataExists =pictures?.count()!!>0
        super.onRestoreInstanceState(savedInstanceState)
    }




    override fun onClick(v: View?) {
        var imgIdResource = 0
        var isVisibility = true
        when (v?.id) {
            R.id.btnCancelPic -> {
                pictures!!.clear()
                this.finish()
            }
            R.id.btnOk -> {
                val intent = Intent()
                intent.putExtra(PICTURE_KEY, pictures)
                setResult(Activity.RESULT_OK, intent)
                this.finish()
            }
            R.id.btnFrontSide -> {
                configureOnClick(btnFrontSide, arrowFrontSide, 1, TypeFile.FrontSide)
                imgIdResource = R.drawable.fantasma
            }
            R.id.btnRightSide -> {
                configureOnClick(btnRightSide, arrowRightSide, 2, TypeFile.RightSide)
                imgIdResource = R.drawable.fantasma_der
            }
            R.id.btnLeftSide -> {
                configureOnClick(btnLeftSide, arrowLeftSide, 3, TypeFile.LeftSide)
                imgIdResource = R.drawable.fantasma_izq
            }
            R.id.btnPostSide -> {
                configureOnClick(btnPostSide, arrowPostSide, 4, TypeFile.PostSide)
                imgIdResource = R.drawable.fantasma
            }
            R.id.btnFreeSide -> {
                configureOnClick(btnFreeSide, arrowFreeSide, 5, TypeFile.FreeSide)
                isVisibility = false
                imgIdResource = -1
            }
        }
        if (imgIdResource != 0){
            changeVisualEffects(imgIdResource, null, isVisibility)
        }
    }


    private fun reviewList() {
        var modelToDelete: Picture? = null
        if ( pictures?.count()!! > 0) {
            for (x in 0 until pictures?.count()!!) {
                if (mCurrentPicture.typePicture ==  pictures!![x].typePicture) {
                    modelToDelete = pictures!![x]
                    break
                }
            }
        }
        if (modelToDelete != null) {
            pictures?.remove(modelToDelete)
        }
    }



    private fun configureOnClick(currentSelected: ImageView, selectedArrow: ImageView, ind: Int, type: TypeFile) {
        currentImageView = currentSelected
        clearArrow()
        selectedArrow.visibility = View.VISIBLE
        mIndicator = ind
        imageFileName =  PictureCapture.getPictureFromIndex(mIndicator)
        mFile = PictureCapture.getOutputMediaFile(this, imageFileName)
        currentType = type
    }

    private fun checkCurrent() {
        runOnUiThread {
            clearArrow()
            var isVisibility = true
            var imgIdResource = 0
            when (mIndicator) {
                1 -> {
                    configure(btnFrontSide, arrowRightSide, 2, PictureCapture.PHOTOACCIDENT_TWO, TypeFile.RightSide)
                    imgIdResource = R.drawable.fantasma_der
                }
                2 -> {
                    configure(btnRightSide, arrowLeftSide, 3,PictureCapture.PHOTOACCIDENT_THREE, TypeFile.LeftSide)
                    imgIdResource = R.drawable.fantasma_izq
                }
                3 -> {
                    configure(btnLeftSide, arrowPostSide, 4, PictureCapture.PHOTOACCIDENT_FOUR, TypeFile.PostSide)
                    imgIdResource = R.drawable.fantasma
                }
                4 -> {
                    configure(btnPostSide, arrowFreeSide, 5, PictureCapture.PHOTOACCIDENT_FREE, TypeFile.FreeSide)
                    imgIdResource = -1
                    isVisibility = false
                }
                5 -> {
                    configure(btnFreeSide, arrowFreeSide, 5, PictureCapture.PHOTOACCIDENT_FREE, TypeFile.FreeSide)
                    imgIdResource = -1
                    isVisibility = false
                }
            }
            if (imgIdResource != 0){
                changeVisualEffects(imgIdResource, null, isVisibility)
            }
        }
    }

    private fun clearArrow() {
        arrowFrontSide.visibility = View.INVISIBLE
        arrowRightSide.visibility = View.INVISIBLE
        arrowLeftSide.visibility = View.INVISIBLE
        arrowPostSide.visibility = View.INVISIBLE
        arrowFreeSide.visibility = View.INVISIBLE
    }


    private fun changeVisualEffects(idImagecharge: Int, image: Image?, isVisible: Boolean) {
        if (isVisible) {
            if (imgGhost.visibility  == View.GONE)
                imgGhost.visibility = View.VISIBLE
            imgGhost.background = ContextCompat.getDrawable(this, idImagecharge)
        } else {
            if (imgGhost.visibility  != View.GONE)
                imgGhost.visibility = View.GONE
        }
        image?.close()
    }

    override fun onResume() {
        super.onResume()
        if(!initCamera){
            initCamera = true
            imgGhost = ImageView(this)
            imgGhost.layoutParams  = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imgGhost.background = ContextCompat.getDrawable(this, R.drawable.fantasma)
            contentGhost.addView(imgGhost)
            camera.initCamera(true,this,null)
        }
        if (previousDataExists)
            loadPreviousContent()
        startCamera()
    }

    override fun onBackPressed() {
        if (isPreviewing) {
            rlPicturePreview.visibility = View.GONE
            llCameraContent.visibility = View.VISIBLE
            isPreviewing = false
            isPictureProcessing = false
            startCamera()
        } else  if (!isPictureProcessing) {
            previousDataExists = false
            pictures?.clear()
            this.finish()
        }
        super.onBackPressed()
    }


    private  fun startCamera(){
        camera.startCamera()
        Handler().postDelayed({ camera.activeFlash() }, 1100)
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
        if(pictures !=  null){
            loadPicture(iconToReplace, File(pictures!![pos].path))
        }
        nextVisibleArrow.visibility = View.VISIBLE
        imageFileName = PictureCapture.getPictureFromIndex(pos)
        mFile = PictureCapture.getOutputMediaFile(this, imageFileName)
        currentType = type
    }


    private  fun loadPreviousContent() {
        var imgIdResource = 0
        var isVisibility = true
        if(pictures != null){
            for (i in 0 until pictures?.count()!!) {
                clearArrow()
                when (pictures!![i].typePicture) {
                    TypeFile.FrontSide -> {
                        configurePreviousPhotoData(btnFrontSide, arrowRightSide, 2, i, TypeFile.RightSide)
                        imgIdResource = R.drawable.fantasma_der
                    }
                    TypeFile.RightSide -> {
                        configurePreviousPhotoData(btnRightSide, arrowLeftSide, 3, i, TypeFile.LeftSide)
                        imgIdResource = R.drawable.fantasma_izq
                    }
                    TypeFile.LeftSide -> {
                        configurePreviousPhotoData(btnLeftSide, arrowPostSide, 4, i, TypeFile.PostSide)
                        imgIdResource = R.drawable.fantasma
                    }
                    TypeFile.PostSide -> {
                        configurePreviousPhotoData(btnPostSide, arrowFreeSide, 5, i, TypeFile.FreeSide)
                        imgIdResource = -1
                        isVisibility = false
                    }
                    TypeFile.FreeSide -> {
                        configurePreviousPhotoData(btnFreeSide, arrowFrontSide, 1, i, TypeFile.FreeSide)
                        imgIdResource = R.drawable.fantasma
                        isVisibility = true
                    }
                }
            }
            mIndicator = pictures?.count()!!
            checkCurrent()
        }
        if (imgIdResource != 0){
            changeVisualEffects(imgIdResource, null, isVisibility)
        }
    }

    private fun configure(ivSelected: ImageView, nextVisibleArrow: ImageView, nextPos: Int, fileName: String, type: TypeFile) {
        currentImageView = ivSelected
        mIndicator = nextPos
        nextVisibleArrow.visibility = View.VISIBLE
        imageFileName = PictureCapture.getPictureFromIndex(nextPos)
        mFile = PictureCapture.getOutputMediaFile(this, imageFileName)
        currentType = type
    }


    //Se debne cerrar con kotlin nuevamente
    private fun setupImageDisplay(data: ByteArray){
        isPreviewing = true
        currentData = data
       camera.desactiveFash()
        GenericAsync({
            val currentBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            val output = FileOutputStream(mFile)
            currentBitmap.compress(Bitmap.CompressFormat.JPEG, 60, output)
        },{
            Glide.with(this)
                    .load(mFile)
                    .asBitmap()
                    .fitCenter()
                    .signature(StringSignature(System.currentTimeMillis().toString()))
                    .into(capturedImage)
            llCameraContent.visibility = View.GONE
            rlPicturePreview.visibility = View.VISIBLE
        }).execute()
    }


    private fun loadPicture(iv: ImageView, file: File) {
         Glide.with(this).load(file)
                .asBitmap()
                .centerCrop()
                .signature(StringSignature(System.currentTimeMillis().toString()))
                .into(BitmapImageTarget(iv))
    }


    inner class  BitmapImageTarget(private var iv : ImageView) : BitmapImageViewTarget(iv){
      override fun setResource(resource: Bitmap) {
            val drawable = RoundedBitmapDrawableFactory.create(resources, resource)
            drawable.isCircular = true
            iv.setImageDrawable(drawable)
        }
    }

    enum class TypeFile {
        FrontSide, RightSide, LeftSide, PostSide, FreeSide, FrontDocSide, PostDocSide, signatureA, signatureB
    }
}