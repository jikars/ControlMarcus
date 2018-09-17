package gcatech.net.controlcameramarcus

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import gcatech.net.controlcameramarcus.cameracontrol.PictureCapture
import gcatech.net.controlcameramarcus.cameracontrol.PictureDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PictureCapture.start(this,"RXYZ123")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PictureCapture.onActivityResult(requestCode,resultCode,data)
    }
}
