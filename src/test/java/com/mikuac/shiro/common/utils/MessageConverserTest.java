package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link MessageConverser}
 *
 * @author Zero
 */
@Slf4j
class MessageConverserTest {

    private static final String CQ_CODE_SPLIT = "(?<=\\[CQ:[^]]{1,99999}])|(?=\\[CQ:[^]]{1,99999}])";

    private static final String CQ_CODE_REGEX = "\\[CQ:([^,\\[\\]]+)((?:,[^,=\\[\\]]+=[^,\\[\\]]*)*)]";

    /**
     * 消息转换的原始实现（保留用于性能测试对照）
     * string 消息上报转消息链
     * 建议传入 event.getMessage 而非 event.getRawMessage
     * 例如 go-cq-http rawMessage 不包含图片 url
     *
     * @param msg 需要修改客户端消息上报类型为 string
     * @return 消息链
     */
    public static List<ArrayMsg> rawToArrayMsgOriginal(@NonNull String msg) {
        List<ArrayMsg> chain = new ArrayList<>();
        try {
            Arrays.stream(msg.split(CQ_CODE_SPLIT)).filter(s -> !s.isEmpty()).forEach(s -> {
                Optional<Matcher> matcher = RegexUtils.matcher(CQ_CODE_REGEX, s);
                ArrayMsg item = new ArrayMsg();
                Map<String, String> data = new HashMap<>();
                if (matcher.isEmpty()) {
                    item.setType(MsgTypeEnum.text);
                    data.put("text", ShiroUtils.unescape(s));
                    item.setData(data);
                }
                if (matcher.isPresent()) {
                    MsgTypeEnum type = MsgTypeEnum.typeOf(matcher.get().group(1));
                    String[] params = matcher.get().group(2).split(",");
                    item.setType(type);
                    Arrays.stream(params).filter(args -> !args.isEmpty()).forEach(args -> {
                        String k = args.substring(0, args.indexOf("="));
                        String v = ShiroUtils.unescape(args.substring(args.indexOf("=") + 1));
                        data.put(k, v);
                    });
                    item.setData(data);
                }
                chain.add(item);
            });
        } catch (Exception e) {
            log.error("Conversion failed: {}", e.getMessage());
        }
        return chain;
    }

    @Test
    void arrayMsgToCodeTest() {
        val raw = "[CQ:at,qq=1122334455]";
        val originalResult = rawToArrayMsgOriginal(raw);
        val optimizedResult = MessageConverser.stringToArray(raw);
        val originalCode = originalResult.get(0).toCQCode();
        val optimizedCode = optimizedResult.get(0).toCQCode();
        assertEquals(raw, originalCode);
        assertEquals(raw, optimizedCode);
        assertEquals(originalResult, optimizedResult);
    }

    @Test
    void getMsgVideoUrlListTest() {
        val stringMsg = "[CQ:video,file=https://test.com/1.mp4][CQ:video,file=https://test.com/2.mp4]";
        val originalArrayMsg = rawToArrayMsgOriginal(stringMsg);
        val optimizedArrayMsg = MessageConverser.stringToArray(stringMsg);

        assertNotNull(originalArrayMsg);
        assertNotNull(optimizedArrayMsg);
        assertEquals(originalArrayMsg, optimizedArrayMsg);

        val originalVideoUrlList = ShiroUtils.getMsgVideoUrlList(originalArrayMsg);
        val optimizedVideoUrlList = ShiroUtils.getMsgVideoUrlList(optimizedArrayMsg);

        assertEquals(2, originalVideoUrlList.size());
        assertEquals(2, optimizedVideoUrlList.size());
        assertEquals(originalVideoUrlList, optimizedVideoUrlList);
    }

