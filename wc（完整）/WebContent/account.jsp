<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>账户管理</title>

<!-- 静态包含引入必要的CSS以及JS文件 -->
<%@ include file="/commons/commons.jsp" %>

</head>
<body>
<!-- 工具栏 begin -->
<div id="tb">
	<div>
		<a href="javascript:openAddDialog()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true">添加</a>
		<a href="javascript:openUpdateDialog()" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true">修改</a>
		<a href="javascript:deleteAccount()" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true">删除</a>
	</div>
	<div>
		账户名称：<input id="accountNameSearch" class="easyui-validatebox" />
		账户类型：<select id="accountTypeSearch" class="easyui-combobox" style="width:160px;" data-options="editable:false">   
				    <option value="">请选择账户类型</option>   
				    <option>招商</option>   
				    <option>工商</option>   
				    <option>建设</option>   
				    <option>农业</option>   
				</select>  
		创建时间：<input id="createTimeSearch"  type= "text" class= "easyui-datebox" data-options="editable:false"></input>   
		<a href="javascript:searchAccount()" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
	</div>
</div>
<!-- 工具栏 end -->


<!-- 添加对话框 begin -->
<div id="addDialog" class="easyui-dialog" title="添加账户" style="width:400px;height:250px;"   
        data-options="iconCls:'icon-add',modal:true,closed:true">   
    <form id="addForm" style="margin-top: 10px">
    	<table align="center">		
    			<tr>
    				<td>账户名称：</td>
    				<td>
    					<input id="accountName" class="easyui-validatebox" data-options="required:true,missingMessage:'不能为空'" /> 
    				</td>
	    		</tr>
	    		<tr>
	    			<td>账户类型：</td>
	    			<td>
	    				<select id="accountType" class="easyui-combobox" style="width:160px;" data-options="editable:false">   
						    <option value="">请选择账户类型</option>   
						    <option>招商</option>   
						    <option>工商</option>   
						    <option>建设</option>   
						    <option>农业</option>   
						</select>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>账户金额：</td>
	    			<td>
	    				<input id="money" type="text" class="easyui-numberbox" value="100" data-options="min:0,precision:2"></input>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>账户备注：</td>
	    			<td>
	    				<input id="remark" class="easyui-validatebox" />
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>&nbsp;&nbsp;</td>
	    			<td>&nbsp;&nbsp;</td>
	    		</tr>
	    		<tr>
	    			<td>
	    				<a style="margin-left: 20px" href="javascript:addAccount()" class="easyui-linkbutton" data-options="iconCls:'icon-save'">保存</a>  
	    			</td>
	    			<td>
	    				<a style="margin-left: 75px" href="javascript:closeAddDialog()" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'">取消</a>  
	    			</td>
	    		</tr>
    	</table>
    </form>
</div> 
<!-- 添加对话框 end --> 


<!-- 修改对话框 begin -->
<div id="updateDialog" class="easyui-dialog" title="修改账户" style="width:400px;height:250px;"   
        data-options="iconCls:'icon-edit',modal:true,closed:true">   
    <form id="updateForm" style="margin-top: 10px">
    	<table align="center">	
    			<input type="hidden" id="accountIdUp" name="accountIdUp"/>	
    			<tr>
    				<td>账户名称：</td>
    				<td>
    					<input id="accountNameUp" name="accountNameUp" class="easyui-validatebox" data-options="required:true,missingMessage:'不能为空'" /> 
    				</td>
	    		</tr>
	    		<tr>
	    			<td>账户类型：</td>
	    			<td>
	    				<select id="accountTypeUp" name="accountTypeUp" class="easyui-combobox" style="width:160px;" data-options="editable:false">   
						    <option value="">请选择账户类型</option>   
						    <option>招商</option>   
						    <option>工商</option>   
						    <option>建设</option>   
						    <option>农业</option>   
						</select>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>账户金额：</td>
	    			<td>
	    				<input id="moneyUp" name="moneyUp" type="text" class="easyui-numberbox" value="100" data-options="min:0,precision:2"></input>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>账户备注：</td>
	    			<td>
	    				<input id="remarkUp" name="remarkUp" class="easyui-validatebox" />
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>&nbsp;&nbsp;</td>
	    			<td>&nbsp;&nbsp;</td>
	    		</tr>
	    		<tr>
	    			<td>
	    				<a style="margin-left: 20px" href="javascript:UpdateAccount()" class="easyui-linkbutton" data-options="iconCls:'icon-save'">保存</a>  
	    			</td>
	    			<td>
	    				<a style="margin-left: 75px" href="javascript:closeUpdateDialog()" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'">取消</a>  
	    			</td>
	    		</tr>
    	</table>
    </form>
</div> 
<!-- 修改对话框 end --> 

<!-- 数据表格 begin -->
<table id="accountData" class="easyui-datagrid" style="fit:true"   
        data-options="url:'account?actionName=queryAccountList',rownumbers:true,toolbar:'#tb',pagination:true,fit:true">   
    <thead>   
        <tr>
        	<th data-options="field:'ck',checkbox:true"></th>    
            <th data-options="field:'id',width:'10%'">编号</th>   
            <th data-options="field:'accountName',width:'10%'">账户名称</th>   
            <th data-options="field:'accountType',width:'10%'">账户类型</th>
            <th data-options="field:'money',width:'10%'">账户金额</th>   
            <th data-options="field:'remark',width:'20%'">账户备注</th>  
            <th data-options="field:'createTime',width:'20%'">创建时间</th>        
            <th data-options="field:'updateTime',width:'20%'">修改时间</th>        
        </tr>   
    </thead>   
</table>  
<!-- 数据表格 end-->


</body>
<script type="text/javascript" src="/wc/statics/js/account.js"></script>
</html>