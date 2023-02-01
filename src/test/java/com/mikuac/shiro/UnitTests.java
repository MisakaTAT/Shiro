package com.mikuac.shiro;

import com.mikuac.shiro.common.limit.RateLimiter;
import com.mikuac.shiro.common.utils.*;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.exception.ShiroException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

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
        val rawMsg = "[CQ:at,qq=1122334455]测试消息1[CQ:face,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test.image,url=https://test.com/2.jpg]\n[CQ:image,file=test.image,url=https://test.com/2.jpg]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(rawMsg);
        Assertions.assertNotNull(arrayMsg);
        int[] count = {0};
        arrayMsg.forEach(InternalUtils.consumerWithIndex((item, index) -> {
            if (MsgTypeEnum.at == item.getType() && index == 0) {
                count[0] = count[0] + 1;
                Assertions.assertEquals("1122334455", item.getData().get("qq"));
            }
            if (MsgTypeEnum.text == item.getType() && index == 1) {
                count[0] = count[0] + 1;
                Assertions.assertEquals("测试消息1", item.getData().get("text"));
            }
            if (MsgTypeEnum.face == item.getType() && index == 2) {
                count[0] = count[0] + 1;
                Assertions.assertEquals("1", item.getData().get("id"));
            }
            if (MsgTypeEnum.text == item.getType() && index == 3) {
                count[0] = count[0] + 1;
                Assertions.assertEquals("测试消息2", item.getData().get("text"));
            }
            if (MsgTypeEnum.video == item.getType() && index == 4) {
                count[0] = count[0] + 1;
                Assertions.assertEquals("https://test.com/1.mp4", item.getData().get("file"));
            }
            if (MsgTypeEnum.image == item.getType() && index == 5) {
                count[0] = count[0] + 1;
                Assertions.assertEquals("test.image", item.getData().get("file"));
                Assertions.assertEquals("https://test.com/2.jpg", item.getData().get("url"));
            }
            if (MsgTypeEnum.text == item.getType() && index == 6) {
                count[0] = count[0] + 1;
                Assertions.assertEquals("\n", item.getData().get("text"));
            }
            if (MsgTypeEnum.image == item.getType() && index == 7) {
                count[0] = count[0] + 1;
                Assertions.assertEquals("test.image", item.getData().get("file"));
                Assertions.assertEquals("https://test.com/2.jpg", item.getData().get("url"));
            }
        }));
        Assertions.assertEquals(8, count[0]);
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
            throw new ShiroException("test");
        } catch (ShiroException e) {
            Assertions.assertEquals("test", e.getMessage());
        }
    }

}
