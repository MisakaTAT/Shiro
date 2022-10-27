package com.mikuac.shiro.common.utils;

import lombok.val;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * <p>NetUtils class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
public class NetUtils {

    /**
     * get request
     *
     * @param url         url
     * @param charsetName charset
     * @return {@link java.lang.String}
     */
    public static String get(String url, String charsetName) {
        val result = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            val connection = new URL(url).openConnection();
            connection.connect();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charsetName));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
