$(function(){  // 预加载
	$('#outTypeSearch').combobox({
	    url:'payOut?actionName=queryPayOutType',
	    valueField:'id',    
	    textField:'typeName'
	}); 
})

function searchPayOut(){
	// 获取查询条件参数
	var outName = $("#outNameSearch").val();
	var outTypePid = $("#outTypeSearch").combo("getValue");
	var createTime = $("#createTimeSearch").combo("getValue");
	
	// 通过datagrid的方法load，将条件传到后台并且响应数据过来
	$('#payOutData').datagrid('load',{
		'outName': outName,
		'outTypePid': outTypePid,
		'createTime': createTime
	});

}

/*************************添加收入****************************//*
*//**
 * 给添加按钮绑定点击事件
 */
function openAddDialog(){
	// 重置表单
	$("#addForm").form("reset");
	// 加载支出类型（父类型、子类型）
	$('#outType').combobox({    
	    url:'payOut?actionName=queryPayOutType',    
	    valueField:'id',    
	    textField:'typeName',
	    onChange:function(newValue,oldValue){
	    	$('#outSonType').combobox({    
	    	    url:'payOut?actionName=queryPayOutType&pid='+newValue,    
	    	    valueField:'id',    
	    	    textField:'typeName',
	    	}); 
	    }
	});  
	// 加载所属账户
	$('#accountId').combobox({
	    url:'account?actionName=queryAccountListByUid',
	    valueField:'id',    
	    textField:'accountName',
	    value:'请选择所属账户'
	}); 
	
	// 打开对话框
	$("#addDialog").dialog("open");
}

/**
 * 给取消按钮绑定点击事件
 */
function closeAddDialog(){
	$("#addDialog").dialog("close");
}

/**
 * 给保存按钮绑定点击事件
 */
function addPayOut(){
	// 获取表单里的参数
	var outName = $("#outName").val();
	var outSonType = $("#outSonType").combo("getValue");
	var accountId = $("#accountId").combo("getValue");
	var money = $("#money").val();
	var remark = $("#remark").val();
	// 进行非空判断
	if(isEmpty(outName)){
		$.messager.alert("添加支出","请输入支出名称！","warning");
		return;
	}
	if(isEmpty(outSonType)){
		$.messager.alert("添加支出","请选择支出类型！","warning");
		return;
	}
	if(accountId == "请选择所属账户"){
		$.messager.alert("添加支出","请输入所属账户！","warning");
		return;
	}
	if(isEmpty(money)){
		$.messager.alert("添加支出","请输入支出金额！","warning");
		return;
	}
	// ajax提交数据到后台，并且得到回调函数里后台响应过来的result
	$.ajax({
		type:"post",
		url:"payOut",
		data:{
			'outName':outName,
			'outTypeId':outSonType,
			'accountId':accountId,
			'money':money,
			'remark':remark,
			'actionName':'addPayOut'
		},
		dataType:'json',
		success:function(result){
			if(result.code == 1){
				// 弹出提示框提示用户成功了
				$.messager.alert("添加支出",result.msg,"info");
				// 刷新数据表格
				$("#payOutData").datagrid("reload");
				// 关闭对话框
				$("#addDialog").dialog("close");
			}else{
				$.messager.alert("添加支出",result.msg,"error");
			}
		}
	});
}

/***********************************修改支出 begin******************************/
function openUpdateDialog(){
	// 判断是否选中（datagrid的 getSelected）
	var obj = $("#payOutData").datagrid("getSelected");
	// 否：提示请至少选一条
	if(obj == null){
		$.messager.alert("修改支出","请至少选中一条记录","warning");
		return;
	}
	// 加载支出类型（父类型、子类型）
	$('#outTypeUp').combobox({    
	    url:'payOut?actionName=queryPayOutType',    
	    valueField:'id',    
	    textField:'typeName',
	    onChange:function(newValue,oldValue){
	    	$('#outSonTypeUp').combobox({    
	    	    url:'payOut?actionName=queryPayOutType&pid='+newValue,    
	    	    valueField:'id',    
	    	    textField:'typeName',
	    	}); 
	    }
	});  
	
	// 加载所属账户
	$('#accountId').combobox({
	    url:'account?actionName=queryAccountListByUid',
	    valueField:'id',    
	    textField:'accountName',
	}); 

	// 将选中的记录填充进表单里
    $("#updateForm").form("load",{
    	'outNameUp':obj.outName,
    	'outTypeUp':obj.parentId,
    	'outSonTypeUp':obj.outTypeId,
    	'accountIdUp':obj.accountId,
    	'moneyUp':obj.money,
    	'remarkUp':obj.remark,
    	'payOutId':obj.id
    });
	
	// 弹出修改话框
    $("#updateDialog").dialog("open");
}

