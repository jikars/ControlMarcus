package gcatech.net.controlcameramarcus.cameracontrol.`interface`

import gcatech.net.controlcameramarcus.cameracontrol.callback.INotifyResultProviderPicture
import gcatech.net.controlcameramarcus.cameracontrol.model.Picture

interface IProvaiderCapture {
    fun init(paths : ArrayList<Picture>?, notify : INotifyResultProviderPicture)
    fun permissionResult(code: Int, permission : Array<out String>)
}