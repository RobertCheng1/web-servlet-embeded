package com.company.mavenembeded;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 重定向有什么作用？
 * 重定向的目的是当Web应用升级后，如果请求路径发生了变化，可以将原来的路径重定向到新路径，从而避免浏览器请求原路径找不到资源。
 *
 * 如果浏览器发送GET /hi请求，RedirectServlet将处理此请求。由于RedirectServlet在内部又发送了重定向响应，
 * 因此，浏览器会收到如下响应：
 *      HTTP/1.1 302 Found
 *      Location: /hello
 * 当浏览器收到302响应后，它会立刻根据Location的指示发送一个新的GET /hello请求，这个过程就是重定向：
 *      ┌───────┐   GET /hi     ┌───────────────┐
 *      │Browser│ ────────────> │RedirectServlet│
 *      │       │ <──────────── │               │
 *      └───────┘   302         └───────────────┘
 *      
 *      
 *      ┌───────┐  GET /hello   ┌───────────────┐
 *      │Browser│ ────────────> │ HelloServlet  │
 *      │       │ <──────────── │               │
 *      └───────┘   200 <html>  └───────────────┘
 * 观察Chrome浏览器的网络请求，可以看到两次HTTP请求, 并且浏览器的地址栏路径自动更新为/hello。
 *
 * 重定向有两种：一种是302响应，称为临时重定向，一种是301响应，称为永久重定向。
 * 两者的区别是，如果服务器发送301永久重定向响应，浏览器会缓存/hi到/hello这个重定向的关联，下次请求/hi的时候，浏览器就直接发送/hello请求了。
 */
@WebServlet(urlPatterns = "/hi")
public class RedirectServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Case1: 临时重定向
        // 构造重定向的路径:
        System.out.println("In the doGet for SC_MOVED_TEMPORARILY");
        String name = req.getParameter("name");
        String redirectToUrl = "/hello" + (name == null ? "" : "?name=" + name);
        // 发送重定向响应:
        resp.sendRedirect(redirectToUrl);
        System.out.println("------------11---");
        resp.setStatus(HttpServletResponse.SC_OK);
        System.out.println("------------22---");
        // ToDo:再执行下面的语句会报错：Cannot call sendRedirect() after the response has been committed, 这句话来自哪里？？？
        // resp.sendRedirect("/moha");
    }

    // protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    //     // Case2: 永久重定向
    //     // 如果服务器发送301永久重定向响应，浏览器会缓存/hi到/hello这个重定向的关联，下次请求/hi的时候，浏览器就直接发送/hello请求了。
    //     System.out.println("In the doGet for SC_MOVED_PERMANENTLY");
    //     resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
    //     resp.setHeader("Location", "/hello");
    // }
}

