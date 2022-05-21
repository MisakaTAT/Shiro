package com.mikuac.shiro.common.utils;

/**
 * @author Zhongren233
 */
public class OneBotMedia {

    private final String file;
    private final Boolean cache;
    private final Boolean proxy;
    private final Integer timeout;

    public OneBotMedia(Builder builder) {
        this.file = builder.file;
        this.cache = builder.cache;
        this.proxy = builder.proxy;
        this.timeout = builder.timeout;
    }

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

    public static class Builder {
        private String file = "";
        private Boolean cache;
        private Boolean proxy;
        private Integer timeout;

        public Builder file(String file) {
            this.file = ShiroUtils.escape(file);
            return this;
        }

        public Builder cache(boolean cache) {
            this.cache = cache;
            return this;
        }

        public Builder proxy(boolean proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder timeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public OneBotMedia build() {
            return new OneBotMedia(this);
        }

    }

}
