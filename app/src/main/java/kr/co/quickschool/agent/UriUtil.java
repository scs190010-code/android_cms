package kr.co.quickschool.agent;

import java.net.URLEncoder;

public final class UriUtil {
    private UriUtil() {}
    public static String enc(String s) {
        try { return URLEncoder.encode(s == null ? "" : s, "UTF-8"); }
        catch (Exception e) { return ""; }
    }
}
