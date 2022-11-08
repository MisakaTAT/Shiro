package com.mikuac.shiro.common.utils;

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
     * <p>builder.</p>
     *
     * @return {@link com.mikuac.shiro.common.utils.OneBotMedia}
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
        stringBuilder.append("file=").append(this.file);
        if (this.cache != null) {
            stringBuilder.append(",cache=").append(this.cache ? 1 : 0);
        }
        if (this.proxy != null) {
            stringBuilder.append(",proxy=").append(this.proxy ? 1 : 0);
        }
        if (this.timeout != null) {
            stringBuilder.append(",timeout=").append(this.timeout);
        }
        return stringBuilder.toString();
    }

    /**
     * <p>file.</p>
     *
     * @param file 文件
     * @return {@link com.mikuac.shiro.common.utils.OneBotMedia}
     */
    public OneBotMedia file(String file) {
        this.file = ShiroUtils.escape(file);
        return this;
    }

    /**
     * <p>cache.</p>
     *
     * @param cache 缓存
     * @return {@link com.mikuac.shiro.common.utils.OneBotMedia}
     */
    public OneBotMedia cache(boolean cache) {
        this.cache = cache;
        return this;
    }

    /**
     * <p>proxy.</p>
     *
     * @param proxy 代理
     * @return {@link com.mikuac.shiro.common.utils.OneBotMedia}
     */
    public OneBotMedia proxy(boolean proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * <p>timeout.</p>
     *
     * @param timeout 超时
     * @return {@link com.mikuac.shiro.common.utils.OneBotMedia}
     */
    public OneBotMedia timeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

}
