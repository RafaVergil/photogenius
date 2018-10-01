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
    const val INSTAGRAM_API_GET_ACCESS_TOKEN_URL = "oauth/access_token"

    const val INSTAGRAM_API_GET_CODE_URL = "https://www.instagram.com/oauth/authorize/?" +
            "client_id=%s" +
            "&redirect_uri=%s" +
            "&response_type=code"

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

    //KEYS
    const val KEY_INSTAGRAM_API_GET_TOKEN_CLIENT_ID = "client_id"
    const val KEY_INSTAGRAM_API_GET_TOKEN_CLIENT_SECRET = "client_secret"
    const val KEY_INSTAGRAM_API_GET_TOKEN_GRANT_TYPE = "grant_type"
    const val KEY_INSTAGRAM_API_GET_TOKEN_REDIRECT_URI = "redirect_uri"
    const val KEY_INSTAGRAM_API_GET_TOKEN_CODE = "code"
    //----------

    //VALUES
    const val VALUE_INSTAGRAM_API_GRANT_TYPE = "authorization_code"
    //----------

}
