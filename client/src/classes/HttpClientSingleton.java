// HttpClientSingleton.java
package classes;

import okhttp3.OkHttpClient;

public class HttpClientSingleton {
    private static final OkHttpClient INSTANCE = new OkHttpClient.Builder()
            .cookieJar(new SimpleCookieJar())
            .build();

    public static OkHttpClient getInstance() {
        return INSTANCE;
    }
}