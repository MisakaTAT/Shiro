package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiConsumer;
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
        val msg = "[CQ:at,qq=1122334455,name=@机器人 &#91;可以&#93;]测试消息1";

        // 将 msg 字符串转换成 ArrayMsg 数组
        val arrayMsg = MessageConverser.stringToArray(msg);

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
    void atParseTest_LastNotText() {
        long selfId = 1122334455L;

        BiConsumer<List<ArrayMsg>, ArrayMsg> assertAtParse = (messages, expected) -> {
            ArrayMsg actual = CommonUtils.atParse(messages, selfId);
            if (expected == null) {
                assertNull(actual);
            } else {
                assertEquals(expected, actual);
            }
        };

        List<ArrayMsg> arrayMsg1 = Arrays.asList(
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "开头文本")),
                new ArrayMsg().setType(MsgTypeEnum.at).setData(Map.of("qq", "1122334455")),
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "中间文本")),
                new ArrayMsg().setType(MsgTypeEnum.face).setData(Map.of("id", 1234))
        );
        assertAtParse.accept(arrayMsg1, arrayMsg1.get(1));

        List<ArrayMsg> arrayMsg2 = Arrays.asList(
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "开头文本")),
                new ArrayMsg().setType(MsgTypeEnum.at).setData(Map.of("qq", "999999999")), // 错误 QQ
                new ArrayMsg().setType(MsgTypeEnum.face).setData(Map.of("id", 1234))
        );
        assertAtParse.accept(arrayMsg2, null);

        List<ArrayMsg> arrayMsg3 = Arrays.asList(
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "开头文本")),
                new ArrayMsg().setType(MsgTypeEnum.at).setData(Map.of("qq", "1122334455")),
                new ArrayMsg().setType(MsgTypeEnum.music).setData(Map.of("url", "voice.mp3"))
        );
        assertAtParse.accept(arrayMsg3, arrayMsg3.get(1));
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
        // empty
    }

    @MessageHandlerFilter(endWith = {"ddd", "rty"})
    public void endWithAno() {
        // empty
    }

    @MessageHandlerFilter(cmd = "abc|qwe", startWith = {"ab", "qwe"})
    public void cmdWithAno() {
        // empty
    }
}