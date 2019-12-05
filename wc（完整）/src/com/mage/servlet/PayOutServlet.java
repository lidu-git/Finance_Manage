package com.mage.servlet;

import java.io.IOException;
import java.sql.CallableStatement;
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
import com.mage.po.OutType;
import com.mage.po.PayIn;
import com.mage.po.PayOut;
import com.mage.po.User;
import com.mage.po.vo.Result;
import com.mage.po.vo.SqlParams;
import com.mage.util.DBUtil;
import com.mage.util.StringUtil;

/**
 * 支出模块
 * 	1、分页及条件查询
 * 	2、添加支出
 * 	3、修改支出
 * 	4、删除支出
 */
@WebServlet("/payOut")
public class PayOutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Result result = new Result();
	

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 获取参数
		String actionName = request.getParameter("actionName");
		// 判断用户行为
		if("queryPayOutType".equals(actionName)){
			// 查询支出类型
			queryPayOutType(request,response);
			return;
		}else if("addPayOut".equals(actionName)){
			// 添加支出
			addPayOut(request,response);
			return;
		}else if("updatePayOut".equals(actionName)){
			// 添加支出
			updatePayOut(request,response);
			return;
		}else if("deletePayOut".equals(actionName)){
			deletePayOut(request,response);
			return;
		}
		// 分页及条件查询
		queryPayOutByPages(request,response);
	}
	
	/**
	 * 删除支出
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void deletePayOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取参数 ids 1,2,3
		String idsStr = request.getParameter("ids");
		// 非空判断
		if(StringUtil.isEmpty(idsStr)){
			response.getWriter().write("0");
			return;
		}
		// 将ids截取转换为数组
		String[] ids = idsStr.split(",");
		
		int row = 0;
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		PayOut payOut = null;
		
		// jdbc操作的sql
		String sql = "select * from payout where id = ?";
		String sqlOld = "update account set money = money + ?, updateTime = now() where id = ?";
		String sqlDel = "delete from payout where id = ?";
		
		// 遍历id数组
		for (String id : ids) {
			// 通过前台传过来的支出主键id查询当前支出记录  PayOut
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
					payOut = new PayOut();
					payOut.setId(Integer.parseInt(id));
					payOut.setMoney(res.getDouble("money"));
					payOut.setAccountId(res.getInt("accountId"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 关闭连接
				DBUtil.close(res, sta, conn);
			}
			if(payOut == null){
				// 跳出当前循环，还会继续下一个记录的删除
				break;
			}
			
			// 创建参数集合
			List<Object> paramsOld = new ArrayList<>();
			paramsOld.add(payOut.getMoney());
			paramsOld.add(payOut.getAccountId());
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
						// 跳出当前删除，还会继续删除下一条
						break;
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
		if(row > 0){
			response.getWriter().write("1");
		}else{
			response.getWriter().write("0");
		}
	}

	/**
	 * 修改支出
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void updatePayOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取参数
		String outName = request.getParameter("outName");
		String outTypeId = request.getParameter("outTypeId");
		String accountId = request.getParameter("accountId");
		String money = request.getParameter("money");
		String remark = request.getParameter("remark");
		String payOutId = request.getParameter("payOutId");
		// 非空判断
		if(StringUtil.isEmpty(outName)
				||StringUtil.isEmpty(outTypeId)
				||StringUtil.isEmpty(accountId)
				||StringUtil.isEmpty(money)
				||StringUtil.isEmpty(payOutId)){
			// 提示失败
			response.getWriter().write("0");
			return;
		}
		// 通过payOutId查询原来支出记录里的账户id以及支出金额
		PayOut payOut = null;
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "select * from payout where id = ?";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			sta.setInt(1, Integer.parseInt(payOutId));
			// 执行查询
			res = sta.executeQuery();
			// 分析结果集
			while(res.next()){
				payOut = new PayOut();
				payOut.setId(Integer.parseInt(payOutId));
				payOut.setAccountId(res.getInt("accountId"));
				payOut.setMoney(res.getDouble("money"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(res, sta, conn);
		}
		
		// 判断要支出的账户里金额是否足够（注意：同一账户情况不同） select 操作
/*		select count(1) from account where id = 8 and money >= 2701
		select count(1) from account where id = 8 and money +100 >= 2800*/
		long count = 0;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			// 判断是否是同一账户
			String sql = "";
			if(Integer.parseInt(accountId) == payOut.getAccountId()){
				sql = "select count(1) from account where id = ? and money +? >= ?";
				// 预编译
				sta = conn.prepareStatement(sql);
				// 设置参数
				sta.setInt(1, Integer.parseInt(payOutId));
				sta.setDouble(2, payOut.getMoney());
				sta.setDouble(3, Double.parseDouble(money));
			}else{
				sql = "select count(1) from account where id = ? and money >= ?";
				// 预编译
				sta = conn.prepareStatement(sql);
				// 设置参数
				sta.setInt(1, Integer.parseInt(payOutId));
				sta.setDouble(2, Double.parseDouble(money));
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
		if(count < 1){
			//否：响应 0
			response.getWriter().write("0");
			return;
		}
		
		// 修改原来账户里的金额（money = money + 原来支出记录里的金额（PayOut.money）） 返回 row
		String sqlOld = "update account set money = money + ?,updateTime = now() where id = ?";
		List<Object> paramsOld = new ArrayList<>();
		paramsOld.add(payOut.getMoney());
		paramsOld.add(payOut.getAccountId());
		SqlParams sqlParamsOld = new SqlParams(sqlOld, paramsOld);
		
		// 修改正确账户里的金额（money = money - 前台传过来的收入金额）
		String sqlNew = "update account set money = money - ? where id = ?";
		List<Object> paramsNew = new ArrayList<>();
		paramsNew.add(money);
		paramsNew.add(accountId);
		SqlParams sqlParamsNew = new SqlParams(sqlNew,paramsNew);
		
		// 更新支出记录	
		String sqlUpdate = "update payOut set outName = ?,outTypeId = ?,accountId = ?,money = ?,updateTime = now(),remark = ? where id = ?";
		List<Object> paramsUpdate = new ArrayList<>();
		paramsUpdate.add(outName);
		paramsUpdate.add(outTypeId);
		paramsUpdate.add(accountId);
		paramsUpdate.add(money);
		paramsUpdate.add(remark);
		paramsUpdate.add(payOutId);
		SqlParams sqlParamsUpdate = new SqlParams(sqlUpdate,paramsUpdate);
		
		List<SqlParams> list = new ArrayList<>();
		list.add(sqlParamsOld);
		list.add(sqlParamsNew);
		list.add(sqlParamsUpdate);
		
		int row = 0;
		
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 设置事务不自动提交
			conn.setAutoCommit(false);
			// 下面的操作放到循环里进行，因为都是update操作
			for (SqlParams sqlParams : list) {
				// 得到sql
				String sql = sqlParams.getSql();
				// 预编译
				sta = conn.prepareStatement(sql);
				// 得到当前sql操作的参数
				List<Object> params = sqlParams.getParams();
				// 循环参数集合设置参数
				for(int i = 0; i < params.size(); i++){
					sta.setObject(i+1, params.get(i));
				}
				// 执行更新
				row = sta.executeUpdate();
				// 判断是否更新成功
				if(row < 1){
					// 失败回滚
					conn.rollback();
					break;
				}
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			// 手动提交事务
			try {
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 关闭连接
			DBUtil.close(null, sta, conn);
		}
		if(row > 0){
			// 成功
			response.getWriter().write("1");
		}else{
			response.getWriter().write("0");
		}
		
	}

	/**
	 * 添加支出
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void addPayOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 接收参数
		String outName = request.getParameter("outName");
		String outTypeId = request.getParameter("outTypeId");
		String accountId = request.getParameter("accountId");
		String money = request.getParameter("money");
		String remark = request.getParameter("remark");
		
		// 非空判断
		if(StringUtil.isEmpty(outName)||StringUtil.isEmpty(outName)||StringUtil.isEmpty(outName)||StringUtil.isEmpty(outName)){
			result.setCode(0);
			result.setMsg("添加失败");
			Gson gson = new Gson();
			response.getWriter().write(gson.toJson(result));
			return;
		}
		
		// 判断要支出的账户金额是否足够
		long count = 0;
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "select count(1) from account where id = ? and money >= ?";
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			sta.setInt(1, Integer.parseInt(accountId));
			sta.setDouble(2, Double.parseDouble(money));
			// 执行查询，得到结果集
			res = sta.executeQuery();
			// 分析结果集
			while(res.next()){
				count = res.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBUtil.close(res, sta, conn);
		}
		if(count < 1){
			result.setCode(0);
			result.setMsg("账户余额不足");
			Gson gson = new Gson();
			response.getWriter().write(gson.toJson(result));
			return;
		}
		
		int row = 0;
		try {
			// 修改账户金额
			// 获取连接
			conn = DBUtil.getConnection();
			// 设置事务不自动提交
			conn.setAutoCommit(false);
			// 编写sql
			String sqlUpdate = "update account set money = money - ?,updateTime = now() where id = ?";
			// 预编译
			sta = conn.prepareStatement(sqlUpdate);
			// 设置参数
			sta.setDouble(1, Double.parseDouble(money));
			sta.setInt(2, Integer.parseInt(accountId));
			// 执行更新
			row = sta.executeUpdate();
			// 判断是否修改成功
			if(row < 1){
				conn.rollback();
				result.setCode(0);
				result.setMsg("添加失败");
				Gson gson = new Gson();
				response.getWriter().write(gson.toJson(result));
				return;
			}
			
			// 添加支出记录
			String sqlAdd = "insert into payOut (outName,outTypeId,money,accountId,createTime,updateTime,remark) values (?,?,?,?,now(),now(),?)";
			// 预编译
			sta = conn.prepareStatement(sqlAdd);
			// 设置参数
			sta.setString(1, outName);
			sta.setInt(2, Integer.parseInt(outTypeId));
			sta.setDouble(3, Double.parseDouble(money));
			sta.setInt(4, Integer.parseInt(accountId));
			sta.setString(5, remark);
			// 执行更新
			row = sta.executeUpdate();
			// 判断是否修改成功
			if(row < 1){
				conn.rollback();
				result.setCode(0);
				result.setMsg("添加失败");
				Gson gson = new Gson();
				response.getWriter().write(gson.toJson(result));
				return;
			}
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			// 提交事务
			try {
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DBUtil.close(null, sta, conn);
		}
		if(row > 0){
			// 成功
			result.setCode(1);
			result.setMsg("添加成功");
			Gson gson = new Gson();
			response.getWriter().write(gson.toJson(result));
		}

	}

	/**
	 * 分页及条件查询
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void queryPayOutByPages(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 接收条件参数
		String outName =  request.getParameter("outName");
		String outTypePid =  request.getParameter("outTypePid");
		String createTime =  request.getParameter("createTime");
		
		// 查询总数的sqlCount
		String sqlCount = "select count(1) "
				+ "from payout p "
				+ "INNER JOIN account a on p.accountId = a.id "
				+ "INNER JOIN outtype o on p.outTypeId = o.id "
				+ "INNER JOIN outtype ot on o.pid = ot.id "
				+ "where a.uid = ?";
		// 查询每页显示的数据的sqlPage
		String sqlPage = "select ot.id as parentId,p.id,p.outName,p.outTypeId,p.money,p.accountId,p.createTime,p.updateTime,p.remark,a.accountName,CONCAT(ot.typeName,'-',o.typeName) as typeName "
				+ "from payout p "
				+ "INNER JOIN account a on p.accountId = a.id "
				+ "INNER JOIN outtype o on p.outTypeId = o.id "
				+ "INNER JOIN outtype ot on o.pid = ot.id "
				+ "where a.uid = ?";
		// 新建参数集合List<Object> params
		List<Object> params = new ArrayList<>();
		// 通过session获取用户uid
		User user = (User)request.getSession().getAttribute("user");
		Integer id = user.getId();
		// 将uid存到params中
		params.add(id);
		
		// 非空判断条件参数
		// 不为空，则拼接sql并且把不为空的参数添加到参数集合里
		if(StringUtil.isNotEmpty(outName)){
			params.add("%"+outName+"%");
			sqlCount+=" and outName like ?";
			sqlPage+=" and outName like ?";
		}
		if(StringUtil.isNotEmpty(outTypePid)){
			params.add(outTypePid);
			sqlCount+=" and ot.id = ?";
			sqlPage+=" and ot.id = ?";
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
		List<PayOut> list = new ArrayList<PayOut>();
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
				PayOut payOut = new PayOut();
				payOut.setId(res.getInt("id"));
				payOut.setOutName(res.getString("outName"));
				payOut.setOutTypeId(res.getInt("outTypeId"));
				payOut.setMoney(res.getDouble("money"));
				payOut.setAccountId(res.getInt("accountId"));
				payOut.setCreateTime(res.getTimestamp("createTime"));
				payOut.setUpdateTime(res.getTimestamp("updateTime"));
				payOut.setRemark(res.getString("remark"));
				payOut.setAccountName(res.getString("accountName"));
				payOut.setTypeName(res.getString("typeName"));
				payOut.setParentId(res.getInt("parentId"));
				list.add(payOut);
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
	 * 查询父支出类型
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void queryPayOutType(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pid = request.getParameter("pid");
		List<OutType> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			// 获取连接
			conn = DBUtil.getConnection();
			// 编写sql
			String sql = "";
			if(StringUtil.isEmpty(pid)){
				sql = "select * from outtype where pid = 0";
			}else{
				sql = "select * from outtype where pid = ?";
			}
			// 预编译
			sta = conn.prepareStatement(sql);
			// 设置参数
			if(StringUtil.isNotEmpty(pid)){
				sta.setInt(1, Integer.parseInt(pid));
			}
			// 执行查询
			res = sta.executeQuery();
			// 分析结果集
			while(res.next()){
				OutType outType = new OutType();
				outType.setId(res.getInt(1));
				outType.setTypeName(res.getString(2));
				outType.setPid(res.getInt(3));
				list.add(outType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			DBUtil.close(res, sta, conn);
		}
		String json = new Gson().toJson(list);
		response.getWriter().write(json);
	}

}