#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARNING,LOG_TAG,__VA_ARGS__)

class AndroidMediaLibraryLogger : public medialibrary::ILogger
{
    virtual void Error( const std::string& msg ) override
    {
        __android_log_print( ANDROID_LOG_ERROR, "VLC/medialibrary", "%s", msg.c_str() );
    }

    virtual void Warning( const std::string& msg ) override
    {
        __android_log_print( ANDROID_LOG_WARN, "VLC/medialibrary", "%s", msg.c_str() );
    }

    virtual void Info( const std::string& msg ) override
    {
        __android_log_print( ANDROID_LOG_INFO, "VLC/medialibrary", "%s", msg.c_str() );
    }

    virtual void Debug( const std::string& msg ) override
    {
        __android_log_print( ANDROID_LOG_DEBUG, "VLC/medialibrary", "%s", msg.c_str() );
    }

    virtual void Verbose( const std::string& msg ) override
    {
        __android_log_print( ANDROID_LOG_VERBOSE, "VLC/medialibrary", "%s", msg.c_str() );
    }
};

#endif // LIBVLCJNI_LOG_H
