package cn.itcast.web.ServletA;

import sun.misc.IOUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "bServlet", value = "/bServlet")
public class bServlet extends HttpServlet {
    /*
    * 演示获取类路径下资源
    * 1.得到 classLoader，2. 调用其 getResourceAsstream(), 得到一个 Input stream
    * */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
//        ClassLoader cl = this.getClass().getClassLoader();
//        InputStream in= cl.getResourceAsStream("a.txt");
//        String s = IOUtils.toString(in);//读取输入流内容，转换成字符串返回
        /*
        * //相对/classes,a.txt 表示classes所在目录，/a.txt是classes的布鲁
        * */

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
}
