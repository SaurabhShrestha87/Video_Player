package com.video.offline.videoplayer.gui.browser

import android.content.Context
import android.os.Handler
import android.view.View
import android.widget.CheckBox
import androidx.collection.SimpleArrayMap
import androidx.fragment.app.FragmentActivity
import org.videolan.medialibrary.interfaces.RootsEventsCb
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.resources.util.canReadStorage
import org.videolan.tools.*
import com.video.offline.videoplayer.MediaParsingService
import com.video.offline.videoplayer.gui.SecondaryActivity
import com.video.offline.videoplayer.gui.helpers.MedialibraryUtils
import com.video.offline.videoplayer.gui.helpers.ThreeStatesCheckbox
import com.video.offline.videoplayer.util.Permissions

interface IStorageFragmentDelegate {
    fun checkBoxAction(v: View, mrl: String)
    fun addRootsCallback()
    fun removeRootsCallback()
    val processingFolders: SimpleArrayMap<String, CheckBox>

    fun withContext(context: Context)
    fun withAdapters(adapters: Array<StorageBrowserAdapter>)
    fun addBannedFoldersCallback(callback: (folder:String, banned: Boolean)-> Unit)
}

class StorageFragmentDelegate : IStorageFragmentDelegate, RootsEventsCb {
    private lateinit var adapters: Array<StorageBrowserAdapter>
    private lateinit var context:Context
    override val processingFolders = SimpleArrayMap<String, CheckBox>()
    private  val handler = Handler()
    private var bannedFolderCallback: ((folder: String, banned: Boolean) -> Unit)? = null

    override fun withContext(context: Context) {
        this.context = context
    }

    override fun withAdapters(adapters: Array<StorageBrowserAdapter>) {
        this.adapters = adapters
    }

    override fun addBannedFoldersCallback(callback: (folder: String, banned: Boolean) -> Unit) {
        bannedFolderCallback = callback
    }

    override fun addRootsCallback() {
        Medialibrary.getInstance().addRootsEventsCb(this)
    }

    override fun removeRootsCallback() {
        Medialibrary.getInstance().removeRootsEventsCb(this)
    }

    override fun checkBoxAction(v: View, mrl: String) {
        val tscb = v as ThreeStatesCheckbox
        val checked = tscb.state == ThreeStatesCheckbox.STATE_CHECKED
        if (checked && mrl.contains("file://") && !canReadStorage(context)) {
            Permissions.showStoragePermissionDialog(context as FragmentActivity, false)
            tscb.state = ThreeStatesCheckbox.STATE_UNCHECKED
            return
        }
        if ((context as? SecondaryActivity)?.isOnboarding == true) {
            val path = mrl.sanitizePath()
            if (checked) {
                MediaParsingService.preselectedStorages.removeAll { it.startsWith(path) }
                MediaParsingService.preselectedStorages.add(path)
            } else {
                MediaParsingService.preselectedStorages.removeAll { it.startsWith(path) }
            }
        } else {
            if (checked) {
                MedialibraryUtils.addDir(mrl, v.context.applicationContext)
                val prefs = Settings.getInstance(v.getContext())
                if (prefs.getInt(KEY_MEDIALIBRARY_SCAN, -1) != ML_SCAN_ON) prefs.putSingle(KEY_MEDIALIBRARY_SCAN, ML_SCAN_ON)
            } else
                MedialibraryUtils.removeDir(mrl)
            processEvent(v as CheckBox, mrl)
        }
    }

    private fun processEvent(cbp: CheckBox, mrl: String) {
        cbp.isEnabled = false
        processingFolders.put(mrl, cbp)
    }

    override fun onRootBanned(entryPoint: String, success: Boolean) {
        handler.post { bannedFolderCallback?.invoke(entryPoint, true) }
    }

    override fun onRootUnbanned(entryPoint: String, success: Boolean) {
        handler.post { bannedFolderCallback?.invoke(entryPoint, false) }
    }

    override fun onRootAdded(entryPoint: String, success: Boolean) {}

    override fun onRootRemoved(entrypoint: String, success: Boolean) {
        var entryPoint = entrypoint
        if (entryPoint.endsWith("/"))
            entryPoint = entryPoint.substring(0, entryPoint.length - 1)
        if (processingFolders.containsKey(entryPoint)) {
            processingFolders.remove(entryPoint)?.let {
                handler.post {
                    it.isEnabled = true
                    if (success) {
                        adapters.forEach {
                            it.updateMediaDirs(context)
                            it.notifyDataSetChanged()
                        }
                    } else
                        it.isChecked = true
                }
            }
        }
    }

    override fun onDiscoveryStarted() {}

    override fun onDiscoveryProgress(entryPoint: String) {}

    override fun onDiscoveryCompleted() {
        handler.post { for (i in 0 until processingFolders.size()) processingFolders.get(processingFolders.keyAt(i))?.isEnabled = true }
        adapters.forEach {
            it.updateMediaDirs(context)
        }
    }

    override fun onDiscoveryFailed(entryPoint: String) {

    }
}