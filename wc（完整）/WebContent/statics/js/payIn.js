function searchPayIn(){
	// 获取查询条件参数
	var inName = $("#inNameSearch").val();
	var inType = $("#inTypeSearch").combo("getValue");
	var createTime = $("#createTimeSearch").combo("getValue");
	
	// 通过datagrid的方法load，将条件传到后台并且响应数据过来
	$('#payInData').datagrid('load',{
		'inName': inName,
		'inType': inType,
		'createTime': createTime
	});

}

/*************************添加收入****************************/
/**
 * 给添加按钮绑定点击事件
 */
function openAddDialog(){
	// 重置对话框表单中的数据（使用form里的reset方法）
	$("#addForm").form("reset");
	// 获取当前用户的所有账户信息并加载到combobox中
	$('#accountId').combobox({
	    url:'account?actionName=queryAccountListByUid',
	    valueField:'id',    
	    textField:'accountName',
	    value:'请选择所属账户'
	}); 
	//弹出添加对话框（使用dialog的open方法）
	$("#addDialog").dialog("open");
}

/**
 * 给取消按钮绑定点击事件
 */
function closeAddDialog(){
	$("#addDialog").dialog("close");
}

function addPayIn(){
	// 获取表单里的参数
	var inName = $("#inName").val();
	var inType = $("#inType").combo("getValue");
	var accountId = $("#accountId").combo("getValue");
	var money = $("#money").val();
	var remark = $("#remark").val();
	// 进行非空判断
	if(isEmpty(inName)){
		$.messager.alert("添加收入","请输入收入名称！","warning");
		return;
	}
	if(isEmpty(inType)){
		$.messager.alert("添加收入","请选择收入类型！","warning");
		return;
	}
	if(accountId == "请选择所属账户"){
		$.messager.alert("添加收入","请输入所属账户！","warning");
		return;
	}
	if(isEmpty(money)){
		$.messager.alert("添加收入","请输入收入金额！","warning");
		return;
	}
	// ajax提交数据到后台，并且得到回调函数里后台响应过来的result
	$.ajax({
		type:"post",
		url:"payIn",
		data:{
			'inName':inName,
			'inType':inType,
			'accountId':accountId,
			'money':money,
			'remark':remark,
			'actionName':'addPayIn'
		},
		success:function(result){
			if(result == 1){
				// 弹出提示框提示用户成功了
				$.messager.alert("添加收入","添加成功！","info");
				// 刷新数据表格
				$("#payInData").datagrid("reload");
				// 关闭对话框
				$("#addDialog").dialog("close");
			}else{
				$.messager.alert("添加收入","添加失败！","error");
			}
		}
	});
}

/***********************************修改收入 begin******************************/
/**
 * 打开修改对话框
 */
function openUpdateDialog(){
	// 判断是否有选中的记录
	var obj =$("#payInData").datagrid("getSelected");
	console.log(obj);
	if(obj == null){
		// 否：弹出提示框警告用户
		$.messager.alert('修改收入','请至少选中一条记录！','warning');
		return;
	}
	
	// 加载所属用户
	$('#accountIdUp').combobox({
	    url:'account?actionName=queryAccountListByUid',
	    valueField:'id',    
	    textField:'accountName'
	}); 
	
	// 填充选中记录中的数据到修改账户对话框中的表单里
	$('#updateForm').form('load',{
		inNameUp:obj.inName,
		inTypeUp:obj.inType,
		accountIdUp:obj.accountId,
		moneyUp:obj.money,
		remarkUp:obj.remark,
		pid:obj.id
	});
	// 打开对话框
	$("#updateDialog").dialog("open");
}

/**
 * 关闭修改对话框
 */
function closeUpdateDialog(){
	$("#updateDialog").dialog("close");
}

/**
 * 给保存按钮绑定点击事件
 */ 
function updatePayIn(){
	//获取表单中的数据
	var inName = $("#inNameUp").val();
	var inType = $("#inTypeUp").combo("getValue");
	var accountId = $("#accountIdUp").combo("getValue");
	var money = $("#moneyUp").val();
	var remark = $("#remarkUp").val();
	var pid =$("#pid").val();
	//非空判断
	if(isEmpty(inName)){
		// 提示用户
		$.messager.alert('修改收入','收入名称不能为空！','warning');
		return;
	}
	if(isEmpty(inType)){
		// 提示用户
		$.messager.alert('修改收入','请选中收入类型！','warning');
		return;
	}
	if(isEmpty(accountId)){
		// 提示用户
		$.messager.alert('修改收入','请选中所属账户！','warning');
		return;
	}
	if(isEmpty(money)){
		// 提示用户
		$.messager.alert('修改收入','请输入收入金额！','warning');
		return;
	}
	if(isEmpty(pid)){
		// 提示用户
		$.messager.alert('修改收入','系统错误！','warning');
		return;
	}
	//通过ajax将数据提交到后台（actionName、accountId）
	$.ajax({
		type:"post",
		url:"payIn",
		data:{
			'inName':inName,
			'inType':inType,
			'accountId':accountId,
			'money':money,
			'remark':remark,
			'pid':pid,
			'actionName':'updatePayIn',
		},
		success:function(result){
			// =1 成功 提示成功、刷新数据表格、关闭对话框
			if(result == 1){
				// 提示成功
				$.messager.alert('修改收入','修改成功！','info');
				// 刷新数据表格
				$("#payInData").datagrid("reload");
				// 关闭对话框
				closeUpdateDialog();
			}else{
				// =0 失败 提示失败
				$.messager.alert('修改收入','修改失败！','error');
			}
			
		}
	});

}

/***********************************修改收入 end******************************/

/**********************************删除收入*******************************/
/**
 * 给工具栏的删除按钮绑定点击事件
 */
function deletePayIn(){
	// 判断是否选中记录(使用datagrid的方法：getChecked  在复选框呗选中的时候返回所有行)
	var objs = $("#payInData").datagrid("getChecked");
	if(objs.length < 1){
		// 否，提示用户至少选中一条记录
		$.messager.alert("删除收入","请选中至少一条记录！","warning");
		return;
	}
	// 获取要删除记录的id，并拼接成需要的格式 1,2,3
	var ids = "";
	for(var i = 0; i < objs.length ; i++){
		var obj = objs[i];
		if(i == objs.length - 1){
			ids += obj.id;
		}else{
			ids += obj.id+",";
		}
	}
	// 提示用户是否要删除该记录（使用message的方法）
	$.messager.confirm('确认对话框', '您确认要删除吗？', function(r){
			if (r){
				// ajax请求后台进行删除操作，回调函数接受后台的响应result;
				$.ajax({
					type:"post",
					url:"payIn",
					data:{
						'ids':ids,
						'actionName':'deletePayin'
					},
					success:function(result){
						if(result != 0){
						    //提示成功删除几条记录、刷新数据表格
							$.messager.alert("删除收入","成功删除"+result+"条记录！","info");
							$("#payInData").datagrid("reload");
						}else{
							//=0 失败 提示失败
							$.messager.alert("删除收入","删除失败！","error");
						}						
					}
				});
			}
	});
}