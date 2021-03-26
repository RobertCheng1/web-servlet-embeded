package com.company.mavenembeded;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * 刚开始启动 main 的时候：报错：
 * Error: A JNI error has occurred, please check your installation and try again
 * Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/catalina/WebResourceRoot
 * 	    at java.lang.Class.getDeclaredMethods0(Native Method)
 * 	    at java.lang.Class.privateGetDeclaredMethods(Class.java:2701)
 * 	    at java.lang.Class.privateGetMethodRecursive(Class.java:3048)
 * 	    at java.lang.Class.getMethod0(Class.java:3018)
 * 	    at java.lang.Class.getMethod(Class.java:1784)
 * 	    at sun.launcher.LauncherHelper.validateMainClass(LauncherHelper.java:650)
 * 	    at sun.launcher.LauncherHelper.checkAndLoadMain(LauncherHelper.java:632)
 * Caused by: java.lang.ClassNotFoundException: org.apache.catalina.WebResourceRoot
 * 	    at java.net.URLClassLoader.findClass(URLClassLoader.java:382)
 * 	    at java.lang.ClassLoader.loadClass(ClassLoader.java:418)
 * 	    at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:355)
 * 	    at java.lang.ClassLoader.loadClass(ClassLoader.java:351)
 * 	    ... 7 more
 * 该节评论中有解决方法：
 * 1，问题产生的原因：廖大佬用的eclipse，我们用的IDEA，
 *   我们在IDEA中，maven配置<scope>provided</scope>，就告诉了IDEA程序会在运行的时候提供这个依赖，但是实际上却并没有提供这个依赖。
 * 2，解决方法： 去掉<scope>provided</scope>，改<scope>complie</scope>，然后re import就可以了。
 *
 * 廖老师亲自回复：那是idea的问题，如果你把provided改成compile，生成的war包会很大，因为把tomcat打包进去了
 * 终极解决方案：
 * 1. 打开idea的Run/Debug Configurations:
 * 2. 选择Application - Main
 * 3. 右侧Configuration：Use classpath of module
 *      钩上☑︎Include dependencies with "Provided" scope
 *
 *
 * 许多初学者经常卡在如何在IDE中启动Tomcat并加载webapp，更不要说断点调试了。我们需要一种简单可靠，能直接在IDE中启动并调试webapp的方法。
 * 因为Tomcat实际上也是一个Java程序，我们看看Tomcat的启动流程：
 *     启动JVM并执行Tomcat的main()方法；
 *     加载war并初始化Servlet；
 *     正常服务。
 * 启动Tomcat无非就是设置好classpath并执行Tomcat某个jar包的main()方法，我们完全可以把Tomcat的jar包全部引入进来，
 * 然后自己编写一个main()方法，先启动Tomcat，然后让它加载我们的webapp就行。
 *
 *
 *
 * 浏览器发出的HTTP请求总是由Web Server先接收，然后，根据Servlet配置的映射，不同的路径转发到不同的Servlet：
 *                     ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
 *
 *                     │            /hello    ┌───────────────┐│
 *                                ┌──────────>│ HelloServlet  │
 *                     │          │           └───────────────┘│
 *      ┌───────┐    ┌──────────┐ │ /signin   ┌───────────────┐
 *      │Browser│───>│Dispatcher│─┼──────────>│ SignInServlet ││
 *      └───────┘    └──────────┘ │           └───────────────┘
 *                     │          │ /         ┌───────────────┐│
 *                                └──────────>│ IndexServlet  │
 *                     │                      └───────────────┘│
 *                                    Web Server
 *                     └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
 * 这种根据路径转发的功能我们一般称为Dispatch。映射到/的IndexServlet比较特殊，它实际上会接收所有未匹配的路径，相当于/*，因为Dispatcher的逻辑可以用伪代码实现如下：
 *
 * HttpServletRequest:
 * HttpServletRequest封装了一个HTTP请求，它实际上是从ServletRequest继承而来。
 * 最早设计Servlet时，设计者希望Servlet不仅能处理HTTP，也能处理类似SMTP等其他协议，
 * 因此，单独抽出了ServletRequest接口，但实际上除了HTTP外，并没有其他协议会用Servlet处理，所以这是一个过度设计。
 * 我们通过HttpServletRequest提供的接口方法可以拿到HTTP请求的几乎全部信息，常用的方法有：
 *     getMethod()：返回请求方法，例如，"GET"，"POST"；
 *     getRequestURI()：返回请求路径，但不包括请求参数，例如，"/hello"；
 *     getQueryString()：返回请求参数，例如，"name=Bob&a=1&b=2"；
 *     getParameter(name)：返回请求参数，GET请求从URL读取参数，POST请求从Body中读取参数；
 *     getContentType()：获取请求Body的类型，例如，"application/x-www-form-urlencoded"；
 *     getContextPath()：获取当前Webapp挂载的路径，对于ROOT来说，总是返回空字符串""；
 *     getCookies()：返回请求携带的所有Cookie；
 *     getHeader(name)：获取指定的Header，对Header名称不区分大小写；
 *     getHeaderNames()：返回所有Header名称；
 *     getInputStream()：如果该请求带有HTTP Body，该方法将打开一个输入流用于读取Body；
 *     getReader()：和getInputStream()类似，但打开的是Reader；
 *     getRemoteAddr()：返回客户端的IP地址；
 *     getScheme()：返回协议类型，例如，"http"，"https"；
 * 此外，HttpServletRequest还有两个方法：setAttribute()和getAttribute()，可以给当前 HttpServletRequest 对象附加多个Key-Value，
 * 相当于把HttpServletRequest当作一个Map<String, Object>使用。
 * 调用HttpServletRequest的方法时，注意务必阅读接口方法的文档说明，因为有的方法会返回null，例如getQueryString()的文档就写了：
 *      ... This method returns null if the URL does not have a query string...
 *
 *
 * HttpServletResponse:
 * HttpServletResponse封装了一个HTTP响应。由于HTTP响应必须先发送Header，再发送Body，所以，操作HttpServletResponse对象时，必须先调用设置Header的方法，最后调用发送Body的方法。
 * 常用的设置Header的方法有：
 *     setStatus(sc)：设置响应代码，默认是200；
 *     setContentType(type)：设置Body的类型，例如，"text/html"；
 *     setCharacterEncoding(charset)：设置字符编码，例如，"UTF-8"；
 *     setHeader(name, value)：设置一个Header的值；
 *     addCookie(cookie)：给响应添加一个Cookie；
 *     addHeader(name, value)：给响应添加一个Header，因为HTTP协议允许有多个相同的Header；
 * 写入响应时，需要通过getOutputStream()获取写入流，或者通过getWriter()获取字符流，二者只能获取其中一个。
 * 写入响应前，无需设置setContentLength()，因为底层服务器会根据写入的字节数自动设置，
 * 如果写入的数据量很小，实际上会先写入缓冲区，
 * 如果写入的数据量很大，服务器会自动采用Chunked编码让浏览器能识别数据结束符而不需要设置Content-Length头。
 *
 * 但是，写入完毕后调用flush()却是必须的，因为大部分Web服务器都基于HTTP/1.1协议，会复用TCP连接。
 * 如果没有调用flush()，将导致缓冲区的内容无法及时发送到客户端。此外，
 * 写入完毕后千万不要调用 close()，原因同样是因为会复用TCP连接，如果关闭写入流，将关闭TCP连接，使得Web服务器无法复用此TCP连接。
 * 写入完毕后对输出流调用 flush() 而不是 close() 方法！===真是满满的细节===
 * 有了HttpServletRequest和HttpServletResponse这两个高级接口，我们就不需要直接处理HTTP协议。
 * 注意到具体的实现类是由各服务器提供的，而我们编写的Web应用程序只关心接口方法，并不需要关心具体实现的子类。
 *
 *
 * Servlet多线程模型: from: Web开发--Servlet进阶
 * 一个Servlet类在服务器中只有一个实例，但对于每个HTTP请求，Web服务器会使用多线程执行请求。
 * 因此，一个Servlet的doGet()、doPost()等处理请求的方法是多线程并发执行的。如果Servlet中定义了字段，要注意多线程并发访问的问题：
 *      public class HelloServlet extends HttpServlet {
 *          private Map<String, String> map = new ConcurrentHashMap<>();
 *
 *          protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
 *              // 注意读写map字段是多线程并发的:
 *              this.map.put(key, value);
 *          }
 *      }
 * 对于每个请求，Web服务器会创建唯一的HttpServletRequest和HttpServletResponse实例，
 * 因此，HttpServletRequest和HttpServletResponse实例只有在当前处理线程中有效，它们总是局部变量，不存在多线程共享的问题。
 *
 *
 * 使用Session和Cookie: from: Web开发--Servlet进阶--使用Session和Cookie
 *     在使用多台服务器构成集群时，使用Session会遇到一些额外的问题。通常，多台服务器集群使用反向代理作为网站入口：
 *                                              ┌────────────┐
 *                                         ┌───>│Web Server 1│
 *                                         │    └────────────┘
 *         ┌───────┐     ┌─────────────┐   │    ┌────────────┐
 *         │Browser│────>│Reverse Proxy│───┼───>│Web Server 2│
 *         └───────┘     └─────────────┘   │    └────────────┘
 *                                         │    ┌────────────┐
 *                                         └───>│Web Server 3│
 *                                              └────────────┘
 *     如果多台Web Server采用无状态集群，那么反向代理总是以轮询方式将请求依次转发给每台Web Server，这会造成一个用户在Web Server 1存储的Session信息，
 *     在Web Server 2和3上并不存在，即从Web Server 1登录后，如果后续请求被转发到Web Server 2或3，那么用户看到的仍然是未登录状态。
 *     要解决这个问题:
 *     方案一是在所有Web Server之间进行Session复制，但这样会严重消耗网络带宽，并且，每个Web Server的内存均存储所有用户的Session，内存使用率很低。
 *     方案二是采用粘滞会话（Sticky Session）机制，即反向代理在转发请求的时候，总是根据JSESSIONID的值判断，相同的JSESSIONID总是转发到固定的Web Server，但这需要反向代理的支持。
 *     无论采用何种方案，使用Session机制，会使得Web Server的集群很难扩展，
 *     因此，Session适用于中小型Web应用程序。对于大型Web应用程序来说，通常需要避免使用Session机制。
 *
 *
 * JSP是Java Server Pages的缩写，它的文件必须放到/src/main/webapp下，文件名必须以.jsp结尾，
 * 整个文件与HTML并无太大区别，但需要插入变量，或者动态输出的地方，使用特殊指令<% ... %>。
 * 
 * MVC模式是一种分离业务逻辑和显示逻辑的设计模式，广泛应用在Web和桌面应用程序。
 *
 * 为了把一些公用逻辑从各个Servlet中抽离出来，JavaEE的Servlet规范还提供了一种Filter组件，即过滤器，它的作用是，
 * 在HTTP请求到达Servlet之前，可以被一个或多个Filter预处理，类似打印日志、登录检查等逻辑，完全可以放到Filter中。
 * 编写Filter时，必须实现Filter接口，在doFilter()方法内部，要继续处理请求，必须调用chain.doFilter()
 * 添加了Filter之后，整个请求的处理架构如下：
 *                  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
 *                                         /             ┌──────────────┐
 *                  │                     ┌─────────────>│ IndexServlet │ │
 *                                        │              └──────────────┘
 *                  │                     │/signin       ┌──────────────┐ │
 *                                        ├─────────────>│SignInServlet │
 *                  │                     │              └──────────────┘ │
 *                                        │/signout      ┌──────────────┐
 *      ┌───────┐   │   ┌──────────────┐  ├─────────────>│SignOutServlet│ │
 *      │Browser│──────>│EncodingFilter├──┤              └──────────────┘
 *      └───────┘   │   └──────────────┘  │/user/profile ┌──────────────┐ │
 *                                        ├─────────────>│ProfileServlet│
 *                  │                     │              └──────────────┘ │
 *                                        │/user/post    ┌──────────────┐
 *                  │                     ├─────────────>│ PostServlet  │ │
 *                                        │              └──────────────┘
 *                  │                     │/user/reply   ┌──────────────┐ │
 *                                        └─────────────>│ ReplyServlet │
 *                  │                                    └──────────────┘ │
 *                   ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // 启动Tomcat:
        System.out.println("aaaaaaaaaa");
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.getInteger("port", 8090));
        tomcat.getConnector();
        // 创建webapp:
        Context ctx = tomcat.addWebapp("", new File("src/main/webapp").getAbsolutePath());
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(
                new DirResourceSet(resources, "/WEB-INF/classes", new File("target/classes").getAbsolutePath(), "/"));
        ctx.setResources(resources);
        tomcat.start();
        tomcat.getServer().await();
    }
}
