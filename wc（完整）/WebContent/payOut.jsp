<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>支出管理</title>

<!-- 静态包含引入必要的CSS以及JS文件 -->
<%@ include file="/commons/commons.jsp" %>

</head>
<body>
<!-- 工具栏 begin -->
<div id="tb">
	<div>
		<a href="javascript:openAddDialog()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true">添加</a>
		<a href="javascript:openUpdateDialog()" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true">修改</a>
		<a href="javascript:deletePayOut()" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true">删除</a>
	</div>
	<div>
		支出名称：<input id="outNameSearch" class="easyui-validatebox" />
		支出类型：<select id="outTypeSearch" class="easyui-combobox" style="width:160px;" data-options="editable:false">   
				</select>  
		创建时间：<input id="createTimeSearch"  type= "text" class= "easyui-datebox" data-options="editable:false"></input>   
		<a href="javascript:searchPayOut()" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
	</div>
</div>
<!-- 工具栏 end -->

<!-- 数据表格 begin -->
<table id="payOutData" class="easyui-datagrid" style="fit:true"   
        data-options="url:'payOut',rownumbers:true,toolbar:'#tb',pagination:true,fit:true">   
    <thead>   
        <tr>
        	<th data-options="field:'parentId',hidden:true"></th>  
        	<th data-options="field:'outTypeId',hidden:true"></th>  
        	<th data-options="field:'ck',checkbox:true"></th>    
            <th data-options="field:'id',width:'10%'">编号</th>   
            <th data-options="field:'outName',width:'10%'">支出名称</th>   
            <th data-options="field:'typeName',width:'10%'">支出类型</th>
            <th data-options="field:'money',width:'10%'">支出金额</th>  
            <th data-options="field:'accountName',width:'10%'">所属账户</th>   
            <th data-options="field:'remark',width:'10%'">支出备注</th>  
            <th data-options="field:'createTime',width:'20%'">创建时间</th>        
            <th data-options="field:'updateTime',width:'20%'">修改时间</th>        
        </tr>   
    </thead>   
</table>  
<!-- 数据表格 end-->

<!-- 添加对话框 begin -->
<div id="addDialog" class="easyui-dialog" title="添加支出" style="width:400px;height:300px;"   
        data-options="iconCls:'icon-add',modal:true,closed:true">   
    <form id="addForm" style="margin-top: 10px">
    	<table align="center">		
    			<tr>
    				<td>支出名称：</td>
    				<td>
    					<input id="outName" class="easyui-validatebox" data-options="required:true,missingMessage:'不能为空'" /> 
    				</td>
	    		</tr>
	    		<tr>
	    			<td>支出类型：</td>
	    			<td>
	    				<select id="outType" class="easyui-combobox" style="width:160px;" data-options="editable:false">    
						</select>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td></td>
	    			<td>
	    				<select id="outSonType" class="easyui-combobox" style="width:160px;" data-options="editable:false">    
						</select>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>所属账户：</td>
	    			<td>
	    				<select id="accountId" class="easyui-combobox" style="width:160px;" data-options="editable:false">   
						</select>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>支出金额：</td>
	    			<td>
	    				<input id="money" type="text" class="easyui-numberbox" value="100" data-options="min:0,precision:2"></input>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>支出备注：</td>
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
	    				<a style="margin-left: 20px" href="javascript:addPayOut()" class="easyui-linkbutton" data-options="iconCls:'icon-save'">保存</a>  
	    			</td>
	    			<td>
	    				<a style="margin-left: 75px" href="javascript:closeAddDialog()" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'">取消</a>  
	    			</td>
	    		</tr>
    	</table>
    </form>
</div> 
<!-- 添加对话框 end  -->

<!-- 修改对话框 begin -->
<div id="updateDialog" class="easyui-dialog" title="修改支出" style="width:400px;height:300px;"   
        data-options="iconCls:'icon-add',modal:true,closed:true">   
    <form id="updateForm" style="margin-top: 10px">
    	<table align="center">
    			<input type="hidden" id="payOutId" name="payOutId"/>		
    			<tr>
    				<td>支出名称：</td>
    				<td>
    					<input id="outNameUp" name="outNameUp" class="easyui-validatebox" data-options="required:true,missingMessage:'不能为空'" /> 
    				</td>
	    		</tr>
	    		<tr>
	    			<td>支出类型：</td>
	    			<td>
	    				<select id="outTypeUp" name="outTypeUp" class="easyui-combobox" style="width:160px;" data-options="editable:false">    
						</select>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td></td>
	    			<td>
	    				<select id="outSonTypeUp" name="outSonTypeUp" class="easyui-combobox" style="width:160px;" data-options="editable:false">    
						</select>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>所属账户：</td>
	    			<td>
	    				<select id="accountIdUp" name="accountIdUp" class="easyui-combobox" style="width:160px;" data-options="editable:false">   
						</select>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>支出金额：</td>
	    			<td>
	    				<input id="moneyUp" name="moneyUp" type="text" class="easyui-numberbox" value="100" data-options="min:0,precision:2"></input>  
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>支出备注：</td>
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
	    				<a style="margin-left: 20px" href="javascript:updatePayOut()" class="easyui-linkbutton" data-options="iconCls:'icon-save'">保存</a>  
	    			</td>
	    			<td>
	    				<a style="margin-left: 75px" href="javascript:closeUpdateDialog()" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'">取消</a>  
	    			</td>
	    		</tr>
    	</table>
    </form>
</div> 
<!-- 修改对话框 end  -->

</body>
<script type="text/javascript" src="/wc/statics/js/payOut.js"></script>
</html>