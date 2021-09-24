<p align="center">
  <a href="https://mikuac.com/archives/675"><img src="https://mikuac.com/images/yuri.jpg" width="200" height="200" alt="Yuri"></a>
</p>

<div align="center">

# Shiro

_✨ 基于 [OneBot](https://github.com/howmanybots/onebot/blob/master/README.md) 协议的 QQ机器人 快速开发框架 ✨_

</div>

<p align="center">
    <a href="https://search.maven.org/search?q=com.mikuac.shiro"><img src="https://img.shields.io/maven-central/v/com.mikuac/shiro.svg?label=Maven%20Central&style=flat-square" alt="maven" /></a>
    <a href="https://github.com/MisakaTAT/Shiro/issues"><img src="https://img.shields.io/github/issues/MisakaTAT/Shiro?style=flat-square" alt="issues" /></a>
    <a href="https://github.com/MisakaTAT/Shiro/blob/main/LICENSE"><img src="https://img.shields.io/github/license/MisakaTAT/Shiro?style=flat-square" alt="license"></a>
    <img src="https://img.shields.io/badge/JDK-1.8+-brightgreen.svg?style=flat-square" alt="jdk-version">
    <a href="https://qm.qq.com/cgi-bin/qm/qr?k=Fl3-G9irYp84ng7LAFlTvqrOGIFHdufR&jump_from=webapi"><img src="https://img.shields.io/badge/QQ群-204219849-brightgreen.svg?style=flat-square" alt="qq-group"></a>
    <a href="https://github.com/howmanybots/onebot"><img src="https://img.shields.io/badge/OneBot-v11-blue?style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABABAMAAABYR2ztAAAAIVBMVEUAAAAAAAADAwMHBwceHh4UFBQNDQ0ZGRkoKCgvLy8iIiLWSdWYAAAAAXRSTlMAQObYZgAAAQVJREFUSMftlM0RgjAQhV+0ATYK6i1Xb+iMd0qgBEqgBEuwBOxU2QDKsjvojQPvkJ/ZL5sXkgWrFirK4MibYUdE3OR2nEpuKz1/q8CdNxNQgthZCXYVLjyoDQftaKuniHHWRnPh2GCUetR2/9HsMAXyUT4/3UHwtQT2AggSCGKeSAsFnxBIOuAggdh3AKTL7pDuCyABcMb0aQP7aM4AnAbc/wHwA5D2wDHTTe56gIIOUA/4YYV2e1sg713PXdZJAuncdZMAGkAukU9OAn40O849+0ornPwT93rphWF0mgAbauUrEOthlX8Zu7P5A6kZyKCJy75hhw1Mgr9RAUvX7A3csGqZegEdniCx30c3agAAAABJRU5ErkJggg=="></a>
</p>

<p align="center">
  <a href="https://misakatat.github.io/shiro-docs">文档</a>
  ·
  <a href="https://github.com/MisakaTAT/Shiro/releases">下载</a>
  ·
  <a href="https://misakatat.github.io/shiro-docs/quickstart">快速开始</a>
  ·
  <a href="">参与贡献</a>
</p>


[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FMisakaTAT%2FShiro.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FMisakaTAT%2FShiro?ref=badge_large)

# QuickStart
请访问 [Maven Repo](https://search.maven.org/search?q=com.mikuac.shiro
) 查看最新版本，并替换 version 内的 latest version
```xml

<dependency>
  <groupId>com.mikuac</groupId>
  <artifactId>shiro</artifactId>
  <version>latest version</version>
</dependency>
```

基础配置文件，或详见 [高级自定义配置](https://misakatat.github.io/shiro-docs/quickstart/#%E9%AB%98%E7%BA%A7%E8%87%AA%E5%AE%9A%E4%B9%89%E9%85%8D%E7%BD%AE)
```yaml
# 修改application.yaml
server:
  port: 5555

shiro:
  # Webscoket连接地址，无需该配置字段可删除，将使用默认值 "/ws/shiro"
  # ws-config:
  #   ws-url: "/ws/shiro"
  # 插件列表 (顺序执行，如果前一个插件返回了MESSAGE_BLOCK，将不会执行后续插件)
  plugin-list:
    - com.mikuac.bot.plugins.ExamplePlugin
```

示例插件
```java
// 继承BotPlugin开始编写插件
@Component
public class ExamplePlugin extends BotPlugin {

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        // 构建消息
        MsgUtils msgUtils = MsgUtils().builder().face(66).text("Hello, this is shiro demo.");
        // 发送私聊消息
        bot.sendPrivateMsg(event.getUserId(), msgUtils.build(), false);
        // 返回 MESSAGE_IGNORE 插件向下执行，返回 MESSAGE_BLOCK 则不执行下一个插件
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        // 构建消息
        MsgUtils msgUtils = MsgUtils().builder().at(event.getUserId()).face(66).text("Hello, this is shiro demo.");
        // 发送群消息
        bot.sendGroupMsg(event.getGroupId(), msgUtils.build(), false);
        // 返回 MESSAGE_IGNORE 插件向下执行，返回 MESSAGE_BLOCK 则不执行下一个插件
        return MESSAGE_IGNORE;
    }

}
```

# Client

Shiro 以 [OneBot-v11](https://github.com/howmanybots/onebot/tree/master/v11/specs) 标准协议进行开发，兼容所有支持反向WebSocket的OneBot协议客户端

| 项目地址 | 平台 | 核心作者 | 备注 |
| --- | --- | --- | --- |
| [Yiwen-Chan/OneBot-YaYa](https://github.com/Yiwen-Chan/OneBot-YaYa) | [先驱](https://www.xianqubot.com/) | kanri |  |
| [richardchien/coolq-http-api](https://github.com/richardchien/coolq-http-api) | CKYU | richardchien | 可在 Mirai 平台使用 [mirai-native](https://github.com/iTXTech/mirai-native) 加载 |
| [Mrs4s/go-cqhttp](https://github.com/Mrs4s/go-cqhttp) | [MiraiGo](https://github.com/Mrs4s/MiraiGo) | Mrs4s |  |
| [yyuueexxiinngg/cqhttp-mirai](https://github.com/yyuueexxiinngg/cqhttp-mirai) | [Mirai](https://github.com/mamoe/mirai) | yyuueexxiinngg |  |
| [takayama-lily/onebot](https://github.com/takayama-lily/onebot) | [OICQ](https://github.com/takayama-lily/oicq) | takayama |  |

# Credits

* [OneBot](https://github.com/botuniverse/onebot)
* [pbbot-spring-boot-starter](https://github.com/ProtobufBot/pbbot-spring-boot-starter)
  Shiro开发时的参考实现，感谢 [lz1998](https://github.com/lz1998) 大佬的开源项目，本项目众多灵感正来源于此。

# License

This product is licensed under the GNU General Public License version 3. The license is as published by the Free
Software Foundation published at https://www.gnu.org/licenses/gpl-3.0.html.

Alternatively, this product is licensed under the GNU Lesser General Public License version 3 for non-commercial use.
The license is as published by the Free Software Foundation published at https://www.gnu.org/licenses/lgpl-3.0.html.

Feel free to contact us if you have any questions about licensing or want to use the library in a commercial closed
source product.

# Thanks

Thanks [JetBrains](https://www.jetbrains.com/?from=mirai) Provide Free License Support OpenSource Project

[<img src="https://mikuac.com/images/jetbrains-variant-3.png" width="200"/>](https://www.jetbrains.com/?from=mirai)

## Stargazers over time

[![Stargazers over time](https://starchart.cc/MisakaTAT/Shiro.svg)](https://starchart.cc/MisakaTAT/Shiro)[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FMisakaTAT%2FShiro.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FMisakaTAT%2FShiro?ref=badge_shield)