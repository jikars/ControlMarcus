package gcatech.net.controlcameramarcus.cameracontrol.callback

import gcatech.net.controlcameramarcus.cameracontrol.model.Picture

interface INotifyResultProviderPicture {
    fun picturesResult(paths : ArrayList<Picture>)
}