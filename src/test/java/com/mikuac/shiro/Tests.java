package com.mikuac.shiro;

import com.mikuac.shiro.bean.MsgChainBean;
import com.mikuac.shiro.common.limit.ActionRateLimiter;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class Tests {

    @Resource
    private ActionRateLimiter actionRateLimiter;

    @Test
    public void testRateLimiter() throws Exception {
        double a = actionRateLimiter.acquire();
        TestCase.assertEquals(a, 0.00);
        double b = actionRateLimiter.acquire();
        if (b <= 0) {
            TestCase.fail("TokenBucket test failed.");
        }
        Thread.sleep(1000);
        double c = actionRateLimiter.acquire();
        TestCase.assertEquals(c, 0.00);
    }

    @Test
    public void testStringToMsgChain() {
        String stringMsg = "[CQ:at,qq=1140667337]测试消息1[CQ:face,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test.image,url=https://test.com/2.jpg]\n[CQ:image,file=test.image,url=https://test.com/2.jpg]";
        String arrayMsg = "[MsgChainBean(type=at, data={qq=1140667337}), MsgChainBean(type=text, data={text=测试消息1}), MsgChainBean(type=face, data={id=1}), MsgChainBean(type=text, data={text=测试消息2}), MsgChainBean(type=video, data={file=https://test.com/1.mp4}), MsgChainBean(type=image, data={file=test.image, url=https://test.com/2.jpg}), MsgChainBean(type=text, data={text=\n}), MsgChainBean(type=image, data={file=test.image, url=https://test.com/2.jpg})]";
        List<MsgChainBean> list = ShiroUtils.stringToMsgChain(stringMsg);
        TestCase.assertNotNull(list);
        TestCase.assertEquals(arrayMsg, list.toString());
    }

    @Test
    public void testMsgUtils() {
        MsgUtils msgUtils = MsgUtils.builder()
                .at(1140667337L)
                .text("Hello")
                .img("https://test.com/1.jpg");
        String buildMsg = "[CQ:at,qq=1140667337]Hello[CQ:image,file=https://test.com/1.jpg]";
        TestCase.assertEquals(buildMsg, msgUtils.build());
    }

}