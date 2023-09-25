package com.mikuac.shiro.enums;

/**
 * <p>CommonEnum class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
public enum CommonEnum {

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
    GROUP("group"),

    /**
     * at all
     */
    AT_ALL("all");

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
