package com.mikuac.shiro;

import com.mikuac.shiro.common.limit.RateLimiter;
import com.mikuac.shiro.common.utils.*;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.enums.MsgTypeEnum;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * <p>UnitTests class.</p>
 *
 * @author zero
 * @version $Id: $Id
 * @since 1.3.7
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UnitTests {

    private BotFactory botFactory;

    @Autowired
    public void setBotFactory(BotFactory botFactory) {
        this.botFactory = botFactory;
    }

    private RateLimiter rateLimiter;

    @Autowired
    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Test
    public void testRateLimiter() {
        val a = rateLimiter.tryAcquire(4);
        TestCase.assertTrue(a);
        val b = rateLimiter.acquire(5);
        TestCase.assertTrue(b);
    }

    @Test
    public void testRawToArrayMsg() {
        val rawMsg = "[CQ:at,qq=1122334455]测试消息1[CQ:face,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test.image,url=https://test.com/2.jpg]\n[CQ:image,file=test.image,url=https://test.com/2.jpg]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(rawMsg);
        TestCase.assertNotNull(arrayMsg);
        int[] count = {0};
        arrayMsg.forEach(InternalUtils.consumerWithIndex((item, index) -> {
            if (MsgTypeEnum.at == item.getType() && index == 0) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("1122334455", item.getData().get("qq"));
            }
            if (MsgTypeEnum.text == item.getType() && index == 1) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("测试消息1", item.getData().get("text"));
            }
            if (MsgTypeEnum.face == item.getType() && index == 2) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("1", item.getData().get("id"));
            }
            if (MsgTypeEnum.text == item.getType() && index == 3) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("测试消息2", item.getData().get("text"));
            }
            if (MsgTypeEnum.video == item.getType() && index == 4) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("https://test.com/1.mp4", item.getData().get("file"));
            }
            if (MsgTypeEnum.image == item.getType() && index == 5) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("test.image", item.getData().get("file"));
                TestCase.assertEquals("https://test.com/2.jpg", item.getData().get("url"));
            }
            if (MsgTypeEnum.text == item.getType() && index == 6) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("\n", item.getData().get("text"));
            }
            if (MsgTypeEnum.image == item.getType() && index == 7) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("test.image", item.getData().get("file"));
                TestCase.assertEquals("https://test.com/2.jpg", item.getData().get("url"));
            }
        }));
        TestCase.assertEquals(8, count[0]);
    }

    @Test
    public void testMsgUtils() {
        val build = MsgUtils.builder().at(1122334455L).text("Hello").img("https://test.com/1.jpg");
        val msg = "[CQ:at,qq=1122334455]Hello[CQ:image,file=https://test.com/1.jpg]";
        TestCase.assertEquals(msg, build.build());
    }

    @Test
    public void testIsAtAll() {
        val atAll = "[CQ:at,qq=all]";
        val atAllArrayMsg = ShiroUtils.rawToArrayMsg(atAll);
        TestCase.assertNotNull(atAllArrayMsg);
        val isAtAll = ShiroUtils.isAtAll(atAllArrayMsg);
        TestCase.assertTrue(isAtAll);

        val atUser = "[CQ:at,qq=1122334455]";
        val atUserArrayMsg = ShiroUtils.rawToArrayMsg(atUser);
        TestCase.assertNotNull(atUserArrayMsg);
        val notAtAll = ShiroUtils.isAtAll(atUserArrayMsg);
        TestCase.assertFalse(notAtAll);
    }

    @Test
    public void testGetAtList() {
        val stringMsg = "[CQ:at,qq=11111][CQ:at,qq=22222][CQ:at,qq=33333]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        TestCase.assertNotNull(arrayMsg);
        val atList = ShiroUtils.getAtList(arrayMsg);
        TestCase.assertEquals(3, atList.size());
    }

    @Test
    public void testGetMsgImgUrlList() {
        val stringMsg = "[CQ:image,file=https://test.com/2.jpg][CQ:image,file=https://test.com/2.jpg]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        TestCase.assertNotNull(arrayMsg);
        val imgUrlList = ShiroUtils.getMsgImgUrlList(arrayMsg);
        TestCase.assertEquals(2, imgUrlList.size());
    }

    @Test
    public void testGetMsgVideoUrlList() {
        val stringMsg = "[CQ:video,file=https://test.com/1.mp4][CQ:video,file=https://test.com/2.mp4]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        TestCase.assertNotNull(arrayMsg);
        val videoUrlList = ShiroUtils.getMsgVideoUrlList(arrayMsg);
        TestCase.assertEquals(2, videoUrlList.size());
    }

    @Test
    public void testGetNickname() {
        val nickname = ShiroUtils.getNickname(1140667337L);
        TestCase.assertNotNull(nickname);
        TestCase.assertEquals("Zero", nickname);
    }

    @Test
    public void testDebugMode() {
        TestCase.assertTrue(log.isDebugEnabled());
    }

    @Test
    public void testOneBotMedia() {
        val media = OneBotMedia.builder().file("https://a.com/1.jpg").cache(false).proxy(false).timeout(10);
        val msg = MsgUtils.builder().img(media).build();
        val stringMsg = "[CQ:image,file=https://a.com/1.jpg,cache=0,proxy=0,timeout=10]";
        TestCase.assertEquals(stringMsg, msg);
    }

    @Test
    public void testBotFactory() {
        val selfId = 1140667337L;
        val bot = botFactory.createBot(selfId, null);
        TestCase.assertNotNull(bot);
        TestCase.assertEquals(selfId, bot.getSelfId());
    }

    @Test
    public void testScanAnnotation() {
        val annotations = new ScanUtils().scanAnnotation("com.mikuac.shiro.annotation");
        annotations.forEach(a -> TestCase.assertTrue(a.isAnnotation()));
    }

    @Test
    public void testConsumerWithIndex() {
        val list = Arrays.asList(1, 2, 3);
        list.forEach(InternalUtils.consumerWithIndex((item, index) -> TestCase.assertEquals(item, list.get(index))));
    }

    @Test
    public void testArrayMsgToCode() {
        val raw = "[CQ:at,qq=1122334455]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(raw);
        val cqCode = ShiroUtils.arrayMsgToCode(arrayMsg.get(0));
        TestCase.assertEquals(raw, cqCode);
    }

}
