package com.bysj_backend.config;


import com.alibaba.fastjson.JSON;
import com.bysj_backend.utils.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登录
 */
@Slf4j
@WebFilter(filterName = "filterConfig",urlPatterns = "/*")
public class FilterConfig implements Filter {

    //路劲匹配器,支持通配符
    public static  final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        OperationResult<String> operationResult = new OperationResult<>();

        //1.获取本次请求的url
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求路劲
        String [] urls = new String[] {
                "/",
                "/index"
        };
        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3.如果不需要处理则直接放行
        if (check){
            filterChain.doFilter(request,response);
            return;
        }
        //4-1.判断后台登录状态,如果已登录,则直接放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录,当前用户id为:{}",request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return;
        }


        if (request.getSession().getAttribute("administrator") != null){
            log.info("管理员已登录,管理员id为:{}",request.getSession().getAttribute("administrator"));
            filterChain.doFilter(request,response);
            return;
        }


        //5.如果未登录则返回登录结果,通过输出流的方式向客户端响应数据
        //返回NOTLOGIN这个 前端绑定的参数后,前端会自己去做判断跳转login页面
        response.getWriter().write(JSON.toJSONString(operationResult.setErrorCode("NOTLOGIN")));
        return;
    }


    /**
     * 路劲匹配,检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String [] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }


}
