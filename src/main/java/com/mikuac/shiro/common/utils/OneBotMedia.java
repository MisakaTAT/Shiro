package com.mikuac.shiro.common.utils;

import java.util.Map;

/**
 * <p>OneBotMedia class.</p>
 *
 * @author Zhongren233
 * @version $Id: $Id
 */
public class OneBotMedia {

    /**
     * 文件
     */
    private String file;

    /**
     * 是否使用缓存
     */
    private Boolean cache;

    /**
     * 是否启用代理
     */
    private Boolean proxy;

    /**
     * 超时
     */
    private Integer timeout;

    /**
     * 自定义文本
     */
    private String summary;

    /**
     * <p>builder.</p>
     *
     * @return {@link OneBotMedia}
     */
    public static OneBotMedia builder() {
        return new OneBotMedia();
    }

    /**
     * <p>escape.</p>
     *
     * @return media code params
     */
    public String escape() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("file=").append(ShiroUtils.escape(this.file));
        if (this.cache != null) {
            stringBuilder.append(",cache=").append(Boolean.TRUE.equals(this.cache) ? 1 : 0);
        }
        if (this.proxy != null) {
            stringBuilder.append(",proxy=").append(Boolean.TRUE.equals(this.proxy) ? 1 : 0);
        }
        if (this.timeout != null) {
            stringBuilder.append(",timeout=").append(this.timeout);
        }
        if (this.summary != null) {
            stringBuilder.append(",summary=").append(ShiroUtils.escape(this.summary));
        }
        return stringBuilder.toString();
    }

    public void escape(Map<String, String> map) {
        map.put("file", this.file);
        if (this.cache != null) {
            map.put("cache", Boolean.TRUE.equals(this.cache) ? "1" : "0");
        }
        if (this.proxy != null) {
            map.put("proxy", Boolean.TRUE.equals(this.proxy) ? "1" : "0");
        }
        if (this.timeout != null) {
            map.put("timeout", this.timeout.toString());
        }
        if (this.summary != null) {
            map.put("summary", this.summary);
        }
    }

    /**
     * <p>file.</p>
     *
     * @param file 文件
     * @return {@link OneBotMedia}
     */
    public OneBotMedia file(String file) {
        this.file = file;
        return this;
    }

    /**
     * <p>cache.</p>
     *
     * @param cache 缓存
     * @return {@link OneBotMedia}
     */
    public OneBotMedia cache(boolean cache) {
        this.cache = cache;
        return this;
    }

    /**
     * <p>proxy.</p>
     *
     * @param proxy 代理
     * @return {@link OneBotMedia}
     */
    public OneBotMedia proxy(boolean proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * <p>timeout.</p>
     *
     * @param timeout 超时
     * @return {@link OneBotMedia}
     */
    public OneBotMedia timeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * <p>summary.</p>
     *
     * @param summary 自定义文本
     * @return {@link OneBotMedia}
     */
    public OneBotMedia summary(String summary) {
        this.summary = summary;
        return this;
    }

}
