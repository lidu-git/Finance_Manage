package com.mage.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mage.po.User;
import com.mage.util.DBUtil;
import com.mage.util.StringUtil;
import com.mysql.fabric.Response;

/**
 * 用户模块
 * 		登录功能
 * 		退出功能
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String actionName = request.getParameter("actionName");
		if("login".equals(actionName)){
			// *登录功能
			try {
				login(request,response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}else if("logout".equals(actionName)){
			// *退出功能
			logout(request,response);
			return;
		}
	}

	/**
	 * 退出功能
	 * @param response 
	 * @param request 
	 * @throws IOException 
	 */
	private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 清Cookie
		Cookie cookie = new Cookie("user",null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		// 清Session
		// 获取Session
		HttpSession session = request.getSession();
		// 三种方式
		// 一 覆盖指定键对应的数据
		session.setAttribute("user", null);
		// 二 移除指定键对应的数据
		session.removeAttribute("user");
		// 三 销毁session
		//session.invalidate();
		// 重定向跳转登录页面
		response.sendRedirect("/wc/login.jsp");
	}

	/**
	 * 登录功能
	 * @param response 
	 * @param request 
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取参数
		String uname = request.getParameter("uname");
		String upwd = request.getParameter("upwd");
		
		// 创建User 存前台传过来的用户名和密码
		User u = new User();
		u.setName(uname);
		u.setPwd(upwd);
		
		// 非空判断
		if(StringUtil.isEmpty(uname) || StringUtil.isEmpty(upwd)){
			// 空：
			// 将前台传过来的用户名和密码以及提示信息存到request作用域中
			request.setAttribute("user", u);
			request.setAttribute("msg", "*用户名或密码不能为空！");
			// 请求转发跳转回登录页面
			request.getRequestDispatcher("/login.jsp").forward(request, response);
			return;
		}
		
		// 连接数据库根据用户名查询用户信息 user
		User user = null;
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "select * from user where name = ?";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			sta.setString(1, uname);
			// 执行查询，得到结果集
			res = sta.executeQuery();
			// 分析结果集
			while(res.next()){
				user = new User();
				user.setId(res.getInt(1));
				user.setName(res.getString("name"));
				user.setPwd(res.getString(3));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(res, sta, conn);
		}
		
		// 判断user是否为空
		if(user == null){
			//空：
			//将前台传过来的用户名和密码以及提示信息存到request作用域中
			request.setAttribute("user", u);
			request.setAttribute("msg", "*用户不存在！");
			//请求转发跳转回登录页面
			request.getRequestDispatcher("/login.jsp").forward(request, response);
			return;
		}

		// 判断密码是否正确
		if(upwd.equals(user.getPwd())){
			// 正确：
			// 将 用户信息 user 存session
			request.getSession().setAttribute("user", user);
			// 将 用户名和密码 存Cookie
			Cookie cookie = new Cookie("user",uname + "-" + upwd);
			cookie.setMaxAge(7*24*60*60);
			response.addCookie(cookie);
			// 重定向跳转首页	
			response.sendRedirect("/wc/index.jsp");
			return;
		}
		// 不正确：		
		// 将前台传过来的用户名和密码以及提示信息存到request作用域中
		request.setAttribute("user", u);
		request.setAttribute("msg", "*用户名或密码错误！");
		// 请求转发跳转回登录页面
		request.getRequestDispatcher("/login.jsp").forward(request, response);

	}

}
