package com.mikuac.shiro.annotation;

import com.mikuac.shiro.enums.AtEnum;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.enums.ReplyEnum;

import java.lang.annotation.*;

import static com.mikuac.shiro.common.utils.CommonUtils.CMD_DEFAULT_VALUE;

/**
 * 此注解仅与 @xxxMessageHandler 一起使用才有效
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageHandlerFilter {

    /**
     * 触发命令，支持正则
     * 注: 仅用于消息校验, 不会返回 matcher (理论上可以做到, 但是会冲突
     *
     * @return 正则表达式
     */
    String cmd() default CMD_DEFAULT_VALUE;

    /**
     * 检查是否被at
     * 如果值为 NEED        只处理带有at机器人的消息
     * 如果值为 NOT_NEED    若消息中at了机器人此条消息会被忽略
     *
     * @return at 枚举
     */
    AtEnum at() default AtEnum.OFF;

    /**
     * 检测是否包含回复
     * OFF              不处理
     * NONE             不包括回复
     * REPLY_ME         回复 bot 的消息
     * REPLY_OTHER      回复任意其他人的消息
     * REPLY_ALL        任意包括回复的消息
     *
     * @return reply 枚举
     */
    ReplyEnum reply() default ReplyEnum.OFF;

    /**
     * 消息中包含某一类型的
     * 注0: reply 如果设为 REPLY_XXX, types 默认增加一条额外的 type.reply, types 为空不受影响
     * 注1: 若 reply 为 NONE, types 包含 type.reply, 则本规则的 type.reply 条件无效
     */
    MsgTypeEnum[] types() default {};

    /**
     * 仅注解指明的群组会触发, 如果为空则任意群组都可以触发
     * 注, 私聊消息无效
     *
     * @return 群组 ID
     */
    long[] groups() default {};

    /**
     * 仅注解指明的 qq 发送会触发, 如果为空则任意消息都可以触发
     * 当 OneBot 实现会上报 {@code message_sent} 事件时，本过滤器同样适用于机器人自身发送的消息；
     * 默认情况下机器人自身发送的消息会被过滤，除非在 {@code senders} 数组中显式包含了机器人的 QQ 号
     *
     * @return 发送者 QQ 号
     */
    long[] senders() default {};

    /**
     * 若指明前缀, 则仅消息头部匹配前缀的消息才可以触发, 判断条件为or, 如果为空则任意消息都可以触发
     *
     * @return 前缀, 可多选
     */
    String[] startWith() default {};

    /**
     * 若指明后缀, 则仅消息尾部匹配后缀的消息才可以触发, 判断条件为or, 如果为空则任意消息都可以触发
     *
     * @return 后缀缀, 可多选
     */
    String[] endWith() default {};

    /**
     * 将过滤器反转, 即所有**不为默认值/非空**的过滤条件反转, 当某条件未设置时反转无效
     * 例如 指明`senders`后, 只有指明的 qq 发送的消息会触发, 反转过滤器后, 指明的 qq 则不会触发
     *
     * @return true 则反转
     */
    boolean invert() default false;

    /**
     * 仅匹配纯文本消息, 即不包含任何 CQ 码的消息，默认开启
     * @return true 仅匹配纯文本消息
     */
    boolean matchPlainText() default true;

}