    @Test
    void isAtAllTest() {
        // 测试全体@消息
        val atAll = "[CQ:at,qq=all]";
        val originalAtAllArrayMsg = rawToArrayMsgOriginal(atAll);
        val optimizedAtAllArrayMsg = MessageConverser.stringToArray(atAll);

        assertNotNull(originalAtAllArrayMsg);
        assertNotNull(optimizedAtAllArrayMsg);
        assertEquals(originalAtAllArrayMsg, optimizedAtAllArrayMsg);

        // 两个方法的结果应该一致
        assertEquals(ShiroUtils.isAtAll(originalAtAllArrayMsg), ShiroUtils.isAtAll(optimizedAtAllArrayMsg));
        assertTrue(ShiroUtils.isAtAll(originalAtAllArrayMsg)); // 应该识别为全体@

        // 测试普通用户@消息
        val atUser = "[CQ:at,qq=1122334455]";
        val originalAtUserArrayMsg = rawToArrayMsgOriginal(atUser);
        val optimizedAtUserArrayMsg = MessageConverser.stringToArray(atUser);

        assertNotNull(originalAtUserArrayMsg);
        assertNotNull(optimizedAtUserArrayMsg);
        assertEquals(originalAtUserArrayMsg, optimizedAtUserArrayMsg);

        // 两个方法的结果应该一致
        assertEquals(ShiroUtils.isAtAll(originalAtUserArrayMsg), ShiroUtils.isAtAll(optimizedAtUserArrayMsg));
        assertFalse(ShiroUtils.isAtAll(originalAtUserArrayMsg)); // 不应该识别为全体@
    }

    @Test
    void getAtListTest() {
        val stringMsg = "[CQ:at,qq=11111][CQ:at,qq=22222][CQ:at,qq=33333]";
        val originalArrayMsg = rawToArrayMsgOriginal(stringMsg);
        val optimizedArrayMsg = MessageConverser.stringToArray(stringMsg);

        assertNotNull(originalArrayMsg);
        assertNotNull(optimizedArrayMsg);
        assertEquals(originalArrayMsg, optimizedArrayMsg);

        val originalAtList = ShiroUtils.getAtList(originalArrayMsg);
        val optimizedAtList = ShiroUtils.getAtList(optimizedArrayMsg);

        assertEquals(3, originalAtList.size());
        assertEquals(3, optimizedAtList.size());
        assertEquals(originalAtList, optimizedAtList);
    }

    @Test
    void getMsgImgUrlListTest() {
        val stringMsg = "[CQ:image,file=https://test.com/2.jpg][CQ:image,file=https://test.com/2.jpg]";
        val originalArrayMsg = rawToArrayMsgOriginal(stringMsg);
        val optimizedArrayMsg = MessageConverser.stringToArray(stringMsg);

        assertNotNull(originalArrayMsg);
        assertNotNull(optimizedArrayMsg);
        assertEquals(originalArrayMsg, optimizedArrayMsg);

        val originalImgUrlList = ShiroUtils.getMsgImgUrlList(originalArrayMsg);
        val optimizedImgUrlList = ShiroUtils.getMsgImgUrlList(optimizedArrayMsg);

        assertEquals(2, originalImgUrlList.size());
        assertEquals(2, optimizedImgUrlList.size());
        assertEquals(originalImgUrlList, optimizedImgUrlList);
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
        val originalResult = rawToArrayMsgOriginal(msg);
        val optimizedResult = MessageConverser.stringToArray(msg);

        assertEquals(expected, originalResult);
        assertEquals(expected, optimizedResult);
        assertEquals(originalResult, optimizedResult);
    }

    @Test
    void rawToArrayMsg2Test() {
        val msg = "[CQ:at,qq=1122334455]测试消息1[CQ:face,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test1.image,url=https://test.com/1.jpg]\n[CQ:image,file=test2.image,url=https://test.com/2.jpg]";
        List<ArrayMsg> originalArrayMsgList = rawToArrayMsgOriginal(msg);
        List<ArrayMsg> optimizedArrayMsgList = MessageConverser.stringToArray(msg);

        assertEquals(originalArrayMsgList, optimizedArrayMsgList);

        val originalCode = MessageConverser.arraysToString(originalArrayMsgList);
        val optimizedCode = MessageConverser.arraysToString(optimizedArrayMsgList);

        assertEquals(msg, originalCode);
        assertEquals(msg, optimizedCode);
        assertEquals(originalCode, optimizedCode);
    }

