package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.model.ArrayMsg;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "java:S1192"})
public class ArrayMsgUtils {

    private final List<ArrayMsg> builder = new ArrayList<>();

    public static ArrayMsgUtils builder() {
        return new ArrayMsgUtils();
    }

    public ArrayMsgUtils text(String text) {
        builder.add(getJsonData("text", m -> m.put("text", text)));
        return this;
    }

    public ArrayMsgUtils img(String img) {
        builder.add(getJsonData("image", m -> m.put("file", ShiroUtils.escape(img))));
        return this;
    }

    public ArrayMsgUtils img(byte[] image) {
        String img = String.format("base64://%s", Base64.getEncoder().encodeToString(image));
        builder.add(getJsonData("image", m -> m.put("file", ShiroUtils.escape(img))));
        return this;
    }

    public ArrayMsgUtils img(OneBotMedia media) {
        builder.add(getJsonData("image", media::escape));
        return this;
    }

    public ArrayMsgUtils video(String video, String cover) {
        builder.add(getJsonData("video", m -> {
            m.put("file", ShiroUtils.escape(video));
            m.put("cover", ShiroUtils.escape(cover));
        }));
        return this;
    }

    public ArrayMsgUtils flashImg(String img) {
        builder.add(getJsonData("image", m -> {
            m.put("flash", "flash");
            m.put("file", ShiroUtils.escape(img));
        }));
        return this;
    }

    public ArrayMsgUtils face(int id) {
        builder.add(getJsonData("face", m -> m.put("id", String.valueOf(id))));
        return this;
    }

    public ArrayMsgUtils voice(OneBotMedia media) {
        builder.add(getJsonData("record", media::escape));
        return this;
    }

    public ArrayMsgUtils voice(String voice) {
        builder.add(getJsonData("record", m -> m.put("file", ShiroUtils.escape(voice))));
        return this;
    }

    public ArrayMsgUtils at(long userId) {
        builder.add(getJsonData("at", m -> m.put("qq", String.valueOf(userId))));
        return this;
    }

    public ArrayMsgUtils atAll() {
        builder.add(getJsonData("at", m -> m.put("qq", "all")));
        return this;
    }

    public ArrayMsgUtils poke(long userId) {
        builder.add(getJsonData("poke", m -> m.put("qq", String.valueOf(userId))));
        return this;
    }

    public ArrayMsgUtils reply(int msgId) {
        builder.add(getJsonData("reply", m -> m.put("id", String.valueOf(msgId))));
        return this;
    }

    public ArrayMsgUtils reply(String msgId) {
        builder.add(getJsonData("reply", m -> m.put("id", msgId)));
        return this;
    }

    public ArrayMsgUtils gift(long userId, int giftId) {
        builder.add(getJsonData("gift", m -> {
            m.put("qq", String.valueOf(userId));
            m.put("id", String.valueOf(giftId));
        }));
        return this;
    }

    public ArrayMsgUtils tts(String text) {
        builder.add(getJsonData("tts", m -> m.put("text", text)));
        return this;
    }

    public ArrayMsgUtils xml(String data) {
        builder.add(getJsonData("xml", m -> m.put("data", data)));
        return this;
    }

    public ArrayMsgUtils xml(String data, int resId) {
        builder.add(getJsonData("xml", m -> {
            m.put("data", String.valueOf(data));
            m.put("resid", String.valueOf(resId));
        }));
        return this;
    }

    public ArrayMsgUtils json(String data) {
        builder.add(getJsonData("json", m -> m.put("data", data)));
        return this;
    }

    public ArrayMsgUtils json(String data, int resId) {
        builder.add(getJsonData("json", m -> {
            m.put("data", String.valueOf(data));
            m.put("resid", String.valueOf(resId));
        }));
        return this;
    }

    public ArrayMsgUtils cardImage(String file) {
        builder.add(getJsonData("cardimage", m -> m.put("file", String.valueOf(file))));
        return this;
    }

    public ArrayMsgUtils cardImage(String file, long minWidth, long minHeight, long maxWidth, long maxHeight, String source, String icon) {
        builder.add(getJsonData("cardimage", m -> {
            m.put("file", ShiroUtils.escape(file));
            m.put("minwidth", String.valueOf(minWidth));
            m.put("minheight", String.valueOf(minHeight));
            m.put("maxwidth", String.valueOf(maxWidth));
            m.put("maxheight", String.valueOf(maxHeight));
            m.put("source", ShiroUtils.escape(source));
            m.put("icon", ShiroUtils.escape(icon));
        }));
        return this;
    }

    public ArrayMsgUtils music(String type, long id) {
        builder.add(getJsonData("music", m -> {
            m.put("type", String.valueOf(type));
            m.put("id", String.valueOf(id));
        }));
        return this;
    }

    public ArrayMsgUtils music(String type, Map<String, String> params) {
        builder.add(getJsonData("music", m -> {
            m.put("type", String.valueOf(type));
            for (Map.Entry<String, String> entry : params.entrySet()) {
                m.put(entry.getKey(), ShiroUtils.escape(entry.getValue()));
            }
        }));
        return this;
    }

    public ArrayMsgUtils rps(int value) {
        builder.add(getJsonData("rps", m -> m.put("value", String.valueOf(value))));
        return this;
    }

    public ArrayMsgUtils longMsg(String id) {
        builder.add(getJsonData("longmsg", m -> m.put("id", id)));
        return this;
    }

    public ArrayMsgUtils forward(String id) {
        builder.add(getJsonData("forward", m -> m.put("id", id)));
        return this;
    }

    public ArrayMsgUtils markdown(String content) {
        builder.add(getJsonData("markdown", m -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("content", content);
            m.put("content", JsonUtils.toJSONString(map));
        }));
        return this;
    }

    /**
     * <pre>{@code
     *     Keyboard keyboard = Keyboard.Builder()
     *     .addRow()
     *     .addButton(Keyboard.TextButtonBuilder()
     *          .label("+1")
     *          .data("md2")
     *          .build()
     *          )
     *     .build();
     * }</pre>
     */
    public ArrayMsgUtils keyboard(Keyboard keyboard) {
        builder.add(getJsonData("keyboard", m -> m.put("keyboard", JsonUtils.toJSONString(keyboard))));
        return this;
    }

    public String buildCQ() {
        return builder.stream().map(ArrayMsg::toCQCode).collect(Collectors.joining());
    }

    public List<ArrayMsg> build() {
        return builder;
    }

    private ArrayMsg getJsonData(String type, Consumer<Map<String, String>> consumer) {
        HashMap<String, String> data = new HashMap<>();
        consumer.accept(data);
        return new ArrayMsg().setRawType(type).setData(data);
    }

}
