package com.mikuac.shiro;

import com.mikuac.shiro.common.limit.RateLimiter;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.OneBotMedia;
import com.mikuac.shiro.common.utils.ShiroUtils;
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
    public void stringToArrayMsgTest() {
        val stringMsg = "[CQ:at,qq=1122334455]测试消息1[CQ:face,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test.image,url=https://test.com/2.jpg]\n[CQ:image,file=test.image,url=https://test.com/2.jpg]";
        val arrayMsg = "[ArrayMsg(type=AT, data={qq=1122334455}), ArrayMsg(type=TEXT, data={text=测试消息1}), ArrayMsg(type=FACE, data={id=1}), ArrayMsg(type=TEXT, data={text=测试消息2}), ArrayMsg(type=VIDEO, data={file=https://test.com/1.mp4}), ArrayMsg(type=IMAGE, data={file=test.image, url=https://test.com/2.jpg}), ArrayMsg(type=TEXT, data={text=\n}), ArrayMsg(type=IMAGE, data={file=test.image, url=https://test.com/2.jpg})]";
        val list = ShiroUtils.stringToMsgChain(stringMsg);
        TestCase.assertNotNull(list);
        TestCase.assertEquals(arrayMsg, list.toString());
    }

    /**
     * <p>msgUtilsTest.</p>
     */
    @Test
    public void msgUtilsTest() {
        val msgUtils = MsgUtils.builder().at(1122334455L).text("Hello").img("https://test.com/1.jpg");
        val buildMsg = "[CQ:at,qq=1122334455]Hello[CQ:image,file=https://test.com/1.jpg]";
        TestCase.assertEquals(buildMsg, msgUtils.build());
    }

    /**
     * <p>isAtAllTest.</p>
     */
    @Test
    public void isAtAllTest() {
        val atAll = "[CQ:at,qq=all]";
        val atAllArrayMsg = ShiroUtils.stringToMsgChain(atAll);
        TestCase.assertNotNull(atAllArrayMsg);
        val isAtAll = ShiroUtils.isAtAll(atAllArrayMsg);
        TestCase.assertTrue(isAtAll);

        val atUser = "[CQ:at,qq=1122334455]";
        val atUserArrayMsg = ShiroUtils.stringToMsgChain(atUser);
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
        val arrayMsg = ShiroUtils.stringToMsgChain(stringMsg);
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
        val arrayMsg = ShiroUtils.stringToMsgChain(stringMsg);
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
        val arrayMsg = ShiroUtils.stringToMsgChain(stringMsg);
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
