// SimpleCookieJar.java
package classes;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SimpleCookieJar implements CookieJar {
    private final CookieManager cookieManager;

    public SimpleCookieJar() {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            try {
                cookieManager.getCookieStore().add(
                        new URI(url.host()),
                        new java.net.HttpCookie(cookie.name(), cookie.value())
                );
            } catch (Exception ignored) {}
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> result = new ArrayList<>();
        List<java.net.HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        for (java.net.HttpCookie httpCookie : cookies) {
            result.add(new Cookie.Builder()
                    .name(httpCookie.getName())
                    .value(httpCookie.getValue())
                    .domain(url.host())
                    .build());
        }
        return result;
    }
}