    @Test
    void rawToArrayMsg3Test() {
        val msg = "[CQ:at,qq=1122334455]测试消息1[CQ:1111,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test1.image,url=https://test.com/1.jpg]\n[CQ:image,file=test2.image,url=https://test.com/2.jpg]";
        val expected = "[CQ:at,qq=1122334455]测试消息1[CQ:unknown,id=1]测试消息2[CQ:video,file=https://test.com/1.mp4][CQ:image,file=test1.image,url=https://test.com/1.jpg]\n[CQ:image,file=test2.image,url=https://test.com/2.jpg]";
        List<ArrayMsg> originalArrayMsgList = rawToArrayMsgOriginal(msg);
        List<ArrayMsg> optimizedArrayMsgList = MessageConverser.stringToArray(msg);

        assertEquals(originalArrayMsgList, optimizedArrayMsgList);

        val originalCode = MessageConverser.arraysToString(originalArrayMsgList);
        val optimizedCode = MessageConverser.arraysToString(optimizedArrayMsgList);

        assertEquals(expected, originalCode);
        assertEquals(expected, optimizedCode);
        assertEquals(originalCode, optimizedCode);
    }

    @Test
    void performanceTest() {
        // 准备测试数据
        String[] testMessages = {
                // 简单文本
                "Hello World",
                // 单个CQ码
                "[CQ:at,qq=123456]",
                // 混合消息
                "Hello [CQ:at,qq=123456] World",
                // 复杂消息
                "Hello [CQ:at,qq=123456] World [CQ:image,file=test.jpg,url=https://example.com/test.jpg] How are you? [CQ:face,id=1]",
                // 多个连续CQ码
                "[CQ:at,qq=123456][CQ:at,qq=789012][CQ:face,id=1]",
                // 长文本混合
                "这是一段很长的文本消息，包含了中文字符和各种符号！@#$%^&*()_+{}|:\"<>?[]\\;',./ " +
                        "[CQ:at,qq=123456789] 然后是更多的文本内容，测试解析性能 " +
                        "[CQ:image,file=test.jpg,url=https://example.com/very-long-url-path/test.jpg] " +
                        "最后还有一些文本内容结束。",
                // 包含转义字符的消息
                "Hello&amp;World&#44;Test&#91;CQ:at,qq=123456&#93;",
                // 空消息
                "",
                // 只有文本无CQ码
                "这是一段纯文本消息，没有任何CQ码，用来测试纯文本解析的性能表现。"
        };

        int warmupIterations = 1000;  // 预热次数
        int testIterations = 10000;   // 测试次数

        System.out.println("开始性能测试...");
        System.out.println("预热次数: " + warmupIterations);
        System.out.println("测试次数: " + testIterations);
        System.out.println();

        // 预热JVM
        System.out.println("预热阶段...");
        for (int i = 0; i < warmupIterations; i++) {
            for (String msg : testMessages) {
                rawToArrayMsgOriginal(msg);
                MessageConverser.stringToArray(msg);
            }
        }

        // 先验证结果的一致性
        System.out.println("验证结果一致性...");
        for (String msg : testMessages) {
            List<ArrayMsg> originalResult = rawToArrayMsgOriginal(msg);
            List<ArrayMsg> optimizedResult = MessageConverser.stringToArray(msg);

            assertEquals(originalResult.size(), optimizedResult.size(),
                    "消息长度不一致: " + msg);

            for (int i = 0; i < originalResult.size(); i++) {
                ArrayMsg original = originalResult.get(i);
                ArrayMsg optimized = optimizedResult.get(i);

                assertEquals(original.getType(), optimized.getType(),
                        "消息类型不一致: " + msg);
                assertEquals(original.getData(), optimized.getData(),
                        "消息数据不一致: " + msg);
            }
        }
        System.out.println("✓ 结果一致性验证通过");
        System.out.println();

        // 测试原始方法性能
        System.out.println("测试原始方法性能...");
        long originalStartTime = System.nanoTime();
        for (int i = 0; i < testIterations; i++) {
            for (String msg : testMessages) {
                rawToArrayMsgOriginal(msg);
            }
        }
        long originalEndTime = System.nanoTime();
        long originalDuration = originalEndTime - originalStartTime;

        // 测试优化方法性能
        System.out.println("测试优化方法性能...");
        long optimizedStartTime = System.nanoTime();
        for (int i = 0; i < testIterations; i++) {
            for (String msg : testMessages) {
                MessageConverser.stringToArray(msg);
            }
        }
        long optimizedEndTime = System.nanoTime();
        long optimizedDuration = optimizedEndTime - optimizedStartTime;

        // 计算性能提升
        double improvementRatio = (double) originalDuration / optimizedDuration;
        double improvementPercentage = ((double) (originalDuration - optimizedDuration) / originalDuration) * 100;

        // 输出结果
        System.out.println("========== 性能测试结果 ==========");
        System.out.printf("原始方法总耗时: %d ns (%.2f ms)%n",
                originalDuration, originalDuration / 1_000_000.0);
        System.out.printf("优化方法总耗时: %d ns (%.2f ms)%n",
                optimizedDuration, optimizedDuration / 1_000_000.0);
        System.out.printf("性能提升倍数: %.2fx%n", improvementRatio);
        System.out.printf("性能提升百分比: %.2f%%%n", improvementPercentage);
        System.out.println();

        // 计算单次调用平均耗时
        long totalCalls = (long) testIterations * testMessages.length;
        double originalAvgNs = (double) originalDuration / totalCalls;
        double optimizedAvgNs = (double) optimizedDuration / totalCalls;

        System.out.printf("单次调用平均耗时:%n");
        System.out.printf("  原始方法: %.2f ns%n", originalAvgNs);
        System.out.printf("  优化方法: %.2f ns%n", optimizedAvgNs);
        System.out.println();

        // 分别测试不同类型消息的性能
        System.out.println("========== 分类性能测试 ==========");
        testCategoryPerformance("简单文本", "Hello World", testIterations);
        testCategoryPerformance("单个CQ码", "[CQ:at,qq=123456]", testIterations);
        testCategoryPerformance("混合消息", "Hello [CQ:at,qq=123456] World", testIterations);
        testCategoryPerformance("复杂消息",
                "Hello [CQ:at,qq=123456] World [CQ:image,file=test.jpg,url=https://example.com/test.jpg] How are you? [CQ:face,id=1]",
                testIterations);

        // 确保优化版本确实更快
        assertTrue(optimizedDuration < originalDuration,
                "优化版本应该比原始版本更快");

        // 确保性能提升至少达到一定程度（比如20%）
        assertTrue(improvementPercentage > 20,
                String.format("性能提升应该至少达到20%%，实际提升: %.2f%%", improvementPercentage));
    }

