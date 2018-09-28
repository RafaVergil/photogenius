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

    const val INSTAGRAM_API_GET_ACCESS_TOKEN_URL = "https://api.instagram.com/oauth/authorize/" +
            "?client_id=%s&redirect_uri=%s&response_type=token"

    const val INSTAGRAM_API_CLIENT_ID = "61f5845972784b6d84224beb48b6a6d6"
    const val INSTAGRAM_API_REDIRECT_URL = "http://%s"
}
