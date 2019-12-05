package com.mage.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mage.po.User;

/**
 * 非法访问拦截
 */
@WebFilter("/*")
public class LoginFilter2 implements Filter {


    public LoginFilter2() {
        // TODO Auto-generated constructor stub
    }


	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		// 基于Http
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
		// 从站点名到?前
		String uri = request.getRequestURI();
		System.out.println(uri);
		
		// 拦截什么
		if(!(uri.contains("login.jsp")||uri.contains("statics")||uri.contains("user")||uri.contains("commons.jsp"))){
			// 判断是否登录
			User user = (User)request.getSession().getAttribute("user");
			if(user==null){
				response.sendRedirect("/wc/login.jsp");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