// 给取消按钮绑定点击事件
function closeUpdateDialog(){
	$("#updateDialog").dialog("close");
}

// 给保存按钮绑定点击事件
function updatePayOut(){
	// 获取表单里的参数
	var outNameUp = $("#outNameUp").val();
	var outTypeUp = $("#outTypeUp").combo("getValue");
	var outSonTypeUp = $("#outSonTypeUp").combo("getValue");
	var accountIdUp = $("#accountIdUp").combo("getValue");
	var moneyUp = $("#moneyUp").val();
	var remarkUp = $("#remarkUp").val();
	var payOutId = $("#payOutId").val();
	// 进行非空判断
	if(isEmpty(outNameUp)){
		$.messager.alert("修改支出","请输入支出名称！","warning");
		return;
	}
	if(isEmpty(outSonTypeUp)){
		$.messager.alert("修改支出","请选择支出类型！","warning");
		return;
	}
	if(accountIdUp == "请选择所属账户"){
		$.messager.alert("修改支出","请选择所属账户！","warning");
		return;
	}
	if(isEmpty(moneyUp)){
		$.messager.alert("修改支出","请输入支出金额！","warning");
		return;
	}
	if(isEmpty(payOutId)){
		$.messager.alert("修改支出","系统异常","warning");
		return;
	}
	// ajax提交数据到后台，并且得到回调函数里后台响应过来的result
	$.ajax({
		type:"post",
		url:"payOut",
		data:{
			'outName':outNameUp,
			'outTypeId':outSonTypeUp,
			'accountId':accountIdUp,
			'money':moneyUp,
			'remark':remarkUp,
			'payOutId':payOutId,
			'actionName':'updatePayOut'
		},
		dataType:'json',
		success:function(result){
			if(result == 1){
				// 弹出提示框提示用户成功了
				$.messager.alert("修改支出","修改成功","info");
				// 刷新数据表格
				$("#payOutData").datagrid("reload");
				// 关闭对话框
				$("#updateDialog").dialog("close");
			}else{
				$.messager.alert("修改支出","修改失败","error");
			}
		}
	});
}


/***********************************修改支出 end******************************/

/**********************************删除收入*******************************/
/**
 * 给工具栏的删除按钮绑定点击事件
 */
function deletePayOut(){
	// 判断是否选中记录(使用datagrid的方法：getChecked  在复选框呗选中的时候返回所有行)
	var objs = $("#payOutData").datagrid("getChecked");
	if(objs.length < 1){
		// 否，提示用户至少选中一条记录
		$.messager.alert("删除支出","请选中至少一条记录！","warning");
		return;
	}
	// 获取要删除记录的id，并拼接成需要的格式 1,2,3
	var ids = "";
	for(var i = 0; i < objs.length ; i++){
		var obj = objs[i];
		if(i == objs.length - 1){
			ids += obj.id;
			break;
		}
		ids += obj.id+",";
	}
	// 提示用户是否要删除该记录（使用message的方法）
	$.messager.confirm('确认对话框', '您确认要删除吗？', function(r){
			if (r){
				// ajax请求后台进行删除操作，回调函数接受后台的响应result;
				$.ajax({
					type:"post",
					url:"payOut",
					data:{
						'ids':ids,
						'actionName':'deletePayOut'
					},
					success:function(result){
						if(result != 0){
						    //提示成功删除几条记录、刷新数据表格
							$.messager.alert("删除支出","成功删除！","info");
							$("#payOutData").datagrid("reload");
						}else{
							//=0 失败 提示失败
							$.messager.alert("删除支出","删除失败！","error");
						}						
					}
				});
			}
	});
}