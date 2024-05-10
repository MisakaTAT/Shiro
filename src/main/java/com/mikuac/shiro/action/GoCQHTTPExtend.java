package com.mikuac.shiro.action;

import com.mikuac.shiro.dto.action.common.*;
import com.mikuac.shiro.dto.action.response.*;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

import java.util.List;
import java.util.Map;

public interface GoCQHTTPExtend {
    /**
     * 获取频道成员列表
     * 由于频道人数较多(数万), 请尽量不要全量拉取成员列表, 这将会导致严重的性能问题
     * 尽量使用 getGuildMemberProfile 接口代替全量拉取
     * nextToken 为空的情况下, 将返回第一页的数据, 并在返回值附带下一页的 token
     *
     * @param guildId   频道ID
     * @param nextToken 翻页Token
     * @return result {@link ActionData} of {@link GuildMemberListResp}
     */
    ActionData<GuildMemberListResp> getGuildMemberList(String guildId, String nextToken);

    /**
     * 发送信息到子频道
     *
     * @param guildId   频道 ID
     * @param channelId 子频道 ID
     * @param msg       要发送的内容
     * @return result {@link ActionData} of {@link GuildMsgId}
     */
    ActionData<GuildMsgId> sendGuildMsg(String guildId, String channelId, String msg);

    /**
     * 获取频道消息
     *
     * @param guildMsgId 频道 ID
     * @param noCache    是否使用缓存
     * @return result {@link ActionData} of {@link GetGuildMsgResp}
     */
    ActionData<GetGuildMsgResp> getGuildMsg(String guildMsgId, boolean noCache);

    /**
     * 获取频道系统内 BOT 的资料
     *
     * @return result {@link ActionData} of {@link GuildServiceProfileResp}
     */
    ActionData<GuildServiceProfileResp> getGuildServiceProfile();

    /**
     * 获取频道列表
     *
     * @return result {@link ActionList} of {@link GuildListResp}
     */
    ActionList<GuildListResp> getGuildList();

    /**
     * 通过访客获取频道元数据
     *
     * @param guildId 频道 ID
     * @return result {@link ActionData} of {@link GuildMetaByGuestResp}
     */
    ActionData<GuildMetaByGuestResp> getGuildMetaByGuest(String guildId);

    /**
     * 获取子频道列表
     *
     * @param guildId 频道 ID
     * @param noCache 是否无视缓存
     * @return result {@link ActionList} of {@link ChannelInfoResp}
     */
    ActionList<ChannelInfoResp> getGuildChannelList(String guildId, boolean noCache);

    /**
     * 单独获取频道成员信息
     *
     * @param guildId 频道ID
     * @param userId  用户ID
     * @return result {@link ActionData} of {@link GuildMemberProfileResp}
     */
    ActionData<GuildMemberProfileResp> getGuildMemberProfile(String guildId, String userId);

    /**
     * 设置群组专属头衔
     *
     * @param groupId      群号
     * @param userId       要设置的 QQ 号
     * @param specialTitle 专属头衔，不填或空字符串表示删除专属头衔
     * @param duration     专属头衔有效期，单位秒，-1 表示永久，不过此项似乎没有效果，可能是只有某些特殊的时间长度有效，有待测试
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupSpecialTitle(long groupId, long userId, String specialTitle, int duration);

    /**
     * 删除好友
     *
     * @param friendId 好友 QQ 号
     * @return result {@link ActionRaw}
     */
    ActionRaw deleteFriend(long friendId);

    /**
     * 设置群头像
     * 目前这个API在登录一段时间后因cookie失效而失效, 请考虑后使用
     *
     * @param groupId 群号
     * @param file    图片文件名（支持绝对路径，网络URL，Base64编码）
     * @param cache   表示是否使用已缓存的文件 （通过网络URL发送时有效, 1表示使用缓存, 0关闭关闭缓存, 默认为1）
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupPortrait(long groupId, String file, int cache);

    /**
     * 检查链接安全性
     * 安全等级, 1: 安全 2: 未知 3: 危险
     *
     * @param url 需要检查的链接
     * @return result {@link ActionData} of {@link CheckUrlSafelyResp}
     */
    ActionData<CheckUrlSafelyResp> checkUrlSafely(String url);

    /**
     * 发送群公告
     *
     * @param groupId 群号
     * @param content 公告内容
     * @return result {@link ActionRaw}
     */
    ActionRaw sendGroupNotice(long groupId, String content);

    /**
     * 获取群 @全体成员 剩余次数
     *
     * @param groupId 群号
     * @return result {@link ActionData} of {@link GroupAtAllRemainResp}
     */
    ActionData<GroupAtAllRemainResp> getGroupAtAllRemain(long groupId);

    /**
     * 上传群文件
     * 在不提供 folder 参数的情况下默认上传到根目录
     * 只能上传本地文件, 需要上传 http 文件的话请先下载到本地
     *
     * @param groupId 群号
     * @param file    本地文件路径
     * @param name    储存名称
     * @param folder  父目录ID
     * @return result {@link ActionRaw}
     */
    ActionRaw uploadGroupFile(long groupId, String file, String name, String folder);

    /**
     * 上传群文件
     * 在不提供 folder 参数的情况下默认上传到根目录
     * 只能上传本地文件, 需要上传 http 文件的话请先下载到本地
     *
     * @param groupId 群号
     * @param file    本地文件路径
     * @param name    储存名称
     * @return result {@link ActionRaw}
     */
    ActionRaw uploadGroupFile(long groupId, String file, String name);

