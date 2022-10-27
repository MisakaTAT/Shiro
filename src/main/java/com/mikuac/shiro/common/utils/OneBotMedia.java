package com.mikuac.shiro.common.utils;

/**
 * <p>OneBotMedia class.</p>
 *
 * @author Zhongren233
 * @version $Id: $Id
 */
public class OneBotMedia {

    private final String file;
    private final Boolean cache;
    private final Boolean proxy;
    private final Integer timeout;

    /**
     * 构造函数
     *
     * @param builder {@link com.mikuac.shiro.common.utils.OneBotMedia.Builder}
     */
    public OneBotMedia(Builder builder) {
        this.file = builder.file;
        this.cache = builder.cache;
        this.proxy = builder.proxy;
        this.timeout = builder.timeout;
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
     * 构造器
     */
    public static class Builder {
        private String file = "";
        private Boolean cache;
        private Boolean proxy;
        private Integer timeout;

        /**
         * @param file 文件
         * @return {@link Builder}
         */
        public Builder file(String file) {
            this.file = ShiroUtils.escape(file);
            return this;
        }

        /**
         * @param cache 缓存
         * @return {@link Builder}
         */
        public Builder cache(boolean cache) {
            this.cache = cache;
            return this;
        }

        /**
         * @param proxy 代理
         * @return {@link Builder}
         */
        public Builder proxy(boolean proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * @param timeout 超时
         * @return {@link Builder}
         */
        public Builder timeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * @return {@link OneBotMedia}
         */
        public OneBotMedia build() {
            return new OneBotMedia(this);
        }

    }

}
