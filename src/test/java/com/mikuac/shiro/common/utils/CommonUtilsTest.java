package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

class CommonUtilsTest {

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
    public void startWithAno() {}

    @MessageHandlerFilter(endWith = {"ddd", "rty"})
    public void endWithAno() {}

    @MessageHandlerFilter(cmd = "abc|qwe", startWith = {"ab", "qwe"})
    public void cmdWithAno() {}
}