package gcatech.net.controlcameramarcus.cameracontrol

import android.content.Intent
import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gcatech.net.controlcameramarcus.R
import kotlinx.android.synthetic.main.dialog_selection.*


class PictureDialog  : DialogFragment()  {



    companion object {
        fun newInstance(): PictureDialog {
            val dialog = PictureDialog()
            val args = Bundle()
            dialog.arguments = args
            return dialog
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_selection, container, false)
    }

    override fun onStart() {
        super.onStart()
        cameraMode.setOnClickListener{
            this.dismiss()
            activity?.startActivityForResult(Intent(activity,CameraActivity::class.java), PictureCapture.CAMERA_REQUEST)
        }
        galeryMode.setOnClickListener{
            this.dismiss()
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            activity?.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PictureCapture.SELECT_PICTURE)
        }
    }

}