    /**
     * 调用 go cq http 下载文件
     *
     * @param url         链接地址
     * @param threadCount 下载线程数
     * @param headers     自定义请求头
     * @return result {@link ActionData} of {@link DownloadFileResp}
     */
    ActionData<DownloadFileResp> downloadFile(String url, int threadCount, String headers);

    /**
     * 调用 go cq http 下载文件
     *
     * @param url 链接地址
     * @return result {@link ActionData} of {@link DownloadFileResp}
     */
    ActionData<DownloadFileResp> downloadFile(String url);

    /**
     * 发送合并转发 (群)
     *
     * @param groupId 群号
     * @param msg     自定义转发消息 (可使用 ShiroUtils.generateForwardMsg() 方法创建)
     *                <a href="https://docs.go-cqhttp.org/cqcode/#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91">参考文档</a>
     * @return result {@link ActionRaw}
     */
    ActionData<MsgId> sendGroupForwardMsg(long groupId, List<Map<String, Object>> msg);

    /**
     * 获取群根目录文件列表
     *
     * @param groupId 群号
     * @return result {@link ActionData} of {@link GroupFilesResp}
     */
    ActionData<GroupFilesResp> getGroupRootFiles(long groupId);

    /**
     * 获取群子目录文件列表
     *
     * @param groupId  群号
     * @param folderId 文件夹ID 参考 Folder 对象
     * @return result {@link ActionData} of {@link GroupFilesResp}
     */
    ActionData<GroupFilesResp> getGroupFilesByFolder(long groupId, String folderId);

    /**
     * 获取精华消息列表
     *
     * @param groupId 群号
     * @return result {@link ActionList} of {@link EssenceMsgResp}
     */
    ActionList<EssenceMsgResp> getEssenceMsgList(long groupId);

    /**
     * 设置精华消息
     *
     * @param msgId 消息 ID
     * @return result {@link ActionRaw}
     */
    ActionRaw setEssenceMsg(int msgId);

    /**
     * 移出精华消息
     *
     * @param msgId 消息 ID
     * @return result {@link ActionRaw}
     */
    ActionRaw deleteEssenceMsg(int msgId);

    /**
     * 设置机器人账号资料
     *
     * @param nickname     昵称
     * @param company      公司
     * @param email        邮箱
     * @param college      学校
     * @param personalNote 个性签名
     * @return result {@link ActionRaw}
     */
    ActionRaw setBotProfile(String nickname, String company, String email, String college, String personalNote);

    /**
     * 发送合并转发 (私聊)
     *
     * @param userId 目标用户
     * @param msg    自定义转发消息 (可使用 ShiroUtils.generateForwardMsg() 方法创建)
     *               <a href="https://docs.go-cqhttp.org/cqcode/#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91">参考文档</a>
     * @return result {@link ActionRaw}
     */
    ActionData<MsgId> sendPrivateForwardMsg(long userId, List<Map<String, Object>> msg);

    /**
     * 发送合并转发
     *
     * @param event 事件
     * @param msg   自定义转发消息 (可使用 ShiroUtils.generateForwardMsg() 方法创建)
     *              <a href="https://docs.go-cqhttp.org/cqcode/#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91">参考文档</a>
     * @return result {@link ActionRaw}
     */
    ActionData<MsgId> sendForwardMsg(AnyMessageEvent event, List<Map<String, Object>> msg);

    /**
     * 获取中文分词
     *
     * @param content 内容
     * @return result {@link ActionData} of {@link WordSlicesResp}
     */
    ActionData<WordSlicesResp> getWordSlices(String content);

    /**
     * 获取当前账号在线客户端列表
     *
     * @param noCache 是否无视缓存
     * @return result {@link ActionData} of {@link ClientsResp}
     */
    ActionData<ClientsResp> getOnlineClients(boolean noCache);

    /**
     * 图片 OCR
     *
     * @param image 图片ID
     * @return result {@link ActionData} of {@link OcrResp}
     */
    ActionData<OcrResp> ocrImage(String image);

    /**
     * 私聊发送文件
     *
     * @param userId 目标用户
     * @param file   本地文件路径
     * @param name   文件名
     * @return result {@link ActionRaw}
     */
    ActionRaw uploadPrivateFile(long userId, String file, String name);

    /**
     * 群打卡
     *
     * @param groupId 群号
     * @return result {@link ActionRaw}
     */
    ActionRaw sendGroupSign(long groupId);

    /**
     * 删除单向好友
     *
     * @param userId QQ号
     * @return result {@link ActionRaw}
     */
    ActionRaw deleteUnidirectionalFriend(long userId);

    /**
     * 获取单向好友列表
     *
     * @return result {@link ActionList} of {@link UnidirectionalFriendListResp}
     */
    ActionList<UnidirectionalFriendListResp> getUnidirectionalFriendList();

    /**
     * 获取群文件资源链接
     *
     * @param groupId 群号
     * @param fileId  文件ID
     * @param busId   文件类型
     * @return result {@link ActionData} of {@link UrlResp}
     */
    ActionData<UrlResp> getGroupFileUrl(long groupId, String fileId, int busId);

    /**
     * 创建群文件文件夹
     *
     * @param groupId    群号
     * @param folderName 文件夹名
     * @return result {@link ActionRaw}
     */
    ActionRaw createGroupFileFolder(long groupId, String folderName);

    /**
     * 删除群文件文件夹
     *
     * @param groupId  群号
     * @param folderId 文件夹ID
     * @return result {@link ActionRaw}
     */
    ActionRaw deleteGroupFileFolder(long groupId, String folderId);

    /**
     * 删除群文件
     *
     * @param groupId 群号
     * @param fileId  文件ID
     * @param busId   文件类型
     * @return result {@link ActionRaw}
     */
    ActionRaw deleteGroupFile(long groupId, String fileId, int busId);
}
