package com.mikuac.shiro;

import com.mikuac.shiro.common.limit.RateLimiter;
import com.mikuac.shiro.common.utils.*;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.enums.AtEnum;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.exception.ShiroException;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * <p>UnitTests class.</p>
 *
 * @author zero
 * @version $Id: $Id
 * @since 1.3.7
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UnitTests {

    @Autowired
    private BotFactory botFactory;

    @Autowired
    private BotContainer botContainer;

    @Autowired
    private RateLimiter rateLimiter;

    @Test
    void testRateLimiter() {
        val a = rateLimiter.tryAcquire(1);
        Assertions.assertTrue(a);
        val b = rateLimiter.acquire(4);
        Assertions.assertTrue(b);
    }

    @Test
    void testRawToArrayMsg() {
        val msg = "[CQ:at,qq=1122334455]测试消息1[CQ:face,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test1.image,url=https://test.com/1.jpg]\n[CQ:image,file=test2.image,url=https://test.com/2.jpg]";
        val expected = Arrays.asList(
                new ArrayMsg().setType(MsgTypeEnum.at).setData(Map.of("qq", "1122334455")),
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "测试消息1")),
                new ArrayMsg().setType(MsgTypeEnum.face).setData(Map.of("id", "1")),
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "测试消息2")),
                new ArrayMsg().setType(MsgTypeEnum.video).setData(Map.of("file", "https://test.com/1.mp4")),
                new ArrayMsg().setType(MsgTypeEnum.image).setData(Map.of("file", "test1.image", "url", "https://test.com/1.jpg")),
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "\n")),
                new ArrayMsg().setType(MsgTypeEnum.image).setData(Map.of("file", "test2.image", "url", "https://test.com/2.jpg"))
        );
        val actual = ShiroUtils.rawToArrayMsg(msg);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testMsgUtils() {
        val build = MsgUtils.builder().at(1122334455L).text("Hello").img("https://test.com/1.jpg");
        val msg = "[CQ:at,qq=1122334455]Hello[CQ:image,file=https://test.com/1.jpg]";
        Assertions.assertEquals(msg, build.build());
    }

    @Test
    void testIsAtAll() {
        val atAll = "[CQ:at,qq=all]";
        val atAllArrayMsg = ShiroUtils.rawToArrayMsg(atAll);
        Assertions.assertNotNull(atAllArrayMsg);
        val isAtAll = ShiroUtils.isAtAll(atAllArrayMsg);
        Assertions.assertTrue(isAtAll);

        val atUser = "[CQ:at,qq=1122334455]";
        val atUserArrayMsg = ShiroUtils.rawToArrayMsg(atUser);
        Assertions.assertNotNull(atUserArrayMsg);
        val notAtAll = ShiroUtils.isAtAll(atUserArrayMsg);
        Assertions.assertFalse(notAtAll);
    }

    @Test
    void testGetAtList() {
        val stringMsg = "[CQ:at,qq=11111][CQ:at,qq=22222][CQ:at,qq=33333]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        Assertions.assertNotNull(arrayMsg);
        val atList = ShiroUtils.getAtList(arrayMsg);
        Assertions.assertEquals(3, atList.size());
    }

    @Test
    void testGetMsgImgUrlList() {
        val stringMsg = "[CQ:image,file=https://test.com/2.jpg][CQ:image,file=https://test.com/2.jpg]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        Assertions.assertNotNull(arrayMsg);
        val imgUrlList = ShiroUtils.getMsgImgUrlList(arrayMsg);
        Assertions.assertEquals(2, imgUrlList.size());
    }

    @Test
    void testGetMsgVideoUrlList() {
        val stringMsg = "[CQ:video,file=https://test.com/1.mp4][CQ:video,file=https://test.com/2.mp4]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        Assertions.assertNotNull(arrayMsg);
        val videoUrlList = ShiroUtils.getMsgVideoUrlList(arrayMsg);
        Assertions.assertEquals(2, videoUrlList.size());
    }

    @Test
    void testGetNickname() {
        val nickname = ShiroUtils.getNickname(1140667337L);
        Assertions.assertNotNull(nickname);
        Assertions.assertEquals("Zero", nickname);
    }

    @Test
    void testDebugMode() {
        Assertions.assertTrue(log.isDebugEnabled());
    }

    @Test
    void testOneBotMedia() {
        val media = OneBotMedia.builder().file("https://a.com/1.jpg").cache(false).proxy(false).timeout(10);
        val msg = MsgUtils.builder().img(media).build();
        val stringMsg = "[CQ:image,file=https://a.com/1.jpg,cache=0,proxy=0,timeout=10]";
        Assertions.assertEquals(stringMsg, msg);
    }

    @Test
    void testBotFactory() {
        val selfId = 1140667337L;
        val bot = botFactory.createBot(selfId, null);
        Assertions.assertNotNull(bot);
        Assertions.assertEquals(selfId, bot.getSelfId());
    }

    @Test
    void testScanAnnotation() {
        val annotations = new ScanUtils().scanAnnotation("com.mikuac.shiro.annotation");
        annotations.forEach(a -> Assertions.assertTrue(a.isAnnotation()));
    }

    @Test
    void testConsumerWithIndex() {
        val list = Arrays.asList(1, 2, 3);
        list.forEach(InternalUtils.consumerWithIndex((item, index) -> Assertions.assertEquals(item, list.get(index))));
    }

    @Test
    void testArrayMsgToCode() {
        val raw = "[CQ:at,qq=1122334455]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(raw);
        val cqCode = ShiroUtils.arrayMsgToCode(arrayMsg.get(0));
        Assertions.assertEquals(raw, cqCode);
    }

    @Test
    void testBotContainer() {
        val selfId = 1140667337L;
        botContainer.robots.put(selfId, botFactory.createBot(selfId, null));
        val bot = botContainer.robots.get(selfId);
        Assertions.assertEquals(selfId, bot.getSelfId());
    }

    @Test
    void testShiroException() {
        try {
            // 抛出自定义异常
            throw new ShiroException("test");
        } catch (ShiroException e) {
            // 捕获并比较异常信息是否一致
            Assertions.assertEquals("test", e.getMessage());
        }
    }

    @Test
    void testAtCheck() {
        val selfId = 123456789L;
        val arrayMsg = new ArrayList<ArrayMsg>();
        arrayMsg.add(new ArrayMsg().setType(MsgTypeEnum.at).setData(Map.of("qq", "123456789")));

        // 测试 AtEnum.NEED 的情况
        boolean actual1 = CommonUtils.atCheck(arrayMsg, selfId, AtEnum.NEED);
        Assertions.assertFalse(actual1);

        // 测试 AtEnum.NOT_NEED 的情况
        boolean actual2 = CommonUtils.atCheck(arrayMsg, selfId, AtEnum.NOT_NEED);
        Assertions.assertTrue(actual2);

        // 清空 arrayMsg 列表
        arrayMsg.clear();

        // 测试 AtEnum.NEED 的情况，但 selfId 不在 arrayMsg 列表中
        boolean actual3 = CommonUtils.atCheck(arrayMsg, selfId, AtEnum.NEED);
        Assertions.assertTrue(actual3);

        // 测试 AtEnum.NOT_NEED 的情况，但 selfId 不在 arrayMsg 列表中
        boolean actual4 = CommonUtils.atCheck(arrayMsg, selfId, AtEnum.NOT_NEED);
        Assertions.assertFalse(actual4);
    }

    @Test
    void testExtractMsg() {
        // 定义包含特殊格式字符串消息的变量
        val msg = "[CQ:at,qq=1122334455]测试消息1";

        // 将 msg 字符串转换成 ArrayMsg 数组
        val arrayMsg = ShiroUtils.rawToArrayMsg(msg);

        // 定义 AtEnum 为 NEED，期望 At 标识被解析并去除
        val expected1 = "测试消息1";
        val actual1 = CommonUtils.extractMsg(msg, arrayMsg, AtEnum.NEED, 1122334455L);
        Assertions.assertEquals(expected1, actual1);

        // 定义 AtEnum 为 NOT_NEED，期望原始消息内容不变
        val actual2 = CommonUtils.extractMsg(msg, arrayMsg, AtEnum.NOT_NEED, 1122334455L);
        Assertions.assertEquals(msg, actual2);

        // 定义 AtEnum 为 NEED，但消息中未包含 At 标识，期望原始消息内容不变
        val expected2 = "测试消息2";
        val actual3 = CommonUtils.extractMsg(expected2, arrayMsg, AtEnum.NEED, 1122334455L);
        Assertions.assertEquals(expected2, actual3);
    }

    @Test
    void testParseAt() {
        // 定义包含 At 标识的 ArrayMsg 数组
        val arrayMsg1 = Arrays.asList(
                new ArrayMsg().setType(MsgTypeEnum.at).setData(Map.of("qq", "1122334455")),
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "测试消息"))
        );
        // 定义期望解析出的 At 标识对应的 ArrayMsg
        val expected1 = arrayMsg1.get(0);
        // 调用 parseAt 函数进行测试
        val actual1 = CommonUtils.parseAt(arrayMsg1, 1122334455L);
        // 使用 assertEquals 函数比较期望值和实际值是否相等
        Assertions.assertEquals(expected1, actual1);

        // 定义不包含 At 标识的 ArrayMsg 数组
        val arrayMsg2 = Collections.singletonList(
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "测试消息"))
        );
        // 调用 parseAt 函数进行测试，期望返回 null
        val actual2 = CommonUtils.parseAt(arrayMsg2, 1122334455L);
        // 使用 assertNull 函数比较期望值和实际值是否相等
        Assertions.assertNull(actual2);

        // 定义包含 At 标识但不是机器人账号的 ArrayMsg 数组
        val arrayMsg3 = Arrays.asList(
                new ArrayMsg().setType(MsgTypeEnum.at).setData(Map.of("qq", "123456789")),
                new ArrayMsg().setType(MsgTypeEnum.text).setData(Map.of("text", "测试消息"))
        );
        // 调用 parseAt 函数进行测试，期望返回 null
        val actual3 = CommonUtils.parseAt(arrayMsg3, 1122334455L);
        // 使用 assertNull 函数比较期望值和实际值是否相等
        Assertions.assertNull(actual3);
    }

}
