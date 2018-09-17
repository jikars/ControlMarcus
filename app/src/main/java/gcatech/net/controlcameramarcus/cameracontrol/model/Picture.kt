package gcatech.net.controlcameramarcus.cameracontrol.model

import gcatech.net.controlcameramarcus.cameracontrol.CameraActivity
import java.io.Serializable


class Picture (path : String,typePicture : CameraActivity.TypeFile) : Serializable {
     var path = path
    var typePicture = typePicture
}