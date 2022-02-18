package com.mikuac.shiro;

import com.mikuac.shiro.common.limit.RateLimiter;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import junit.framework.TestCase;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UnitTests {

    @Resource
    private RateLimiter rateLimiter;

    @Test
    public void rateLimiterTest() {
        val a = rateLimiter.tryAcquire(4);
        TestCase.assertTrue(a);
        val b = rateLimiter.acquire(5);
        TestCase.assertTrue(b);
    }

    @Test
    public void stringToArrayMsgTest() {
        val stringMsg = "[CQ:at,qq=1122334455]测试消息1[CQ:face,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test.image,url=https://test.com/2.jpg]\n[CQ:image,file=test.image,url=https://test.com/2.jpg]";
        val arrayMsg = "[MsgChainBean(type=at, data={qq=1122334455}), MsgChainBean(type=text, data={text=测试消息1}), MsgChainBean(type=face, data={id=1}), MsgChainBean(type=text, data={text=测试消息2}), MsgChainBean(type=video, data={file=https://test.com/1.mp4}), MsgChainBean(type=image, data={file=test.image, url=https://test.com/2.jpg}), MsgChainBean(type=text, data={text=\n}), MsgChainBean(type=image, data={file=test.image, url=https://test.com/2.jpg})]";
        val list = ShiroUtils.stringToMsgChain(stringMsg);
        TestCase.assertNotNull(list);
        TestCase.assertEquals(arrayMsg, list.toString());
    }

    @Test
    public void msgUtilsTest() {
        val msgUtils = MsgUtils.builder().at(1122334455L).text("Hello").img("https://test.com/1.jpg");
        val buildMsg = "[CQ:at,qq=1122334455]Hello[CQ:image,file=https://test.com/1.jpg]";
        TestCase.assertEquals(buildMsg, msgUtils.build());
    }

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

    @Test
    public void getAtListTest() {
        val stringMsg = "[CQ:at,qq=11111][CQ:at,qq=22222][CQ:at,qq=33333]";
        val arrayMsg = ShiroUtils.stringToMsgChain(stringMsg);
        TestCase.assertNotNull(arrayMsg);
        val atList = ShiroUtils.getAtList(arrayMsg);
        TestCase.assertEquals(3, atList.size());
    }

    @Test
    public void getMsgImgUrlListTest() {
        val stringMsg = "[CQ:image,file=https://test.com/2.jpg][CQ:image,file=https://test.com/2.jpg]";
        val arrayMsg = ShiroUtils.stringToMsgChain(stringMsg);
        TestCase.assertNotNull(arrayMsg);
        val imgUrlList = ShiroUtils.getMsgImgUrlList(arrayMsg);
        TestCase.assertEquals(2, imgUrlList.size());
    }

    @Test
    public void getMsgVideoUrlListTest() {
        val stringMsg = "[CQ:video,file=https://test.com/1.mp4][CQ:video,file=https://test.com/2.mp4]";
        val arrayMsg = ShiroUtils.stringToMsgChain(stringMsg);
        TestCase.assertNotNull(arrayMsg);
        val videoUrlList = ShiroUtils.getMsgVideoUrlList(arrayMsg);
        TestCase.assertEquals(2, videoUrlList.size());
    }

    @Test
    public void getNicknameTest() {
        val nickname = ShiroUtils.getNickname(1140667337L);
        TestCase.assertNotNull(nickname);
        TestCase.assertEquals("Zero", nickname);
    }

}