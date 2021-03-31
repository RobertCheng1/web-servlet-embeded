package com.company.mavenembeded.listener;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 除了Servlet和Filter外，JavaEE的Servlet规范还提供了第三种组件：Listener。
 * Listener顾名思义就是监听器，有好几种Listener，其中最常用的是ServletContextListener
 *
 * 任何标注为@WebListener，且实现了特定接口的类会被Web服务器自动初始化。
 * 上述AppListener实现了 ServletContextListener 接口，它会在整个Web应用程序初始化完成后，以及Web应用程序关闭后获得回调通知。我们可以
 * 把初始化数据库连接池等工作放到 contextInitialized() 回调方法中，
 * 把清理资源的工作放到 contextDestroyed() 回调方法中，
 * 因为Web服务器保证在contextInitialized()执行后，才会接受用户的HTTP请求。
 *
 *
 * 很多第三方Web框架都会通过一个 ServletContextListener 接口初始化自己。
 * 除了 ServletContextListener 外，还有几种Listener：
 *     HttpSessionListener：监听HttpSession的创建和销毁事件；
 *     ServletRequestListener：监听ServletRequest请求的创建和销毁事件；
 *     ServletRequestAttributeListener：监听ServletRequest请求的属性变化事件（即调用ServletRequest.setAttribute()方法）；
 *     ServletContextAttributeListener：监听ServletContext的属性变化事件（即调用ServletContext.setAttribute()方法）；
 *
 * ServletContext
 * 一个Web服务器可以运行一个或多个WebApp，对于每个WebApp，Web服务器都会为其创建一个全局唯一的 ServletContext 实例，===这一点很重要===
 * 我们在 AppListener里面编写的两个回调方法实际上对应的就是 ServletContext 实例的创建和销毁：
 *      public void contextInitialized(ServletContextEvent sce) {
 *          System.out.println("WebApp initialized: ServletContext = " + sce.getServletContext());
 *      }
 * ServletRequest、HttpSession等很多对象也提供 getServletContext()方法获取到同一个ServletContext实例。
 * ServletContext实例最大的作用就是设置和共享全局信息。
 * 此外，ServletContext还提供了动态添加Servlet、Filter、Listener等功能，它允许应用程序在运行期间动态添加一个组件，虽然这个功能不是很常用
 */
@WebListener
public class AppListener implements ServletContextListener {
    // 在此初始化WebApp,例如打开数据库连接池等:
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("In the contextInitialized: WebApp initialized.");
    }

    // 在此清理WebApp,例如关闭数据库连接池等:
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("In the contextDestroyed: WebApp destroyed.");
    }
}

