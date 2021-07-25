package com.example.wiki.util;


import java.io.Serializable;

/**
 * @description 在线程本地变量中存取值
 */
public class RequestContext implements Serializable {

    /**
     * 远程地址
     */
    private static ThreadLocal<String> remoteAddr = new ThreadLocal<>();

    public static String getRemoteAddr() {
        return remoteAddr.get();
    }

    public static void setRemoteAddr(String remoteAddr) {
        RequestContext.remoteAddr.set(remoteAddr);
    }

}
