package cn.itcast.web.ServletA;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * BaseServlet
 * 我们希望在一个Servlet中处理多个请求
 * 创建Servlet时继承本类而不是继承HttpServlet，重写service方法
 * 客户端在发起请求时需要传递method参数来判断调用哪个方法
 * eg：http://localhost:8080/test/UserServlet?method=login
 * 返回值"f:/xxx"为转发、"r:/xxx"为重定向、"d:/xxx"为下载
 * 重定向可以重定向到其他项目中，写法："r:/192.168.11.24:8080/example/index.jsp"
 * 下载可以下载服务器中目录下的文件 "d:/WEB-INF/a.jpg"
 * 也可以下载磁盘绝对路径下的文件 "d:/G:/a.jpg"
 * @author hui.zhang
 *
 */
@SuppressWarnings("serial")
public class BaseServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");//处理响应编码

        //获取传递的method参数
        String methodName = request.getParameter("method");
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new RuntimeException("没有传递method参数，不能确定要调用的方法！");
        }
        //得到当前类的class对象
        Class c  = getClass();
        Method method = null;
        try {
            method = c.getMethod(methodName,
                    HttpServletRequest.class,HttpServletResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("要调用的方法："+methodName+"(HttpServletRequest,HttpServletResponse)，不存在！",e);
        }
        //调用method表示的方法
        try {
            String result = (String)method.invoke(this, request,response);
            /*
             * 获取请求处理方法执行后返回的字符串，它表示转发或重定向的路径
             * 如果返回为null或者""，什么也不做
             * 判断返回值中是否存在冒号，如果没有默认转发，因为转发的情况较多
             * 如果有冒号，分割，得到前缀f表示forward，前缀r表示redirect
             * 后缀为要转发或重定向的路径
             */
            if (result == null || result.trim().isEmpty()) {
                return;
            }
            if (result.contains(":")) {  //"f:/index.jsp"
                int index = result.indexOf(":");
                String s = result.substring(0, index); //f
                String path = result.substring(index+1); // "/index.jsp"
                if (s.equalsIgnoreCase("r")) {
                    // "/192.168.11.24:8080/example/index.jsp"
                    if (path.contains(":")) { //有:说明有端口号是其他项目的路径
                        if (path.contains("http")) {
                            // "/http://192.168.11.24:8080/example/index.jsp"
                            response.sendRedirect(path.substring(1));
                        } else {
                            response.sendRedirect("http://"+path.substring(1));
                        }
                    } else {
                        response.sendRedirect(request.getContextPath()+path);
                    }
                } else if (s.equalsIgnoreCase("f")) {
                    request.getRequestDispatcher(path).forward(request, response);
                } else if (s.equalsIgnoreCase("d")) { //表示下载
                    /**
                     * 两个头一个流
                     * 1. Content-Type
                     * 2. Content-Disposition
                     * 3. 流：下载文件的数据
                     */
//                    path = "/WEB-INF/mp3/自娱自乐.mp3";
//                    path = "/G://a.jpg";
                    int indexOf = path.lastIndexOf("/");
                    String name = path.substring(indexOf); // /自娱自乐.mp3
                    //如果包含:，说明是绝对路径
                    String filename = null;
                    if (path.contains(":")) {
                        filename = path.substring(1);
                    } else { //说明是服务器端文件，需要获得绝对路径
                        //获得文件的绝对路径
                        filename = this.getServletContext().getRealPath(path);
                    }
                    //去掉文件名前的/
                    name = name.substring(1); // 自娱自乐.mp3
                    //通过DownUtils工具类处理不同浏览器下载时中文名乱码问题
                    String framename = DownUtils.filenameEncoding(name, request);
                    //头1：获得要下载的文件MIME类型
                    String contentType = this.getServletContext().getMimeType(filename);
                    //头2：ContentDisposition
                    String contentDisposition = "attachment;filename="+framename;
                    FileInputStream input = new FileInputStream(filename);
                    response.setHeader("Content-Type", contentType);
                    response.setHeader("Content-Disposition",contentDisposition);
                    ServletOutputStream output = response.getOutputStream();
                    IOUtils.copy(input, output);
                    input.close();
                    output.close();
                } else {
                    throw new RuntimeException("操作："+s+"目前还不支持！");
                }

            } else { //默认转发
                request.getRequestDispatcher(result).forward(request, response);
            }
        } catch (Exception e) {
            throw new RuntimeException("调用的方法："+methodName+"(HttpServletRequest,HttpServletResponse)，内部抛出异常！", e);
        }

    }

}
