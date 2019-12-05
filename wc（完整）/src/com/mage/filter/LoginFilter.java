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
/*@WebFilter("/*")*/
public class LoginFilter implements Filter {


    public LoginFilter() {
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
		
		// 放行静态资源
		if(uri.contains("statics")){
			chain.doFilter(request, response);
			return;
		}
		
		// 放行指定页面    
		if(uri.contains("login.jsp")){
			chain.doFilter(request, response);
			return;
		}
		
		// 放行指定行为
		if(uri.indexOf("user")>-1){
			chain.doFilter(request, response);
			return;
		}
		
		// 判断用户是否登录，登录了则放行
		User user = (User)request.getSession().getAttribute("user");
		if(user != null){
			chain.doFilter(request, response);
			return;
		}
		
		// 跳转到登录页面
		response.sendRedirect("/wc/login.jsp");
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
