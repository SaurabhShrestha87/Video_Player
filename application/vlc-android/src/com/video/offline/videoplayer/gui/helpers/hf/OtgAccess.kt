package com.video.offline.videoplayer.gui.helpers.hf

import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import com.video.offline.videoplayer.R

const val SAF_REQUEST = 85
const val TAG = "OtgAccess"

const val OTG_SCHEME = "otg"

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class OtgAccess : BaseHeadlessFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            AlertDialog.Builder(requireActivity())
                    .setTitle(resources.getString(R.string.allow_otg))
                    .setMessage(resources.getString(R.string.allow_otg_description))

                    .setPositiveButton(R.string.ok) { _, _ ->
                        val safIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                        try {
                            startActivityForResult(safIntent, SAF_REQUEST)
                        } catch (e: ActivityNotFoundException) {
                            exit()
                        }
                    }

                    .setOnCancelListener {
                        exit()
                    }
                    .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (intent != null && requestCode == SAF_REQUEST) otgRoot.value = intent.data
        else super.onActivityResult(requestCode, resultCode, intent)
        exit()
    }

    companion object {
        val otgRoot = MutableStateFlow<Uri?>(null)
    }
}

fun FragmentActivity.requestOtgRoot() {
    supportFragmentManager.beginTransaction().add(OtgAccess(), TAG).commitAllowingStateLoss()
}

@WorkerThread
fun getDocumentFiles(context: Context, path: String) : List<MediaWrapper>? {
    val rootUri = OtgAccess.otgRoot.value ?: return null
    var documentFile = DocumentFile.fromTreeUri(context, rootUri)

    val parts = path.substringAfterLast(':').split("/".toRegex()).dropLastWhile { it.isEmpty() }
    for (part in parts) {
        if (part == "") continue
        documentFile = documentFile?.findFile(part)
    }

    if (documentFile == null) {
        Log.w(TAG, "Failed to find file")
        return null
    }

    // we have the end point DocumentFile, list the files inside it and return
    val list = mutableListOf<MediaWrapper>()
    for (file in documentFile.listFiles()) {
        if (file.exists() && file.canRead()) {
            if (file.name?.startsWith(".") == true) continue
            val mw = MLServiceLocator.getAbstractMediaWrapper(file.uri).apply {
                type = when {
                    file.isDirectory -> MediaWrapper.TYPE_DIR
                    file.type?.startsWith("video") == true -> MediaWrapper.TYPE_VIDEO
                    file.type?.startsWith("audio") == true -> MediaWrapper.TYPE_AUDIO
                    else -> type
                }
                title = file.name
            }
            list.add(mw)
        }
    }
    return list
}
