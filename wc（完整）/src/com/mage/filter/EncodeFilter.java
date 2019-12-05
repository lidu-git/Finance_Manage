package com.mage.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * GET：
	请求：
	   	Tomcat8及以上，不需要处理
	   	Tomcat7及以下，需要处理乱码
	   	new String(request.getParameter(name).getBytes("ISO-8859-1"),"UTF-8");
	响应：
	   	response.setContentType("text/html;charset=UTF-8");
	   
	POST：
		请求：
	        Tomcat8及以上，需要处理乱码，request.setCharacterEncoding("UTF-8");
			Tomcat7及以下，需要处理乱码，request.setCharacterEncoding("UTF-8");
		响应：
		   	response.setContentType("text/html;charset=UTF-8");
		   	
 * @author Cushier
 *
 */
@WebFilter("/*")
public class EncodeFilter implements Filter {


	public void destroy() {
	}


	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		// 基于Http
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
		// 设置POST请求编码以及响应编码
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		// 获取服务器版本
		String serverInfo = request.getServletContext().getServerInfo();
		// Apache Tomcat/8.0.52
		System.out.println(serverInfo);
		
		// 得到版本号  **左闭右开**
		String info = serverInfo.substring(serverInfo.indexOf("/")+1,serverInfo.indexOf("/")+2);
		
		// 判断Tomcat版本是否是7及以下
		if(info != null && Integer.parseInt(info) <= 7){
			// 判断请求方式
			String method = request.getMethod();
			System.out.println(method);
			if("get".equalsIgnoreCase(method)){
				// 是get请求
				// 	   	Tomcat7及以下，需要处理乱码
			   	//        new String(request.getParameter(name).getBytes("ISO-8859-1"),"UTF-8");
				// 无法得到用户的参数，所以定义一个内部类MyWrapper，继承HttpServletRequestWrapper，重写getParameter方法
				//new String(request.getParameter(name).getBytes("ISO-8859-1"),"UTF-8");
				// myWrapper其实就是重写了getParameter方法的request对象，而重写后的方法里解决了乱码问题，所以用户调用的时候不会乱码
				HttpServletRequest myWrapper = new MyWrapper(request);
				// 放行
				chain.doFilter(myWrapper, response);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

	/**
	 * 定义内部类MyWrapper继承HttpServletRequestWrapper
	 *    目的：重写getParameter方法
	 * @author Cushier
	 *
	 */
	class MyWrapper extends HttpServletRequestWrapper{

		HttpServletRequest request;
		
		public MyWrapper(HttpServletRequest request) {
			super(request);
			this.request = request;
		}

		@Override
		public String getParameter(String name) {
			String value = null;
			try {
				if(request.getParameter(name)!=null){
					value = new String(request.getParameter(name).getBytes("ISO-8859-1"),"UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return value;
		}
		
	}
}