    /**
     * 测试特定类别消息的性能
     */
    private void testCategoryPerformance(String category, String message, int iterations) {
        // 原始方法
        long originalStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            rawToArrayMsgOriginal(message);
        }
        long originalTime = System.nanoTime() - originalStart;

        // 优化方法
        long optimizedStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            MessageConverser.stringToArray(message);
        }
        long optimizedTime = System.nanoTime() - optimizedStart;

        double improvement = ((double) (originalTime - optimizedTime) / originalTime) * 100;

        System.out.printf("%s: 原始=%.2fms, 优化=%.2fms, 提升=%.1f%%%n",
                category,
                originalTime / 1_000_000.0,
                optimizedTime / 1_000_000.0,
                improvement);
    }

    /**
     * 内存使用情况测试
     */
    @Test
    void memoryUsageTest() {
        String complexMessage = "Hello [CQ:at,qq=123456] World [CQ:image,file=test.jpg,url=https://example.com/test.jpg] How are you? [CQ:face,id=1]";
        int iterations = 1000;

        System.out.println("内存使用情况测试...");

        // 强制垃圾回收
        System.gc();
        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // 测试原始方法
        for (int i = 0; i < iterations; i++) {
            rawToArrayMsgOriginal(complexMessage);
        }

        System.gc();
        long afterOriginalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // 测试优化方法
        for (int i = 0; i < iterations; i++) {
            MessageConverser.stringToArray(complexMessage);
        }

        System.gc();
        long afterOptimizedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.printf("基准内存: %d bytes%n", beforeMemory);
        System.out.printf("原始方法后内存: %d bytes (增加: %d bytes)%n",
                afterOriginalMemory, afterOriginalMemory - beforeMemory);
        System.out.printf("优化方法后内存: %d bytes (增加: %d bytes)%n",
                afterOptimizedMemory, afterOptimizedMemory - afterOriginalMemory);
    }
}
