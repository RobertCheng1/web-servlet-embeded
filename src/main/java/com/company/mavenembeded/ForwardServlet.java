package com.company.mavenembeded;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Forward是指内部转发。
 * 当一个Servlet处理请求的时候，它可以决定自己不继续处理，而是转发给另一个Servlet处理。
 * 例如，我们已经编写了一个能处理/hello的HelloServlet，继续编写一个能处理/moha的 ForwardServlet：
 * ForwardServlet在收到请求后，它并不自己发送响应，而是把请求和响应都转发给路径为/hello的Servlet，
 * 后续请求的处理实际上是由HelloServlet完成的。
 *
 *                                ┌────────────────────────┐
 *                                │      ┌───────────────┐ │
 *                                │ ────>│ForwardServlet │ │
 *      ┌───────┐  GET /morning   │      └───────────────┘ │
 *      │Browser│ ──────────────> │              │         │
 *      │       │ <────────────── │              ▼         │
 *      └───────┘    200 <html>   │      ┌───────────────┐ │
 *                                │ <────│ HelloServlet  │ │
 *                                │      └───────────────┘ │
 *                                │       Web Server       │
 *                                └────────────────────────┘
 * 注意到使用转发的时候，浏览器的地址栏路径仍然是/moha，浏览器并不知道该请求在Web服务器内部实际上做了一次转发。
 */
@WebServlet(urlPatterns = "/moha")
public class ForwardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/hello").forward(req, resp);
    }
}

