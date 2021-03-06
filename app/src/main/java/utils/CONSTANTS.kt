package utils

/*
    I have a set of common classes that I use in all my projects.
    CONSTANTS is one of those classes. CONSTANTS is where I keep constants like
    - Filenames
    - Server URLs
    - File paths
    - Extensions
    - Method names
    - Scores
    - API Project ids
*/
object CONSTANTS {

    //INSTAGRAM API
    const val INSTAGRAM_API_BASE_URL = "https://api.instagram.com/"

    //KEYS
    const val KEY_INSTAGRAM_API_GET_TOKEN_CLIENT_ID = "client_id"
    const val KEY_INSTAGRAM_API_GET_TOKEN_CLIENT_SECRET = "client_secret"
    const val KEY_INSTAGRAM_API_GET_TOKEN_GRANT_TYPE = "grant_type"
    const val KEY_INSTAGRAM_API_GET_TOKEN_REDIRECT_URI = "redirect_uri"
    const val KEY_INSTAGRAM_API_GET_TOKEN_CODE = "code"

    const val KEY_INSTAGRAM_API_MEDIA_MAX_TAG_ID = "max_tag_id"
    const val KEY_INSTAGRAM_API_MEDIA_MIN_TAG_ID = "min_tag_id"
    const val KEY_INSTAGRAM_API_MEDIA_TAG_QUERY = "q"
    const val KEY_INSTAGRAM_API_MEDIA_TOKEN = "access_token"

    const val KEY_INSTAGRAM_API_MEDIA_TAG_NAME = "tag-name"

    const val KEY_MEDIA_BUNDLE = "media"
    const val KEY_MEDIA_INDEX_BUNDLE = "index"

    const val KEY_ENVELOPE_METADE = "meta"
    const val KEY_ERROR_ENVELOPE_CODE = "code"
    const val KEY_ERROR_ENVELOPE_TYPE = "error_type"
    const val KEY_ERROR_ENVELOPE_MESSAGE = "error_message"

    const val KEY_INSTAGRAM_MEDIA_TOKEN_TYPE_IMAGE = "image"
    const val KEY_INSTAGRAM_MEDIA_TOKEN_TYPE_VIDEO = "video"
    const val KEY_INSTAGRAM_MEDIA_TOKEN_TYPE_CAROUSEL = "carousel"
    //----------

    //VALUES
    const val VALUE_INSTAGRAM_API_GRANT_TYPE = "authorization_code"
    //----------

    //MISC
    const val STAGGERED_GRID_COLS_SPAN = 2
    const val MEDIA_SEARCH_SCHEDULE_TIME = 1250
    //----------

    //Persistence
    const val DATABASE_NAME = "media_db"
    const val SHAREDPREFS_NAME = "pg_sharedprefs"

    const val KEY_SHAREDPREFS_USER = "current_user"
    //----------

    //media
    const val INSTAGRAM_API_GET_MEDIA = "v1/media/search?"
    const val INSTAGRAM_API_GET_SELF_MEDIA = "v1/users/self/media/recent/"
    const val INSTAGRAM_API_GET_MEDIA_BY_TAG
            = "v1/tags/{$KEY_INSTAGRAM_API_MEDIA_TAG_NAME}/media/recent"
    const val INSTAGRAM_API_GET_TAGS = "v1/tags/search"

    //auth
    const val INSTAGRAM_API_GET_ACCESS_TOKEN_URL = "oauth/access_token"
    const val INSTAGRAM_API_GET_CODE_URL = "https://www.instagram.com/oauth/authorize/?" +
            "client_id=%s" +
            "&redirect_uri=%s" +
            "&response_type=code" +
            "&scope=basic+public_content"
    const val INSTAGRAM_API_AUTH_SCOPES = "&scope=basic+public_content"

    //app settings
    const val INSTAGRAM_API_CLIENT_ID = "61f5845972784b6d84224beb48b6a6d6"

    /*
        According to Instagram API, the Client Secret should NEVER be stored in the client.
        As we are implementing the Server-Side Auth Flow (Explicit), the options were either
        implement using WebView (which is what we're doing here, and that includes storing the
        Client Secret in the app code) or using a free hosting PHP server, which is not the
        most reliable thing in the world. Between those two, keeping the info in the app seems
        to be the best option.
     */
    const val INSTAGRAM_API_CLIENT_SECRET = "ae286571cf6046aa870eacaf617a41d0"
    const val INSTAGRAM_API_REDIRECT_URL = "http://br.com.rafaelverginelli.photogenius"
    //----------

    //WebView Error treating
    const val ERROR_TYPE_UNKOWN = 0
    const val ERROR_TYPE_NONE = 1
    const val ERROR_TYPE_CONNECTION = 2
    const val ERROR_TYPE_INSTAGRAM = 3

    const val ERROR_TYPE_OAUTH_ACCESS_TOKEN_EXCEPTION = "OAuthAccessTokenException"
    const val ERROR_TYPE_OAUTH_FORBIDDEN_EXCEPTION = "OAuthForbiddenException"

    const val ERROR_SOURCE_INSTAGRAM_SERVER = "5xx Server Error"
    const val ERROR_SOURCE_CHROME = "::ERR"

    //----------


}
