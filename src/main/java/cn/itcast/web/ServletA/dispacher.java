package cn.itcast.web.ServletA;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class dispacher extends BaseServlet{
    public String fun(HttpServletRequest request, HttpServletResponse response)
    {
        return "forward:/index.jsp";

    }
}
