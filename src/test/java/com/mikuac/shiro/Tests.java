package com.mikuac.shiro;

import com.mikuac.shiro.bean.MsgChainBean;
import com.mikuac.shiro.common.utils.ShiroUtils;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Tests {

    @Test
    public void testStringToMsgChain() {
        String stringMsg = "[CQ:at,qq=1140667337]测试消息1[CQ:face,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test.image,url=https://test.com/2.jpg]\n[CQ:image,file=test.image,url=https://test.com/2.jpg]";
        String arrayMsg = "[MsgChainBean(type=at, data={qq=1140667337}), MsgChainBean(type=text, data={text=测试消息1}), MsgChainBean(type=face, data={id=1}), MsgChainBean(type=text, data={text=测试消息2}), MsgChainBean(type=video, data={file=https://test.com/1.mp4}), MsgChainBean(type=image, data={file=test.image, url=https://test.com/2.jpg}), MsgChainBean(type=text, data={text=\n}), MsgChainBean(type=image, data={file=test.image, url=https://test.com/2.jpg})]";
        List<MsgChainBean> list = ShiroUtils.stringToMsgChain(stringMsg);
        TestCase.assertNotNull(list);
        TestCase.assertEquals(arrayMsg, list.toString());
    }

}