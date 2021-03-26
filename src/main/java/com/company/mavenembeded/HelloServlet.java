package com.company.mavenembeded;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {
    // 通过该变量可以证明： 一个Servlet类在服务器中只有一个实例，但对于每个HTTP请求，Web服务器会使用多线程执行请求
    // 通过多次访问 http://127.0.0.1:8090/hello 即可看到效果
    private int count = 0;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("In the doGet for hello");
        this.count = this.count + 1;
        resp.setContentType("text/html");
        System.out.println("req.getRequestURI() = " + req.getRequestURI());
        System.out.println("req.getContextPath() = " + req.getContextPath());
        String name = req.getParameter("name");
        if (name == null) {
            name = "world " + this.count;
        }
        PrintWriter pw = resp.getWriter();
        pw.write("<h1>Hello, " + name + "!</h1>");
        pw.flush();
    }
}