<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>登录</title>

<!-- 静态包含引入必要的CSS以及JS文件 -->
<%@ include file="/commons/commons.jsp" %>

</head>
<body
	style="background-image: url(/wc/statics/img/bgimg.jpg); background-repeat: no-repeat;">
	<div
		style="margin-left: 50%; margin-top: 150px; background-color: #fafafa; margin-right: 20%">
		<div id="p" class="easyui-panel"
			style="width: 500px; height: 250px; padding: 10px; background: #fafafa;"
			data-options="iconCls:'icon-save',closable:true,    
                collapsible:true,minimizable:true,maximizable:true">
			<h2 style="margin-left: 40%;">登&nbsp;&nbsp;录</h2>
			<div style="margin: 10px 0;"></div>
			<div style="padding: 20px 0 10px 60px">
				<form id="loginForm" action="/wc/user" method="post">
					<input type="hidden" name="actionName" value="login" />
					<table>
						<tr>
							<td>用户名称：</td>
							<td><input id="uname" name="uname" type="text" value="admin" <%-- value="${user.name }" --%> /></td>
						</tr>
						<tr>
							<td>用户密码：</td>
							<td><input id="upwd" name="upwd" type="password" value="admin" <%-- value="${user.pwd }" --%> /></td>
						</tr>
						<tr>
							<td></td>
							<td><span id="msg" style="color: red">${msg }</span></td>
						</tr>
	
						<tr>
							<td></td>
							<td><input id="loginBtn" type="button" value="登录" />
								&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" value="重置" /></td>
						</tr>
					</table>
				</form>
			</div>
		</div>

	</div>
</body>
<script type="text/javascript" src="/wc/statics/js/login.js"></script>
</html>