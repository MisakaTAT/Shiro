package com.mikuac.shiro.handler.injection;

import com.mikuac.shiro.common.utils.ArrayUtils;
import com.mikuac.shiro.common.utils.RegexUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.HandlerMethod;
import com.mikuac.shiro.dto.action.annotation.GroupAdminHandler;
import com.mikuac.shiro.dto.action.annotation.GroupMessageHandler;
import com.mikuac.shiro.dto.action.annotation.GroupUploadHandler;
import com.mikuac.shiro.dto.action.annotation.PrivateMessageHandler;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.dto.event.notice.GroupAdminNoticeEvent;
import com.mikuac.shiro.dto.event.notice.GroupUploadNoticeEvent;
import com.mikuac.shiro.enums.AtEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author meme
 * @version V0.0.1
 * @Package com.mikuac.shiro.handler.injection
 * @Description:
 * @date 2021/10/26 21:24
 */


@Component
public class InjectionHandler  {

    public  void invokeGroupMessage(@NotNull Bot bot, GroupMessageEvent event){
        //获取所有成员方法
//        Method[] methods = pluginClass.getMethods();

        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getHandler();
        List<HandlerMethod> handlerMethodList = handlers.get(GroupMessageHandler.class);
        if (handlerMethodList==null)
            return;
        for (HandlerMethod handlerMethod:handlerMethodList){
            GroupMessageHandler gmh=handlerMethod.getMethod().getAnnotation(GroupMessageHandler.class);

            if (gmh.excludeGroupIds().length>0&&ArrayUtils.contain(gmh.excludeGroupIds(),event.getGroupId()))
                return;

            if (gmh.groupIds().length>0&&!ArrayUtils.contain(gmh.groupIds(),event.getGroupId()))
                return;
            GroupMessageEvent.GroupSender sender = event.getSender();
            if (gmh.excludeSenderIds().length>0&&ArrayUtils.contain(gmh.excludeSenderIds(),Long.parseLong(sender.getUserId())))
                return;

            if (gmh.excludeSenderIds().length>0&&!ArrayUtils.contain(gmh.senderIds(),Long.parseLong(sender.getUserId())))
                return;

            List<String> atList = ShiroUtils.getAtList(event.getRawMessage());


            if (gmh.isAt()== AtEnum.NEED&&!atList.contains(String.valueOf(event.getSelfId())))
                return;
            if (gmh.isAt()==AtEnum.NOT_NEED&&atList.contains(String.valueOf(event.getSelfId())))
                return;



            Map<Class<?>,Object> argMap= new HashMap<>();
            if (!"none".equals(gmh.regex())){
                Matcher matcher = RegexUtils.regexMacher(gmh.regex(), event.getRawMessage());
                if (matcher==null)
                    return;
                argMap.put(Matcher.class,matcher);
            }



            argMap.put(GroupMessageEvent.GroupSender.class,sender);
            argMap.put(Bot.class,bot);
            argMap.put(GroupMessageEvent.class,event) ;
            invokeMethod(handlerMethod, argMap);

        }
    }


    //调用私人信息
    public  void invokePrivateMessage(@NotNull Bot bot, PrivateMessageEvent event){
        //获取所有成员方法
//        Method[] methods = pluginClass.getMethods();

        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getHandler();
        List<HandlerMethod> handlerMethods = handlers.get(PrivateMessageHandler.class);
        if (handlerMethods==null)
            return;
        for (HandlerMethod handlerMethod:handlerMethods){
            PrivateMessageHandler pmh=handlerMethod.getMethod().getAnnotation(PrivateMessageHandler.class);

            if (pmh.excludeSenderIds().length>0&&ArrayUtils.contain(pmh.excludeSenderIds(),event.getUserId()))
                return;

            if (pmh.excludeSenderIds().length>0&&!ArrayUtils.contain(pmh.senderIds(),event.getUserId()))
                return;
            Map<Class<?>,Object> argMap= new HashMap<>();
            if (!"none".equals(pmh.regex())){
                Matcher matcher = RegexUtils.regexMacher(pmh.regex(), event.getRawMessage());
                if (matcher==null)
                    return;
                argMap.put(Matcher.class,matcher);
            }


            argMap.put(PrivateMessageEvent.PrivateSender.class,event.getPrivateSender());

            argMap.put(Bot.class,bot);
            argMap.put(PrivateMessageEvent.class,event) ;
            invokeMethod(handlerMethod, argMap);

        }
    }

    //调用上传
    public  void invokeGroupUpload(@NotNull Bot bot, GroupUploadNoticeEvent event){
        //获取所有成员方法
//        Method[] methods = pluginClass.getMethods();

        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getHandler();
        List<HandlerMethod> handlerMethods = handlers.get(GroupUploadHandler.class);
        if (handlerMethods==null)
            return;
        for (HandlerMethod handlerMethod:handlerMethods){
            GroupUploadHandler handler=handlerMethod.getMethod().getAnnotation(GroupUploadHandler.class);

            if (handler.excludeGroupIds().length>0&&ArrayUtils.contain(handler.excludeGroupIds(),event.getGroupId()))
                return;

            if (handler.groupIds().length>0&&!ArrayUtils.contain(handler.groupIds(),event.getGroupId()))
                return;

            Map<Class<?>,Object> argMap= new HashMap<>();
            if (!"none".equals(handler.regex())){

                Matcher matcher = RegexUtils.regexMacher(handler.regex(), event.getFile().getName());
                if (matcher==null)
                    return;
                argMap.put(Matcher.class,matcher);
            }



            argMap.put(Bot.class,bot);
            argMap.put(PrivateMessageEvent.class,event) ;
            invokeMethod(handlerMethod, argMap);

        }
    }


    public  void invokeGroupAdmin(@NotNull Bot bot, GroupAdminNoticeEvent event){
        //获取所有成员方法
//        Method[] methods = pluginClass.getMethods();

        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getHandler();
        List<HandlerMethod> handlerMethods = handlers.get(GroupAdminHandler.class);
        if (handlerMethods==null)
            return;
        for (HandlerMethod handlerMethod:handlerMethods){
            GroupAdminHandler handler=handlerMethod.getMethod().getAnnotation(GroupAdminHandler.class);

            if (handler.excludeGroupIds().length>0&&ArrayUtils.contain(handler.excludeGroupIds(),event.getGroupId()))
                return;

            if (handler.groupIds().length>0&&!ArrayUtils.contain(handler.groupIds(),event.getGroupId()))
                return;

            switch (handler.TYPE_ENUM()){
                case OFF:
                    return;
                case ON:
                    break;
                case UNSET:
                    if (!event.getSubType().equals("unset"))
                    return;
                case SET:
                    if (!event.getSubType().equals("set"))
                        return;
            }

            Map<Class<?>,Object> argMap= new HashMap<>();
            argMap.put(Bot.class,bot);
            argMap.put(GroupAdminNoticeEvent.class,event) ;
            invokeMethod(handlerMethod, argMap);

        }
    }



    public void invokeMethod(HandlerMethod handlerMethod, Map<Class<?>, Object> argMap) {
        Class<?>[] parameterTypes = handlerMethod.getMethod().getParameterTypes();
        Object[] objects = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (argMap.containsKey(parameterType)){
                objects[i]=argMap.remove(parameterType);
            }else {
                objects[i]=null;
            }
        }
        try {
            handlerMethod.getMethod().invoke(handlerMethod.getObject(),objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
