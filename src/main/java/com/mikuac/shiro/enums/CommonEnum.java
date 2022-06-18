package com.mikuac.shiro.enums;

/**
 * @author Zero
 */
public enum CommonEnum {

    /**
     * default command value
     */
    DEFAULT_CMD("none"),

    /**
     * set
     */
    SET("set"),

    /**
     * unset
     */
    UNSET("unset"),

    /**
     * group
     */
    GROUP("group");

    private final String value;

    /**
     * 枚举构造函数
     *
     * @param value enum value
     */
    CommonEnum(String value) {
        this.value = value;
    }

    /**
     * get enum value
     *
     * @return value
     */
    public String value() {
        return this.value;
    }

}
