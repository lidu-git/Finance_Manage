package com.mage.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.mage.po.PayIn;
import com.mage.po.User;
import com.mage.po.vo.SqlParams;
import com.mage.util.DBUtil;
import com.mage.util.StringUtil;

/**
 * 收入模块
 * 	1、分页及条件查询
 * 	2、添加收入
 * 	3、修改收入
 * 	4、删除收入
 */
@WebServlet("/payIn")
public class PayInServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 获取参数
		String actionName = request.getParameter("actionName");
		// 判断用户行为
		if("addPayIn".equals(actionName)){
			// 添加收入
			addPayIn(request,response);
			return;
		}else if("updatePayIn".equals(actionName)){
			// 修改收入
			updatePayIn(request,response);
			return;
		}else if("deletePayin".equals(actionName)){
			// 删除收入
			deletePayin(request,response);
			return;
		}
		// 分页及条件查询
		queryPayInByPages(request,response);
	}
	
	/**
	 * 删除收入
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void deletePayin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 接收参数   ids=1,2,3
		String idsStr = request.getParameter("ids");
		
		//判断非空
		if(StringUtil.isEmpty(idsStr)){
			response.getWriter().write("0");
			return;
		}
		
		// 将ids通过字符串分割得到id数组
		String[] ids = idsStr.split(",");
		
		int row = 0;
		int count = 0;
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		PayIn payIn = null;
		
		// jdbc操作的sql
		String sql = "select * from payin where id = ?";
		String sqlOld = "update account set money = money - ?, updateTime = now() where id = ?";
		String sqlDel = "delete from payin where id = ?";
		
		// 遍历id数组
		for (String id : ids) {
			// 通过前台传过来的收入主键id查询当前收入记录  PayIn
			try {
				// 获取连接
				conn = DBUtil.getConnection();
				// 预编译
				sta = conn.prepareStatement(sql);
				// 设置参数
				sta.setInt(1, Integer.parseInt(id));
				// 执行查询，得到结果集
				res = sta.executeQuery();
				// 分析结果集
				while(res.next()){
					payIn = new PayIn();
					payIn.setId(Integer.parseInt(id));
					payIn.setMoney(res.getDouble("money"));
					payIn.setAccountId(res.getInt("accountId"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(res, sta, conn);
			}
			if(payIn == null){
				break;
			}
			
			// 创建参数集合
			List<Object> paramsOld = new ArrayList<>();
			paramsOld.add(payIn.getMoney());
			paramsOld.add(payIn.getAccountId());
			SqlParams sqlParamsOld = new SqlParams(sqlOld,paramsOld);
			
			List<Object> paramsDel = new ArrayList<>();
			paramsDel.add(Integer.parseInt(id));
			SqlParams sqlparamsDel = new SqlParams(sqlDel,paramsDel);
			
			List<SqlParams> list = new ArrayList<>();
			list.add(sqlParamsOld);
			list.add(sqlparamsDel);
			
			try {
				// 获取连接
				conn = DBUtil.getConnection();
				// 设置事务不自动提交
				conn.setAutoCommit(false);
				// 循环遍历进行jdbc操作
				//修改原来账户里的金额(money = money - 原来收入记录里的金额（PayIn.money）)
				//通过主键id删除收入记录
				for (SqlParams sqlParams : list) {
					String sql1 = sqlParams.getSql();
					// 预编译
					sta = conn.prepareStatement(sql1);
					// 循环设置参数
					List<Object> params = sqlParams.getParams();
					for (int i = 0; i < params.size(); i++) {
						sta.setObject(i+1, params.get(i));
					}
					// 执行更新，得到影响行
					row = sta.executeUpdate();
					// 判断当前操作是否成功
					if(row < 1){
						// 失败了
						// 回滚
						conn.rollback();
						break;
					}else{
						count++;
					}
				}
			} catch (Exception e) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} finally {
				try {
					conn.commit();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 关闭连接
				DBUtil.close(null, sta, conn);
			}

		}
		// 前台判断是否成功
		response.getWriter().write(""+count/2);
	}

	/**
	 * 修改收入
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void updatePayIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 接收前台表单中的数据 (前台需要把收入主键id传过来)
		String inName = request.getParameter("inName");
		String inType = request.getParameter("inType");
		String accountId = request.getParameter("accountId");
		String money = request.getParameter("money");
		String remark = request.getParameter("remark");
		String pid = request.getParameter("pid");
		
		// 非空判断
		if(StringUtil.isEmpty(inName)||StringUtil.isEmpty(inType)||StringUtil.isEmpty(accountId)||StringUtil.isEmpty(money)||StringUtil.isEmpty(pid)){
			response.getWriter().write("0");
			return;
		}
		
		//	通过前台传过来的收入主键id查询当前收入记录  PayIn(之前把钱给了谁)
		PayIn payIn = null;
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "select * from payin where id = ?";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			sta.setInt(1, Integer.parseInt(pid));
			// 执行查询
			res = sta.executeQuery();
			// 分析结果集
			while(res.next()){
				payIn = new PayIn();
				payIn.setId(Integer.parseInt(pid));
				payIn.setAccountId(res.getInt("accountId"));
				payIn.setMoney(res.getDouble("money"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(res, sta, conn);
		}
		
		// 修改原来账户里的金额（money = money - 原来收入记录里的金额（PayIn.money）
		String sqlOld = "update account set money = money - ? where id = ?";
		List<Object> paramsOld = new ArrayList<>();
		paramsOld.add(payIn.getMoney());
		paramsOld.add(payIn.getAccountId());
		SqlParams sqlParamsOld = new SqlParams();
		sqlParamsOld.setSql(sqlOld);
		sqlParamsOld.setParams(paramsOld);
		
		// 修改正确账户里的金额（money = money + 前台传过来的收入金额）
		String sqlNew = "update account set money = money + ? where id = ?";
		List<Object> paramsNew = new ArrayList<>();
		paramsNew.add(money);
		paramsNew.add(accountId);
		SqlParams sqlParamsNew = new SqlParams();
		sqlParamsNew.setSql(sqlNew);
		sqlParamsNew.setParams(paramsNew);
		
		// 更新收入记录	
		String sqlUpdate = "update payin set inName = ?,money = ?,inType = ?,accountId = ?,updateTime = now(),remark = ? where id = ?";
		List<Object> paramsUpdate = new ArrayList<>();
		paramsUpdate.add(inName);
		paramsUpdate.add(money);
		paramsUpdate.add(inType);
		paramsUpdate.add(accountId);
		paramsUpdate.add(remark);
		paramsUpdate.add(pid);
		SqlParams sqlParamsUpdate = new SqlParams();
		sqlParamsUpdate.setSql(sqlUpdate);
		sqlParamsUpdate.setParams(paramsUpdate);
		
		List<SqlParams> list = new ArrayList<>();
		list.add(sqlParamsOld);
		list.add(sqlParamsNew);
		list.add(sqlParamsUpdate);
		int row = 0;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 设置不自动提交事务
			conn.setAutoCommit(false);
			for (SqlParams sqlParams : list) {
				// 预编译
				sta = conn.prepareStatement(sqlParams.getSql());
				// 循环设置参数
				for(int i = 0; i < sqlParams.getParams().size(); i++){
					sta.setObject(i+1, sqlParams.getParams().get(i));
				}
				// 执行更新
				row = sta.executeUpdate();
				// 判断当前操作是否成功
				if(row < 1){
					// 失败，回滚
					conn.rollback();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// 关闭连接
			DBUtil.close(res, sta, conn);
		}
		// 判断是否修改成功
		if(row > 0){
			// 成功
			response.getWriter().write("1");
		}else{
			// 失败
			response.getWriter().write("0");
		}
		
	}

	/**
	 * 添加收入
	 * @param request
	 * @param response
	 * @throws IOException 
	 */

	private void addPayIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 接受前台表单中的参数
		String inName = request.getParameter("inName");
		String inType = request.getParameter("inType");
		String accountId = request.getParameter("accountId");
		String money = request.getParameter("money");
		String remark = request.getParameter("remark");
		// 非空判断
		if(StringUtil.isEmpty(inName)||StringUtil.isEmpty(inType)||StringUtil.isEmpty(accountId)||StringUtil.isEmpty(money)){
			// 空：响应0
			response.getWriter().write("0");
			return;
		}
		int row = 0;
		Connection conn = null;
		PreparedStatement sta = null;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "insert into payin (inName,inType,accountId,money,remark,createTime,updateTime) values (?,?,?,?,?,now(),now())";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			sta.setString(1, inName);
			sta.setString(2, inType);
			sta.setInt(3, Integer.parseInt(accountId));
			sta.setDouble(4, Double.parseDouble(money));
			sta.setString(5, remark);
			// 执行查询，得到row
			row=sta.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(null, sta, conn);
		}
		if(row < 1){
			response.getWriter().write("0");
			return;
		}
		// 通过accountId找到该账户并给该账户的金额增加收入金额
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "update account set money = money + ?,updateTime = now() where id = ?";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			sta.setDouble(1, Double.parseDouble(money));
			sta.setInt(2, Integer.parseInt(accountId));
			// 执行更新
			row = sta.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(null, sta, conn);
		}
		if(row > 0){
			// 成功
			response.getWriter().write("1");
		}else{
			// 失败
			response.getWriter().write("0");
		}
	}

	/**
	 * 分页及条件查询
	 * 	          接受条件查询参数
			查询总数的sqlCount
			查询每页显示的数据的sqlPage
			新建参数集合List<Object> params
			非空判断条件参数
				不为空，则拼接sql并且把不为空的参数添加到参数集合里
				
			查询总数 count
				获取连接
				预编译(sqlCount)
				遍历参数集合设置参数
				执行查询，得到结果集
				分析结果集，得到count
				关闭连接
			
			查询每页显示的数据
				获取连接
				sqlPage += limit ?,?
				把查询开始的下标index以及每页显示的数量pageSize添加到参数集合里
				预编译(sqlPage)
				遍历参数集合设置参数
				执行查询，得到结果集
				分析结果集，得到List<PayIn>
				关闭连接
			
			把上面的总数和每页显示的数据放到map中转换为json
			把json响应给前台
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void queryPayInByPages(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 接受条件查询参数
		String inName = request.getParameter("inName");
		String inType = request.getParameter("inType");
		String createTime = request.getParameter("createTime");
		
		// 查询总数的sqlCount
		String sqlCount = "select count(1) from payin p inner join account a on p.accountId = a.id where uid = ?";
		// 查询每页显示的数据的sqlPage
		String sqlPage = "select"
				+ " p.id,inName,inType,p.money,accountName,p.remark,p.createTime,p.updateTime,accountId "
				+ "from payin p inner join account a "
				+ "on p.accountId = a.id "
				+ "where uid = ?";
		// 新建参数集合List<Object> params
		List<Object> params = new ArrayList<>();
		// 通过session获取用户uid
		User user = (User)request.getSession().getAttribute("user");
		Integer id = user.getId();
		// 将uid存到params中
		params.add(id);
		
		// 非空判断条件参数
		// 不为空，则拼接sql并且把不为空的参数添加到参数集合里
		if(StringUtil.isNotEmpty(inName)){
			params.add("%"+inName+"%");
			sqlCount+=" and inName like ?";
			sqlPage+=" and inName like ?";
		}
		if(StringUtil.isNotEmpty(inType)){
			params.add(inType);
			sqlCount+=" and inType = ?";
			sqlPage+=" and inType = ?";
		}
		if(StringUtil.isNotEmpty(createTime)){
			params.add(createTime);
			sqlCount+=" and createTime < ?";
			sqlPage+=" and createTime < ?";
		}

		
		// 查询总数 count
		long count = 0;
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 预编译(sqlCount)
			sta = conn.prepareStatement(sqlCount);
			// 遍历参数集合设置参数
			for(int i = 0;i < params.size(); i++){
				sta.setObject(i+1, params.get(i));
			}
			// 执行查询，得到结果集
			res = sta.executeQuery();
			// 分析结果集，得到count
			while(res.next()){
				count = res.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(res, sta, conn);
		}
		
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
		List<PayIn> list = new ArrayList<PayIn>();
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
				PayIn payIn = new PayIn();
				payIn.setId(res.getInt("id"));
				payIn.setAccountId(res.getInt("accountId"));
				payIn.setInName(res.getString("inName"));
				payIn.setAccountName(res.getString("accountName"));
				payIn.setMoney(res.getDouble("money"));
				payIn.setCreateTime(res.getTimestamp("createTime"));
				payIn.setUpdateTime(res.getTimestamp("updateTime"));
				payIn.setRemark(res.getString("remark"));
				payIn.setInType(res.getString("inType"));
				list.add(payIn);
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

}
