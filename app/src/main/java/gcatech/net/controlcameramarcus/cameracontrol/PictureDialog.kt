package gcatech.net.controlcameramarcus.cameracontrol

import android.content.Intent
import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gcatech.net.controlcameramarcus.R
import gcatech.net.controlcameramarcus.cameracontrol.model.Picture
import kotlinx.android.synthetic.main.dialog_selection.*


@Suppress("UNCHECKED_CAST")
class PictureDialog  : DialogFragment()  {


    private   var  pictures : ArrayList<Picture>? = null

    companion object {
        private  const val PICTURE_KEY ="PICTUREKEYDIALOG"
        fun newInstance(pictures : ArrayList<Picture>?): PictureDialog {
            val dialog = PictureDialog()
            val args = Bundle()
            if(pictures != null){
                args.putSerializable(PICTURE_KEY, pictures)
            }
            dialog.arguments = args
            return dialog
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if( arguments?.getSerializable(PICTURE_KEY) != null){
            pictures =arguments?.getSerializable(PICTURE_KEY) as   ArrayList<Picture>?
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(PICTURE_KEY,pictures )
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        if(savedInstanceState?.getSerializable(PICTURE_KEY) != null){
            pictures = savedInstanceState.getSerializable(PICTURE_KEY)!! as   ArrayList<Picture>?
        }
        super.onViewStateRestored(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_selection, container, false)
    }

    override fun onStart() {
        super.onStart()
        cameraMode.setOnClickListener{
            this.dismiss()
            if(pictures == null || pictures?.count()!! < 5){
                val intent = Intent(activity,CameraActivity::class.java)
                intent.putExtra(CameraActivity.PICTURE_KEY,pictures )
                activity?.startActivityForResult( intent,PictureCapture.CAMERA_REQUEST)
            }

        }
        galeryMode.setOnClickListener{
            this.dismiss()
            if(pictures == null || pictures?.count()!! < 5) {
                val intent = Intent()
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                activity?.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PictureCapture.SELECT_PICTURE)
            }
        }
    }

}