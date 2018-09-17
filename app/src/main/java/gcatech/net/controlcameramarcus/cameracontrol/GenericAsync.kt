package gcatech.net.controlcameramarcus.cameracontrol

import android.os.AsyncTask

class GenericAsync (private val handler: () -> Unit, private  val handlerPost :() -> Unit) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        handlerPost()
    }
}