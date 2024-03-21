package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShiroUtilsTest {

    @Test
    void arrayMsgToCodeTest() {
        val raw = "[CQ:at,qq=1122334455]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(raw);
        val cqCode = ShiroUtils.arrayMsgToCode(arrayMsg.get(0));
        assertEquals(raw, cqCode);
    }

    @Test
    void getMsgVideoUrlListTest() {
        val stringMsg = "[CQ:video,file=https://test.com/1.mp4][CQ:video,file=https://test.com/2.mp4]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        assertNotNull(arrayMsg);
        val videoUrlList = ShiroUtils.getMsgVideoUrlList(arrayMsg);
        assertEquals(2, videoUrlList.size());
    }

    @Test
    void isAtAllTest() {
        val atAll = "[CQ:at,qq=all]";
        val atAllArrayMsg = ShiroUtils.rawToArrayMsg(atAll);
        assertNotNull(atAllArrayMsg);
        val isAtAll = ShiroUtils.isAtAll(atAllArrayMsg);
        assertTrue(isAtAll);

        val atUser = "[CQ:at,qq=1122334455]";
        val atUserArrayMsg = ShiroUtils.rawToArrayMsg(atUser);
        assertNotNull(atUserArrayMsg);
        val notAtAll = ShiroUtils.isAtAll(atUserArrayMsg);
        assertFalse(notAtAll);
    }

    @Test
    void getAtListTest() {
        val stringMsg = "[CQ:at,qq=11111][CQ:at,qq=22222][CQ:at,qq=33333]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        assertNotNull(arrayMsg);
        val atList = ShiroUtils.getAtList(arrayMsg);
        assertEquals(3, atList.size());
    }

    @Test
    void getMsgImgUrlListTest() {
        val stringMsg = "[CQ:image,file=https://test.com/2.jpg][CQ:image,file=https://test.com/2.jpg]";
        val arrayMsg = ShiroUtils.rawToArrayMsg(stringMsg);
        assertNotNull(arrayMsg);
        val imgUrlList = ShiroUtils.getMsgImgUrlList(arrayMsg);
        assertEquals(2, imgUrlList.size());
    }

    @Test
    void testRawToArrayMsgTest() {
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
        assertEquals(expected, actual);
    }

    @Test
    void rawToArrayMsg2Test() {
        val msg = "[CQ:at,qq=1122334455]测试消息1[CQ:face,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test1.image,url=https://test.com/1.jpg]\n[CQ:image,file=test2.image,url=https://test.com/2.jpg]";
        List<ArrayMsg> arrayMsgList = ShiroUtils.rawToArrayMsg(msg);
        val actual = ShiroUtils.arrayMsgToCode(arrayMsgList);
        assertEquals(msg, actual);
    }

    @Test
    void rawToArrayMsg3Test() {
        val msg = "[CQ:at,qq=1122334455]测试消息1[CQ:1111,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test1.image,url=https://test.com/1.jpg]\n[CQ:image,file=test2.image,url=https://test.com/2.jpg]";
        val expected = "[CQ:at,qq=1122334455]测试消息1[CQ:unknown,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test1.image,url=https://test.com/1.jpg]\n[CQ:image,file=test2.image,url=https://test.com/2.jpg]";
        List<ArrayMsg> arrayMsgList = ShiroUtils.rawToArrayMsg(msg);
        val actual = ShiroUtils.arrayMsgToCode(arrayMsgList);
        assertEquals(expected, actual);
    }

}
