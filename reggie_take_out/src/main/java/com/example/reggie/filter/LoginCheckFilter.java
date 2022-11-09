package com.example.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、判断是否需要拦截
        //需要放行的路径
        String[] urls = new String[]{"/employee/login", "/employee/logout",
                "/backend/**", "/front/**", "/common/**",
                "/user/sendMsg", "/user/login"};
        String uri = request.getRequestURI();
        if(check(urls, uri)) { //放行
            filterChain.doFilter(request, response);
            return;
        }

        //2-1、判断是否已经登录(后台)
        if(request.getSession().getAttribute("employee") != null) {
            //通过ThreadLocal赋予id值
            Long id = (Long) request.getSession().getAttribute("employee");
            BaseContext.setId(id);

            //已登录，放行
            filterChain.doFilter(request, response);
            return;
        }

        //2-2、判断是否已经登录(手机用户端)
        if(request.getSession().getAttribute("user") != null) {
            //通过ThreadLocal赋予id值
            Long id = (Long) request.getSession().getAttribute("user");
            BaseContext.setId(id);

            //已登录，放行
            filterChain.doFilter(request, response);
            return;
        }

        log.info("拦截到请求:{}", request.getRequestURI());
        log.info("用户未登录");

        //3、未登录，拦截，并返回json对象
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，判断该路径是否需要放行
     * @param urls
     * @param uri
     * @return
     */
    public boolean check(String[] urls, String uri) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, uri);
            if(match) {
                return true;
            }
        }
        return false;
    }
}