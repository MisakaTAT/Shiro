package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.model.ArrayMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ArrayMsgUtil extends MsgUtils {
    private final List<ArrayMsg> builder = new ArrayList<>();

    @Override
    public MsgUtils text(String text) {
        builder.add(getJsonData("text", m -> m.put("text", text)));
        return this;
    }

    @Override
    public MsgUtils img(String img) {
        builder.add(getJsonData("image", m -> m.put("file", ShiroUtils.escape(img))));
        return this;
    }

    @Override
    public MsgUtils img(OneBotMedia media) {
        builder.add(getJsonData("image", media::escape));
        return this;
    }

    @Override
    public MsgUtils video(String video, String cover) {
        builder.add(getJsonData("video", m -> {
            m.put("file", ShiroUtils.escape(video));
            m.put("cover", ShiroUtils.escape(cover));
        }));
        return this;
    }

    @Override
    public MsgUtils flashImg(String img) {
        builder.add(getJsonData("image", m -> {
            m.put("flash", "flash");
            m.put("file", ShiroUtils.escape(img));
        }));
        return this;
    }

    @Override
    public MsgUtils face(int id) {
        builder.add(getJsonData("face", m -> m.put("id", String.valueOf(id))));
        return this;
    }

    @Override
    public MsgUtils voice(OneBotMedia media) {
        builder.add(getJsonData("record", media::escape));
        return this;
    }

    @Override
    public MsgUtils voice(String voice) {
        builder.add(getJsonData("record", m -> m.put("file", ShiroUtils.escape(voice))));
        return this;
    }

    @Override
    public MsgUtils at(long userId) {
        builder.add(getJsonData("at", m -> m.put("qq", String.valueOf(userId))));
        return this;
    }

    @Override
    public MsgUtils atAll() {
        builder.add(getJsonData("at", m -> m.put("qq", "all")));
        return this;
    }

    @Override
    public MsgUtils poke(long userId) {
        builder.add(getJsonData("poke", m -> m.put("qq", String.valueOf(userId))));
        return this;
    }

    @Override
    public MsgUtils reply(int msgId) {
        builder.add(getJsonData("reply", m -> m.put("id", String.valueOf(msgId))));
        return this;
    }

    @Override
    public MsgUtils reply(String msgId) {
        builder.add(getJsonData("reply", m -> m.put("id", msgId)));
        return this;
    }

    @Override
    public MsgUtils gift(long userId, int giftId) {
        builder.add(getJsonData("gift", m -> {
            m.put("qq", String.valueOf(userId));
            m.put("id", String.valueOf(giftId));
        }));
        return this;
    }

    @Override
    public MsgUtils tts(String text) {
        builder.add(getJsonData("tts", m -> m.put("text", text)));
        return this;
    }

    @Override
    public MsgUtils xml(String data) {
        builder.add(getJsonData("xml", m -> m.put("data", data)));
        return this;
    }

    @Override
    public MsgUtils xml(String data, int resId) {
        builder.add(getJsonData("xml", m -> {
            m.put("data", String.valueOf(data));
            m.put("resid", String.valueOf(resId));
        }));
        return this;
    }

    @Override
    public MsgUtils json(String data) {
        builder.add(getJsonData("json", m -> m.put("data", data)));
        return this;
    }

    @Override
    public MsgUtils json(String data, int resId) {
        builder.add(getJsonData("json", m -> {
            m.put("data", String.valueOf(data));
            m.put("resid", String.valueOf(resId));
        }));
        return this;
    }

    @Override
    public MsgUtils cardImage(String file) {
        builder.add(getJsonData("cardimage", m -> m.put("file", String.valueOf(file))));
        return this;
    }

    @Override
    public MsgUtils cardImage(String file, long minWidth, long minHeight, long maxWidth, long maxHeight, String source, String icon) {
        builder.add(getJsonData("cardimage", m -> {
            m.put("file", ShiroUtils.escape(file));
            m.put("minwidth", String.valueOf(minHeight));
            m.put("minheight", String.valueOf(minHeight));
            m.put("maxwidth", String.valueOf(maxWidth));
            m.put("maxheight", String.valueOf(maxHeight));
            m.put("source", ShiroUtils.escape(source));
            m.put("icon", ShiroUtils.escape(icon));
        }));
        return this;
    }

    @Override
    public MsgUtils music(String type, long id) {
        builder.add(getJsonData("music", m -> {
            m.put("type", String.valueOf(type));
            m.put("id", String.valueOf(id));
        }));
        return this;
    }

    @Override
    public MsgUtils customMusic(String url, String audio, String title, String content, String image) {
        builder.add(getJsonData("music", m -> {
            m.put("type", "custom");
            m.put("url", ShiroUtils.escape(url));
            m.put("audio", ShiroUtils.escape(audio));
            m.put("title", ShiroUtils.escape(title));
            m.put("content", ShiroUtils.escape(content));
            m.put("image", ShiroUtils.escape(image));
        }));
        return this;
    }

    @Override
    public MsgUtils customMusic(String url, String audio, String title) {
        builder.add(getJsonData("music", m -> {
            m.put("type", "custom");
            m.put("url", ShiroUtils.escape(url));
            m.put("audio", ShiroUtils.escape(audio));
            m.put("title", ShiroUtils.escape(title));
        }));
        return this;
    }

    @Override
    public MsgUtils rps(int value) {
        builder.add(getJsonData("rps", m -> m.put("value", String.valueOf(value))));
        return this;
    }

    @Override
    public String build() {
        return builder.stream().map(ArrayMsg::toCqCode).collect(Collectors.joining());
    }

    public List<ArrayMsg> buildList() {
        return builder;
    }

    private ArrayMsg getJsonData(String type, Consumer<Map<String, String>> consumer) {
        HashMap<String, String> data = new HashMap<>();
        consumer.accept(data);
        return new ArrayMsg().setRowType(type).setData(data);
    }
}
