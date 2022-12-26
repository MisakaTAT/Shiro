package com.mikuac.shiro;

import com.mikuac.shiro.common.limit.RateLimiter;
import com.mikuac.shiro.common.utils.InternalUtils;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.OneBotMedia;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.enums.MsgTypeEnum;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

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

    @Resource
    private RateLimiter rateLimiter;

    /**
     * <p>rateLimiterTest.</p>
     */
    @Test
    public void rateLimiterTest() {
        val a = rateLimiter.tryAcquire(4);
        TestCase.assertTrue(a);
        val b = rateLimiter.acquire(5);
        TestCase.assertTrue(b);
    }

    /**
     * <p>stringToArrayMsgTest.</p>
     */
    @Test
    public void rawToArrayMsgTest() {
        val rawMsg = "[CQ:at,qq=1122334455]测试消息1[CQ:face,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test.image,url=https://test.com/2.jpg]\n[CQ:image,file=test.image,url=https://test.com/2.jpg]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(rawMsg);
        TestCase.assertNotNull(arrayMsg);
        int[] count = {0};
        arrayMsg.forEach(InternalUtils.consumerWithIndex((item, index) -> {
            if (MsgTypeEnum.AT == item.getType() && index == 0) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("1122334455", item.getData().get("qq"));
            }
            if (MsgTypeEnum.TEXT == item.getType() && index == 1) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("测试消息1", item.getData().get("text"));
            }
            if (MsgTypeEnum.FACE == item.getType() && index == 2) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("1", item.getData().get("id"));
            }
            if (MsgTypeEnum.TEXT == item.getType() && index == 3) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("测试消息2", item.getData().get("text"));
            }
            if (MsgTypeEnum.VIDEO == item.getType() && index == 4) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("https://test.com/1.mp4", item.getData().get("file"));
            }
            if (MsgTypeEnum.IMAGE == item.getType() && index == 5) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("test.image", item.getData().get("file"));
                TestCase.assertEquals("https://test.com/2.jpg", item.getData().get("url"));
            }
            if (MsgTypeEnum.TEXT == item.getType() && index == 6) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("\n", item.getData().get("text"));
            }
            if (MsgTypeEnum.IMAGE == item.getType() && index == 7) {
                count[0] = count[0] + 1;
                TestCase.assertEquals("test.image", item.getData().get("file"));
                TestCase.assertEquals("https://test.com/2.jpg", item.getData().get("url"));
            }
        }));
        TestCase.assertEquals(8, count[0]);
    }

    /**
     * <p>msgUtilsTest.</p>
     */
    @Test
    public void msgUtilsTest() {
        val build = MsgUtils.builder().at(1122334455L).text("Hello").img("https://test.com/1.jpg");
        val msg = "[CQ:at,qq=1122334455]Hello[CQ:image,file=https://test.com/1.jpg]";
        TestCase.assertEquals(msg, build.build());
    }

    /**
     * <p>isAtAllTest.</p>
     */
    @Test
    public void isAtAllTest() {
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

    /**
     * <p>getAtListTest.</p>
     */
    @Test
    public void getAtListTest() {
        val stringMsg = "[CQ:at,qq=11111][CQ:at,qq=22222][CQ:at,qq=33333]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        TestCase.assertNotNull(arrayMsg);
        val atList = ShiroUtils.getAtList(arrayMsg);
        TestCase.assertEquals(3, atList.size());
    }

    /**
     * <p>getMsgImgUrlListTest.</p>
     */
    @Test
    public void getMsgImgUrlListTest() {
        val stringMsg = "[CQ:image,file=https://test.com/2.jpg][CQ:image,file=https://test.com/2.jpg]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        TestCase.assertNotNull(arrayMsg);
        val imgUrlList = ShiroUtils.getMsgImgUrlList(arrayMsg);
        TestCase.assertEquals(2, imgUrlList.size());
    }

    /**
     * <p>getMsgVideoUrlListTest.</p>
     */
    @Test
    public void getMsgVideoUrlListTest() {
        val stringMsg = "[CQ:video,file=https://test.com/1.mp4][CQ:video,file=https://test.com/2.mp4]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        TestCase.assertNotNull(arrayMsg);
        val videoUrlList = ShiroUtils.getMsgVideoUrlList(arrayMsg);
        TestCase.assertEquals(2, videoUrlList.size());
    }

    /**
     * <p>getNicknameTest.</p>
     */
    @Test
    public void getNicknameTest() {
        val nickname = ShiroUtils.getNickname(1140667337L);
        TestCase.assertNotNull(nickname);
        TestCase.assertEquals("Zero", nickname);
    }

    /**
     * <p>debugModeTest.</p>
     */
    @Test
    public void debugModeTest() {
        TestCase.assertTrue(log.isDebugEnabled());
    }

    /**
     * <p>testOneBotMedia.</p>
     */
    @Test
    public void testOneBotMedia() {
        val media = OneBotMedia.builder().file("https://a.com/1.jpg").cache(false).proxy(false).timeout(10);
        val msg = MsgUtils.builder().img(media).build();
        val stringMsg = "[CQ:image,file=https://a.com/1.jpg,cache=0,proxy=0,timeout=10]";
        TestCase.assertEquals(stringMsg, msg);
    }

}
