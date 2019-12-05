package com.mage.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mage.po.Account;
import com.mage.po.User;
import com.mage.util.DBUtil;
import com.mage.util.StringUtil;

/**
 * 账户模块
 */
@WebServlet("/account")
public class AccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 接受参数
		String actionName = request.getParameter("actionName");
		// 判断用户行为
		if("queryAccountList".equals(actionName)){
			// 查询账户列表
			//queryAccountListByUid(request,response);
			// 分页查询账户列表
			// queryAccountListByPage(request,response);
			// 分页条件查询账户列表
			queryAccountListByPages(request,response);
			return;
		}else if("addAccount".equals(actionName)){
			// 添加账户
			addAccount(request,response);
			return;
		}else if("updateAccount".equals(actionName)){
			// 修改账户
			updateAccount(request,response);
			return;
		}else if("deleteAccount".equals(actionName)){
			// 删除账户
			deleteAccount(request,response);
		}else if("queryAccountListByUid".equals(actionName)){
			// 查询账户列表
			queryAccountListByUid(request,response);
		}
		
	}

	
	/**
	 * 分页条件查询账户列表
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void queryAccountListByPages(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// 接受条件查询参数
		String accountName = request.getParameter("accountName");
		String accountType = request.getParameter("accountType");
		String createTime = request.getParameter("createTime");
		
		String sqlCount = "select count(1) from account where uid = ?";
		String sqlPage = "select * from account where uid = ?";
		// 参数集合
		List<Object> params = new ArrayList<>();
		// 通过Session获取用户uid
		User user = (User)request.getSession().getAttribute("user");
		Integer id = user.getId();
		params.add(id);
		// 非空判断
		if(StringUtil.isNotEmpty(accountName)){
			params.add("%"+accountName+"%");
			// 拼接查询总数的sql
			sqlCount += " and accountName like ?";
			// sqlCount += " and accountName like CONCAT('%',?,'%')";
			// 拼接查询当前页数据的sql
			sqlPage += " and accountName like ?";
		}
		if(StringUtil.isNotEmpty(accountType)){
			params.add(accountType);
			// 拼接查询总数的sql
			sqlCount += " and accountType = ?";
			// 拼接查询当前页数据的sql
			sqlPage += " and accountType = ?";
		}
		if(StringUtil.isNotEmpty(createTime)){
			params.add(createTime);
			// 拼接查询总数的sql
			sqlCount += " and createTime < ?";
			// 拼接查询当前页数据的sql
			sqlPage += " and createTime < ?";
		}
		
		/******************得到总数*********************/
		// 通过用户uid查询数据库中当前用户账户总数
		long count = 0;
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			// 创建连接
			conn = DBUtil.getConnection();
			// 预编译
			sta = conn.prepareStatement(sqlCount);
			// 遍历参数集合设置参数
			for(int i = 0;i < params.size();i++){
				sta.setObject(i+1, params.get(i));
			}
			// 执行查询
			res = sta.executeQuery();
			// 分析结果集
			while(res.next()){
				count = res.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(res, sta, conn);
		}
		
		
		/******************得到当前页要显示的数据*********************/

		// 接受前台数据表格传过来的参数 page和rows
		String page = request.getParameter("page");
		String rows = request.getParameter("rows");
		// 设置默认的当前页 currentPage 和每页显示的数量 pageSize
		Integer currentPage = 1;
		Integer pageSize = 5;
		// 非空判断
		// 不为空：currentPage = pags  pageSize = rows
		if(StringUtil.isNotEmpty(page)){
			currentPage = Integer.parseInt(page);
		}
		if(StringUtil.isNotEmpty(rows)){
			pageSize = Integer.parseInt(rows);
		}
		
		//得到数据库开始查询的下标  (currentPage - 1)*pageSize
		Integer index = (currentPage - 1)*pageSize;
		// 通过用户uid以及下标和每页显示的数量查询当前页要显示的数据
		List<Account> list = new ArrayList<Account>();
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			sqlPage += " limit ?,?";
			// 添加参数
			params.add(index);
			params.add(pageSize);
			// 预编译
			sta = conn.prepareStatement(sqlPage);
			// 设置参数
			for(int i = 1;i <= params.size(); i++){
				sta.setObject(i, params.get(i-1));
			}
			// 执行查询，得到结果集
			res = sta.executeQuery();
			// 分析结果集
			while(res.next()){
				Account account = new Account();
				account.setId(res.getInt("id"));
				account.setAccountName(res.getString("accountName"));
				account.setMoney(res.getDouble("money"));
				account.setUid(id);
				account.setCreateTime(res.getTimestamp("createTime"));
				account.setUpdateTime(res.getTimestamp("updateTime"));
				account.setRemark(res.getString("remark"));
				account.setAccountType(res.getString("accountType"));
				list.add(account);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(res, sta, conn);
		}
		
		// 把上面的总数和当前页要显示的数据放到Map里 分别对应键  total 和  rows 的值
		Map<String,Object> map = new HashMap<>();
		map.put("total", count);
		map.put("rows", list);
		// 将Map转换为json并响应给前台
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		String json = gson.toJson(map);
		response.getWriter().write(json);
		
	}


	/**
	 * 分页查询账户列表
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void queryAccountListByPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		/******************得到总数*********************/
		// 通过Session获取用户uid
		User user = (User)request.getSession().getAttribute("user");
		Integer id = user.getId();
		// 通过用户uid查询数据库中当前用户账户总数
		long count = 0;
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			// 创建连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "select count(1) from account where uid = ?";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			sta.setInt(1, id);
			// 执行查询
			res = sta.executeQuery();
			// 分析结果集
			while(res.next()){
				count = res.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(res, sta, conn);
		}
		
		
		/******************得到当前页要显示的数据*********************/

		// 接受前台数据表格传过来的参数 page和rows
		String page = request.getParameter("page");
		String rows = request.getParameter("rows");
		// 设置默认的当前页 currentPage 和每页显示的数量 pageSize
		Integer currentPage = 1;
		Integer pageSize = 5;
		// 非空判断
		// 不为空：currentPage = pags  pageSize = rows
		if(StringUtil.isNotEmpty(page)){
			currentPage = Integer.parseInt(page);
		}
		if(StringUtil.isNotEmpty(rows)){
			pageSize = Integer.parseInt(rows);
		}
		
		//得到数据库开始查询的下标  (currentPage - 1)*pageSize
		Integer index = (currentPage - 1)*pageSize;
		// 通过用户uid以及下标和每页显示的数量查询当前页要显示的数据
		List<Account> list = new ArrayList<Account>();
		Connection conn2 = null;
		PreparedStatement sta2 = null;
		ResultSet res2 = null;
		try {
			// 获取连接
			conn2 = DBUtil.getConnection();
			// 编写sql
			String sql = "select * from account where uid = ? limit ?,?";
			// 预编译
			sta2 = conn2.prepareStatement(sql);
			// 设置参数
			sta2.setInt(1, id);
			sta2.setInt(2, index);
			sta2.setInt(3, pageSize);
			// 执行查询，得到结果集
			res2 = sta2.executeQuery();
			// 分析结果集
			while(res2.next()){
				Account account = new Account();
				account.setId(res2.getInt("id"));
				account.setAccountName(res2.getString("accountName"));
				account.setMoney(res2.getDouble("money"));
				account.setUid(id);
				account.setCreateTime(res2.getTimestamp("createTime"));
				account.setUpdateTime(res2.getTimestamp("updateTime"));
				account.setRemark(res2.getString("remark"));
				account.setAccountType(res2.getString("accountType"));
				list.add(account);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(res2, sta2, conn2);
		}
		
		// 把上面的总数和当前页要显示的数据放到Map里 分别对应键  total 和  rows 的值
		Map<String,Object> map = new HashMap<>();
		map.put("total", count);
		map.put("rows", list);
		// 将Map转换为json并响应给前台
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		String json = gson.toJson(map);
		response.getWriter().write(json);
	}

	/**
	 * 删除账户
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void deleteAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取前台提交过来的ids
		String ids =request.getParameter("ids");
		// 非空判断
		if(StringUtil.isEmpty(ids)){
			// 空：响应0给前台ajax的回调函数
			response.getWriter().write("0");
			return;
		}
		// 通过账户id删除数据库对应的数据，返回受影响的行数row
		int row = 0;
		Connection conn = null;
		PreparedStatement sta = null;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "delete from account where id in ("+ids+")";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			//sta.setString(1, ids);
			// 执行更新
			row = sta.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(null, sta, conn);
		}
		//判断是否删除成功
		if(row > 0){
			// row>0 成功：响应1给前台ajax的回调函数
			response.getWriter().write("1");
		}else{
			// 否则      失败：响应0给前台ajax的回调函数
			response.getWriter().write("0");
		}
		
	}


	/**
	 * 修改账户
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void updateAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 接受参数
		String accountName = request.getParameter("accountName");
		String accountType = request.getParameter("accountType");
		String money = request.getParameter("money");
		String remark = request.getParameter("remark");
		String accountId = request.getParameter("accountId");
		// 非空判断
		if(StringUtil.isEmpty(accountName)||StringUtil.isEmpty(accountType)||StringUtil.isEmpty(money)||StringUtil.isEmpty(accountId)){
			// 空：响应0给前台ajax的回调函数
			response.getWriter().write("0");
			return;
		}
		// 通过账户id更新数据库对应的数据，返回受影响的行数row
		int row = 0;
		Connection conn = null;
		PreparedStatement sta = null;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "update account set accountName = ?,money = ?,remark = ?, accountType = ?,updateTime = now() where id = ?";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			sta.setString(1, accountName);
			sta.setDouble(2, Double.parseDouble(money));
			sta.setString(3, remark);
			sta.setString(4, accountType);
			sta.setInt(5, Integer.parseInt(accountId));
			// 执行更新，得到影响行
			row = sta.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(null, sta, conn);
		}
		//判断是否更新成功
		if(row > 0){
			// row>0 成功：响应1给前台ajax的回调函数
			response.getWriter().write("1");
		}else{
			// 否则      失败：响应0给前台ajax的回调函数
			response.getWriter().write("0");
		}
			
			
	}


	/**
	 * 添加账户
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void addAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取参数
		String accountName = request.getParameter("accountName");
		String accountType = request.getParameter("accountType");
		String money = request.getParameter("money");
		String remark = request.getParameter("remark");
		// 非空判断
		if(StringUtil.isEmpty(accountName)||StringUtil.isEmpty(accountType)||StringUtil.isEmpty(money)){
			// 	空  响应0
			response.getWriter().write("0");
			return;
		}

		//	通过session获取用户uid
		User user = (User) request.getSession().getAttribute("user");
		Integer id = user.getId();
		//		jdbc 添加操作 返回受影响行数 row
		int row = 0;
		Connection conn = null;
		PreparedStatement sta = null;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "insert into account (accountName,money,uid,remark,accountType,createTime,updateTime) values (?,?,?,?,?,now(),now())";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			sta.setString(1, accountName);
			sta.setDouble(2, Double.parseDouble(money));
			sta.setInt(3, id);
			sta.setString(4, remark);
			sta.setString(5, accountType);
			// 执行更新
			row = sta.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(null, sta, conn);
		}
		if(row > 0){
			// row>0
			// 成功，响应1
			response.getWriter().write("1");
		}else{
			// 否则
			// 失败，响应0
			response.getWriter().write("0");
		}

		
	}


	/**
	 * 通过用户id查询账户列表
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void queryAccountListByUid(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 通过session获取用户uid
		User user = (User)request.getSession().getAttribute("user");
		Integer id = user.getId();
		// 通过用户uid从account表中把所有账户查出来并放到集合list中
		List<Account> list = new ArrayList<Account>();
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "select * from account where uid = ?";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			sta.setInt(1, id);
			// 执行查询，得到结果集
			res = sta.executeQuery();
			// 分析结果集
			while(res.next()){
				Account account = new Account();
				account.setId(res.getInt("id"));
				account.setAccountName(res.getString("accountName"));
				account.setMoney(res.getDouble("money"));
				account.setUid(id);
				account.setCreateTime(res.getTimestamp("createTime"));
				account.setUpdateTime(res.getTimestamp("updateTime"));
				account.setRemark(res.getString("remark"));
				account.setAccountType(res.getString("accountType"));
				list.add(account);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(res, sta, conn);
		}
		// 把list转换成json  设置gson转换时间时候使用的格式
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		String json = gson.toJson(list);
		// 响应给前台
		response.getWriter().write(json);
	}


}
