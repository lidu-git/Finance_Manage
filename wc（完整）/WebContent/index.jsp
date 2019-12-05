<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>首页</title>

<!-- 静态包含引入必要的CSS以及JS文件 -->
<%@ include file="/commons/commons.jsp" %>

</head>
<!-- <a href="/wc/user?actionName=logout">注销</a> -->
<body class="easyui-layout"> 

	<!-- 上begin  -->  
    <div data-options="region:'north'" style="height:100px; background-image: url('/wc/statics/img/dog.jpg'); background-repeat: no-repeat;">
    	<div style="margin-top: 75px;margin-left: 90px;font-size: 13px">
    		<a href="/wc/index.jsp" style="text-decoration: none;">旺财管理系统</a>
    		<span style="float: right;margin-right: 20px;font-size: 17px">当前登录用户：<b>${user.name }</b>
    		&nbsp;&nbsp;&nbsp;&nbsp;
    		<a href="/wc/user?actionName=logout">注销</a></span>
    	</div>
    </div> 
    <!-- 上end -->  
    
    <!-- 下begin  -->  
    <div data-options="region:'south'" style="height:50px;text-align: center;color: blue;">
    	<br>
		Copyright © 2006-2026 旺财 All Rights Reserved 电话：0086-88888888
		QQ：123456 <a href="#" class="go-top"> <i class="icon-bon-arrow-up"></i></a>
    </div>   
    <!-- 下end --> 
    
    <!-- <div data-options="region:'east',iconCls:'icon-reload',title:'East',split:true" style="width:100px;"></div>    -->
    
    <!-- 左begin  -->  
    <div data-options="region:'west',title:'菜单导航栏'" style="width:170px;">
	    <div id="aa" class="easyui-accordion" style="width:100%;height:100%;">   
	    <div title="用户管理" data-options="iconCls:'icon-man'" style="overflow:auto;padding:10px;">  
	    	<a href="javascript:openTab('账户管理','icon-fwgl','/wc/account.jsp')" class="easyui-linkbutton" data-options="iconCls:'icon-fwgl',plain:true">账户管理</a> 
	    	<a href="javascript:openTab('收入管理','icon-yxjhgl','/wc/payIn.jsp')" class="easyui-linkbutton" data-options="iconCls:'icon-yxjhgl',plain:true">收入管理</a>
	    	<a href="javascript:openTab('支出管理','icon-yxjhgl','/wc/payOut.jsp')" class="easyui-linkbutton" data-options="iconCls:'icon-yxjhgl',plain:true">支出管理</a>
	    	<a href="javascript:openTab('报表管理','icon-tjbb','/wc/echarts.jsp')" class="easyui-linkbutton" data-options="iconCls:'icon-tjbb',plain:true">报表管理</a> 
	    </div>   
	    <div title="系统管理" data-options="iconCls:'icon-item'" style="padding:10px;">   
	    	<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-item',plain:true">系统设置</a>
	    </div>   
</div>  
    </div>   
    <!-- 左end --> 
    
    <!-- 中begin  -->
    <div data-options="region:'center'" style="padding:5px;background:#eee;">
    	<div id="tabs" class="easyui-tabs" data-options="fit:true">   
		    <div title="主页" iconCls='icon-home' style="padding:20px;text-align: center;" >   
		        <h1>欢迎来到旺财管理系统！</h1>    
		    </div>   
		      
		</div>  
    </div>   
    <!-- 中end --> 
</body>
<script type="text/javascript" src="/wc/statics/js/index.js"></script>
</html>