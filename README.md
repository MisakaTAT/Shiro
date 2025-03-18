<p align="center">
  <img src=".github/assets/logo.png" width="200" height="200" alt="Shiro">
</p>

<div align="center">

# Shiro

_✨ 基于 [OneBot](https://github.com/howmanybots/onebot/blob/master/README.md) 协议的 QQ机器人 快速开发框架 ✨_

</div>

<p align="center">
    <a href="https://search.maven.org/search?q=com.mikuac.shiro"><img src="https://img.shields.io/maven-central/v/com.mikuac/shiro.svg?label=Maven%20Central&style=flat-square" alt="maven" /></a>
    <a href="https://github.com/MisakaTAT/Shiro/issues"><img src="https://img.shields.io/github/issues/MisakaTAT/Shiro?style=flat-square" alt="issues" /></a>
    <a href="https://github.com/MisakaTAT/Shiro/blob/main/LICENSE"><img src="https://img.shields.io/github/license/MisakaTAT/Shiro?style=flat-square" alt="license"></a>
    <img src="https://img.shields.io/badge/JDK-17+-brightgreen.svg?style=flat-square" alt="jdk-version">
    <a href=""><img src="https://img.shields.io/badge/QQ群-174706945-brightgreen.svg?style=flat-square" alt="qq-group"></a>
    <a href="https://github.com/howmanybots/onebot"><img src="https://img.shields.io/badge/OneBot-v11-blue?style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABABAMAAABYR2ztAAAAIVBMVEUAAAAAAAADAwMHBwceHh4UFBQNDQ0ZGRkoKCgvLy8iIiLWSdWYAAAAAXRSTlMAQObYZgAAAQVJREFUSMftlM0RgjAQhV+0ATYK6i1Xb+iMd0qgBEqgBEuwBOxU2QDKsjvojQPvkJ/ZL5sXkgWrFirK4MibYUdE3OR2nEpuKz1/q8CdNxNQgthZCXYVLjyoDQftaKuniHHWRnPh2GCUetR2/9HsMAXyUT4/3UHwtQT2AggSCGKeSAsFnxBIOuAggdh3AKTL7pDuCyABcMb0aQP7aM4AnAbc/wHwA5D2wDHTTe56gIIOUA/4YYV2e1sg713PXdZJAuncdZMAGkAukU9OAn40O849+0ornPwT93rphWF0mgAbauUrEOthlX8Zu7P5A6kZyKCJy75hhw1Mgr9RAUvX7A3csGqZegEdniCx30c3agAAAABJRU5ErkJggg=="></a>
</p>

<p align="center">
  <a href="https://misakatat.github.io/shiro-docs">文档</a>
  ·
  <a href="https://github.com/MisakaTAT/Shiro/releases">下载</a>
  ·
  <a href="https://misakatat.github.io/shiro-docs">快速开始</a>
  ·
  <a href="">参与贡献</a>
</p>

<div align="center">

[![Repobeats analytics image](https://repobeats.axiom.co/api/embed/c0b4ba71b13fe79015de15fb9396651f97f3acf9.svg "Repobeats analytics image")](https://github.com/MisakaTAT/Shiro/pulse)

</div>

# Migration Guide

> 从 `v2` 版本开始仅支持 `JDK 17+` 与 `SpringBoot 3.0.0+`
>
>详见项目文档 [v2迁移指南](https://misakatat.github.io/shiro-docs/migration.html)

# QuickStart

## 依赖引入

> 引入依赖时请替换版本 `latest` 为 `Maven Central` 实际的最新版本

### Maven

```xml

<dependency>
    <groupId>com.mikuac</groupId>
    <artifactId>shiro</artifactId>
    <version>latest</version>
</dependency>
```

### Gradle Kotlin DSL

```kotlin
implementation("com.mikuac:shiro:latest")
```

### Gradle Groovy DSL

```groovy
implementation 'com.mikuac:shiro:latest'
```

## 示例插件

### 注解调用

> 编写 `application.yaml` 配置文件
> 或参考 [进阶配置文件](https://misakatat.github.io/shiro-docs/advanced.html#进阶配置文件)

```yaml
server:
  port: 5000
```

```java

@Shiro
@Component
public class ExamplePlugin {
    // 更多用法详见 @MessageHandlerFilter 注解源码

    // 当机器人收到的私聊消息消息符合 cmd 值 "hi" 时，这个方法会被调用。
    @PrivateMessageHandler
    @MessageHandlerFilter(cmd = "hi")
    public void fun1(Bot bot, PrivateMessageEvent event, Matcher matcher) {
        // 构建消息
        String sendMsg = MsgUtils.builder().face(66).text("Hello, this is shiro demo.").build();
        // 发送私聊消息
        bot.sendPrivateMsg(event.getUserId(), sendMsg, false);
    }

    // 如果 at 参数设定为 AtEnum.NEED 则只有 at 了机器人的消息会被响应
    @GroupMessageHandler
    @MessageHandlerFilter(at = AtEnum.NEED)
    public void fun2(GroupMessageEvent event) {
        // 以注解方式调用可以根据自己的需要来为方法设定参数
        // 例如群组消息可以传递 GroupMessageEvent, Bot, Matcher 多余的参数会被设定为 null
        System.out.println(event.getMessage());
    }

    // 同时监听群组及私聊消息 并根据消息类型（私聊，群聊）回复
    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "say hello")
    public void fun3(Bot bot, AnyMessageEvent event) {
        bot.sendMsg(event, "hello", false);
    }
}
```

### 重写父类方法

- 注解方式编写的插件无需在插件列表 `plugin-list`定义
- 服务端配置文件 `resources/application.yaml` 追加如下内容
- 插件列表为顺序执行，如果前一个插件返回了 `MESSAGE_BLOCK` 将不会执行后续插件

> 编写 `application.yaml` 配置文件
> 或参考 [进阶配置文件](https://misakatat.github.io/shiro-docs/advanced.html#进阶配置文件)

```yaml
server:
  port: 5000
shiro:
  plugin-list:
    - com.example.bot.plugins.ExamplePlugin
```

```java
@Component
public class ExamplePlugin extends BotPlugin {

    @Override
    public int onPrivateMessage(Bot bot, PrivateMessageEvent event) {
        if ("hi".equals(event.getMessage())) {
            // 构建消息
            String sendMsg = MsgUtils.builder()
                    .face(66)
                    .text("hello, this is shiro example plugin.")
                    .build();
            // 发送私聊消息
            bot.sendPrivateMsg(event.getUserId(), sendMsg, false);
        }
        // 返回 MESSAGE_IGNORE 执行 plugin-list 下一个插件，返回 MESSAGE_BLOCK 则不执行下一个插件
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        if ("hi".equals(event.getMessage())) {
            // 构建消息
            String sendMsg = MsgUtils.builder()
                    .at(event.getUserId())
                    .face(66)
                    .text("hello, this is shiro example plugin.")
                    .build();
            // 发送群消息
            bot.sendGroupMsg(event.getGroupId(), sendMsg, false);
        }
        // 返回 MESSAGE_IGNORE 执行 plugin-list 下一个插件，返回 MESSAGE_BLOCK 则不执行下一个插件
        return MESSAGE_IGNORE;
    }

}
```

### 加载外部插件

#### 目录结构
Shiro 支持自动加载 .jar 格式的插件，并通过 ServiceLoader 进行管理。默认情况下，Shiro 会扫描当前运行路径下的 plugins 目录，并尝试加载所有符合 BotPlugin 接口的插件。

以下只是一个示例结构（可根据实际情况调整，比如替换 Gradle 为 Maven）
```
ForeignPluginExample/
├── src/                                                                  # 源代码目录
│   ├── main/java/com/mikuac/demo/DemoPlugin.java                         # 插件实现
│   ├── main/resources/META-INF/services/com.mikuac.shiro.core.BotPlugin  # SPI 注册文件
├── build.gradle.kts            # Gradle 构建脚本
├── settings.gradle.kts         # Gradle 设置文件
├── gradlew                     # Gradle 可执行文件（Linux/macOS）
├── gradlew.bat                 # Gradle 可执行文件（Windows）
└── gradle/wrapper/             # Gradle Wrapper 相关文件
```

#### 开发指南

##### 插件类定义

插件必须实现 BotPlugin 接口，并使用 @Component 注解，以便 Shiro 能够正确识别。

```java
package com.mikuac.demo;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

@Component
public class DemoPlugin extends BotPlugin {
    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        String msg = event.getMessage();
        if (msg.equals("ping")) {
            bot.sendGroupMsg(event.getGroupId(), "pong", false);
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }
}
```
##### 配置 META-INF/services

为了让 ServiceLoader 能够发现插件，需要在 src/main/resources/META-INF/services/ 目录下创建 com.mikuac.shiro.core.BotPlugin 文件，并填写插件的完整类名。

```
com.mikuac.demo.DemoPlugin
```

##### 编译插件

```
./gradlew build
```

生成的插件 JAR 文件位于 build/libs/DemoPlugin-1.0-SNAPSHOT.jar，需要将其移动到 Shiro 的 `plugins` 目录中。

##### 重新启动 Shiro 以加载插件

Shiro 在启动时会自动扫描 `plugins` 目录，并加载符合条件的插件。

##### 相关配置
Shiro 的 `application.yml` 中可以自定义插件目录:

```yml
shiro:
  pluginScanPath: "/home/user/mybot"
```
这样，Shiro 将从 `/home/user/mybot` 目录加载插件，而不是默认的 `plugins`。

# Client

Shiro 以 [OneBot-v11](https://github.com/howmanybots/onebot/tree/master/v11/specs)
标准协议进行开发，兼容所有支持反向WebSocket的OneBot协议客户端

| 项目                                                        | 描述                                              | 备注     |
| ----------------------------------------------------------- | ------------------------------------------------- | -------- |
| [LLOneBot](https://github.com/LLOneBot/LLOneBot)            | 使你的 NTQQ 支持 OneBot11 协议进行 QQ 机器人开发  |          |
| [Lagrange.Core](https://github.com/KonataDev/Lagrange.Core) | NTQQ 的协议实现                                   |          |
| [go-cqhttp](https://github.com/Mrs4s/go-cqhttp)             | 基于 Mirai 以及 MiraiGo 的 OneBot Golang 原生实现 | 停止维护 |
| [OpenShamrock](https://github.com/whitechi73/OpenShamrock)  | 基于 Xposed 实现 OneBot 标准的机器人框架          |          |



# Contributors
See [Contributing](https://github.com/MisakaTAT/Shiro/graphs/contributors) for details. Thanks to all the people who already contributed!

[![contributors](https://stg.contrib.rocks/image?repo=MisakaTAT/Shiro)](https://github.com/MisakaTAT/Shiro/graphs/contributors)

# Credits

* [OneBot](https://github.com/botuniverse/onebot)
* [pbbot-spring-boot-starter](https://github.com/ProtobufBot/pbbot-spring-boot-starter)

# License

This product is licensed under the GNU General Public License version 3. The license is as published by the Free
Software Foundation published at https://www.gnu.org/licenses/gpl-3.0.html.

Alternatively, this product is licensed under the GNU Lesser General Public License version 3 for non-commercial use.
The license is as published by the Free Software Foundation published at https://www.gnu.org/licenses/lgpl-3.0.html.

Feel free to contact us if you have any questions about licensing or want to use the library in a commercial closed
source product.

# Thanks

[JetBrains](https://www.jetbrains.com/?from=Shiro) offers free licenses to support open source projects.

[<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.png" width="280"/>](https://www.jetbrains.com/?from=shiro)

# Stargazers over time

[![Stargazers over time](https://starchart.cc/MisakaTAT/Shiro.svg)](https://starchart.cc/MisakaTAT/Shiro)
