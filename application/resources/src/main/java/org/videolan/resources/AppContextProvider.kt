package org.videolan.resources

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import org.videolan.tools.wrap
import java.lang.reflect.InvocationTargetException

object AppContextProvider {

    private lateinit var context: Context
    var currentActivity: Activity? = null


    // Property to get the new locale only on restart to prevent change the locale partially on runtime
    var locale: String? = ""
        private set

    fun setLocale(newLocale: String?) {
        locale = newLocale
        updateContext()
    }

    val appContext: Context
        get() {
            return if (::context.isInitialized) context
            else {
                try {
                    context = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication").invoke(null) as Application
                } catch (ignored: IllegalAccessException) {
                } catch (ignored: InvocationTargetException) {
                } catch (ignored: NoSuchMethodException) {
                } catch (ignored: ClassNotFoundException) {
                } catch (ignored: ClassCastException) {}
                context
            }
        }

    fun init(context: Context) {
        this.context = context
    }

    fun updateContext() {
        locale.takeIf { !it.isNullOrEmpty() }?.let {
            init(ContextWrapper(appContext).wrap(it))
        }
    }

    /**
     * @return the main resources from the Application
     */
    val appResources: Resources
        get() = appContext.resources
}
