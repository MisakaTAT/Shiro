package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

class CommonUtilsTest {

    @Test
    void atCheckTest() {
        val selfId = 123456789L;
        val arrayMsg = new ArrayList<ArrayMsg>();
        arrayMsg.add(new ArrayMsg().setType(MsgTypeEnum.at).setData(Map.of("qq", "123456789")));

        // 测试 AtEnum.NEED 的情况
        boolean actual1 = CommonUtils.atCheck(arrayMsg, selfId, AtEnum.NEED);
        assertFalse(actual1);

        // 测试 AtEnum.NOT_NEED 的情况
        boolean actual2 = CommonUtils.atCheck(arrayMsg, selfId, AtEnum.NOT_NEED);
        assertTrue(actual2);

        // 清空 arrayMsg 列表
        arrayMsg.clear();

        // 测试 AtEnum.NEED 的情况，但 selfId 不在 arrayMsg 列表中
        boolean actual3 = CommonUtils.atCheck(arrayMsg, selfId, AtEnum.NEED);
        assertTrue(actual3);

        // 测试 AtEnum.NOT_NEED 的情况，但 selfId 不在 arrayMsg 列表中
        boolean actual4 = CommonUtils.atCheck(arrayMsg, selfId, AtEnum.NOT_NEED);
        assertFalse(actual4);
    }

    @Test
    void extractMsgTest() {
        // 定义包含特殊格式字符串消息的变量
        val msg = "[CQ:at,qq=1122334455]测试消息1";

        // 将 msg 字符串转换成 ArrayMsg 数组
        val arrayMsg = ShiroUtils.rawToArrayMsg(msg);

        // 定义 AtEnum 为 NEED，期望 @ 标识被解析并去除
        val expected1 = "测试消息1";
        val actual1 = CommonUtils.msgExtract(msg, arrayMsg, AtEnum.NEED, 1122334455L);
        assertEquals(expected1, actual1);

        // 定义 AtEnum 为 NOT_NEED，期望原始消息内容不变
        val actual2 = CommonUtils.msgExtract(msg, arrayMsg, AtEnum.NOT_NEED, 1122334455L);
        assertEquals(msg, actual2);

        // 定义 AtEnum 为 NEED，但消息中未包含 @ 标识，期望原始消息内容不变
        val expected2 = "测试消息2";
        val actual3 = CommonUtils.msgExtract(expected2, arrayMsg, AtEnum.NEED, 1122334455L);
        assertEquals(expected2, actual3);

        // 定义 AtEnum 为 BOTH，期望 @ 标识被解析并去除
        val actual4 = CommonUtils.msgExtract(msg, arrayMsg, AtEnum.BOTH, 1122334455L);
        assertEquals(expected1, actual4);

        // 定义 AtEnum 为 BOTH，但消息中未包含 @ 标识，期望原始消息内容不变
        val actual5 = CommonUtils.msgExtract(expected2, arrayMsg, AtEnum.BOTH, 1122334455L);
        assertEquals(expected2, actual5);
    }

    @Test
    void atParseTest() {
        // 定义包含 @ 标识的 ArrayMsg 数组
        val arrayMsg1 = Arrays.asList(
                new ArrayMsg().setType(MsgTypeEnum.at).setData(Map.of("qq", "1122334455")),
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "测试消息"))
        );
        // 定义期望解析出的 @ 标识对应的 ArrayMsg
        val expected1 = arrayMsg1.get(0);
        // 调用 atParse 函数进行测试
        val actual1 = CommonUtils.atParse(arrayMsg1, 1122334455L);
        // 使用 assertEquals 函数比较期望值和实际值是否相等
        assertEquals(expected1, actual1);

        // 定义不包含 @ 标识的 ArrayMsg 数组
        val arrayMsg2 = Collections.singletonList(
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "测试消息"))
        );
        // 调用 atParse 函数进行测试，期望返回 null
        val actual2 = CommonUtils.atParse(arrayMsg2, 1122334455L);
        // 使用 assertNull 函数比较期望值和实际值是否相等
        assertNull(actual2);

        // 定义包含 @ 标识但不是机器人账号的 ArrayMsg 数组
        val arrayMsg3 = Arrays.asList(
                new ArrayMsg().setType(MsgTypeEnum.at).setData(Map.of("qq", "123456789")),
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "测试消息"))
        );
        // 调用 atParse 函数进行测试，期望返回 null
        val actual3 = CommonUtils.atParse(arrayMsg3, 1122334455L);
        // 使用 assertNull 函数比较期望值和实际值是否相等
        assertNull(actual3);
    }

    @Test
    void debugMsgDeleteBase64ContentTest() {
        val msg = "[CQ:image,file=\"base64://ABCDEFG\"][CQ:video,file=\"base64://1234567\"]";
        val expected = "[CQ:image,file=\"(base64)\"][CQ:video,file=\"(base64)\"]";
        String s = CommonUtils.debugMsgDeleteBase64Content(msg);
        assertEquals(expected, s);
    }

    @Test
    void startWithTest() throws NoSuchMethodException {
        // 测试startsWith能否正常运行
        MessageEvent event1 = new MessageEvent();
        event1.setSelfId(1L);
        event1.setMessage("qweAA");
        CheckResult checkResult = CommonUtils.allFilterCheck(
                event1,
                1L,
                this.getClass().getMethod("startWithAno").getAnnotation(MessageHandlerFilter.class)
        );
        Matcher matcher = checkResult.getMatcher();
        Assertions.assertEquals("AA", matcher.group(2));
    }

    @Test
    void endWithTest() throws NoSuchMethodException {
        // 测试endsWith能否正常运行
        MessageEvent event1 = new MessageEvent();
        event1.setSelfId(1L);
        event1.setMessage("DQddd");
        CheckResult checkResult = CommonUtils.allFilterCheck(
                event1,
                1L,
                this.getClass().getMethod("endWithAno").getAnnotation(MessageHandlerFilter.class)
        );
        Matcher matcher = checkResult.getMatcher();
        Assertions.assertEquals("DQ", matcher.group(1));
    }

    @Test
    void cmdTest() throws NoSuchMethodException {
        // 测试是否会干扰上面的cmd
        MessageEvent event1 = new MessageEvent();
        event1.setSelfId(1L);
        event1.setMessage("abc");
        CheckResult checkResult = CommonUtils.allFilterCheck(
                event1,
                1L,
                this.getClass().getMethod("cmdWithAno").getAnnotation(MessageHandlerFilter.class)
        );
        Matcher matcher = checkResult.getMatcher();
        Assertions.assertEquals("abc", matcher.group());
    }

    @MessageHandlerFilter(startWith = {"abc", "qwe"})
    public void startWithAno() {
    }

    @MessageHandlerFilter(endWith = {"ddd", "rty"})
    public void endWithAno() {
    }

    @MessageHandlerFilter(cmd = "abc|qwe", startWith = {"ab", "qwe"})
    public void cmdWithAno() {
    }
}