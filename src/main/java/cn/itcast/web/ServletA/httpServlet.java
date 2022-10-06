package cn.itcast.web.ServletA;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "httpServlet", value = "/httpServlet")
public class httpServlet extends HttpServlet {
    @Override//不重写doget dopost  会返回405
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        System.out.println("doGet");
        ServletContext servletContext = this.getServletContext();
        servletContext.getAttribute("count");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
        System.out.println("doPost……");
    }
}
