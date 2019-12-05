function searchAccount(){
	// 获取查询条件参数
	var accountName = $("#accountNameSearch").val();
	var accountType = $("#accountTypeSearch").combo("getValue");
	var createTime = $("#createTimeSearch").combo("getValue");
	
	// 通过datagrid的方法load，将条件传到后台并且响应数据过来
	$('#accountData').datagrid('load',{
		'accountName': accountName,
		'accountType': accountType,
		'createTime': createTime
	});

}


/***********************************添加账户 begin********************************/
/**
 * 给添加按钮绑定点击事件
 */
function openAddDialog(){
	// 重置表单
	$("#addForm").form("reset");
	// 打开对话框
	$("#addDialog").dialog("open");
}

/**
 * 给取消按钮绑定点击事件
 */
function closeAddDialog(){
	// 关闭对话框
	$("#addDialog").dialog("close");
}

/**
 * 给保存按钮绑定点击事件
 */
function addAccount(){
	//接收参数
	var accountName = $("#accountName").val();
	var accountType = $("#accountType").combo("getValue");
	console.log(accountType);
	var money = $("#money").val();
	var remark = $("#remark").val();
	//非空判断
	if(isEmpty(accountName)){
		// 提示用户
		$.messager.alert('添加账户','账户名称不能为空！','warning');
		return;
	}
	if(isEmpty(accountType)){
		// 提示用户
		$.messager.alert('添加账户','请选中账户类型！','warning');
		return;
	}
	if(isEmpty(money)){
		// 提示用户
		$.messager.alert('添加账户','账户金额不能为空！','warning');
		return;
	}
	//通过ajax将数据提交后台
	$.ajax({
		type:"post",
		url:"account",
		data:{
			'accountName':accountName,
			'accountType':accountType,
			'money':money,
			'remark':remark,
			'actionName':'addAccount'
		},
		success:function(result){
			//	回调函数  result
			if(result == 1){
				//	=1成功
				//	提示添加成功
				$.messager.alert('添加账户','添加成功！','info');
				//	刷新数据表格
				$("#accountData").datagrid("reload");
				//	关闭对话框
				closeAddDialog();
			}else{
				//	=0失败
				//	提示添加失败
				$.messager.alert('添加账户','添加失败！','error');
			}

		}
	});
}
/***********************************添加账户 end********************************/



/***********************************修改账户 begin******************************/
/**
 * 打开修改对话框
 */
function openUpdateDialog(){
	// 判断是否有选中的记录
	var obj =$("#accountData").datagrid("getSelected");
	console.log(obj);
	if(obj == null){
		// 否：弹出提示框警告用户
		$.messager.alert('修改账户','请至少选中一条记录！','warning');
		return;
	}
	// 填充选中记录中的数据到修改账户对话框中的表单里
	$('#updateForm').form('load',{
		accountNameUp:obj.accountName,
		accountTypeUp:obj.accountType,
		moneyUp:obj.money,
		remarkUp:obj.remark,
		accountIdUp:obj.id
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
 * 保存账户操作
 */
function UpdateAccount(){
	//获取表单中的数据
	var accountName = $("#accountNameUp").val();
	var accountType = $("#accountTypeUp").combo("getValue");
	var money = $("#moneyUp").val();
	var remark = $("#remarkUp").val();
	var accountId = $("#accountIdUp").val();
	//非空判断
	if(isEmpty(accountName)){
		// 提示用户
		$.messager.alert('修改账户','账户名称不能为空！','warning');
		return;
	}
	if(isEmpty(accountType)){
		// 提示用户
		$.messager.alert('修改账户','请选中账户类型！','warning');
		return;
	}
	if(isEmpty(money)){
		// 提示用户
		$.messager.alert('修改账户','账户金额不能为空！','warning');
		return;
	}
	//通过ajax将数据提交到后台（actionName、accountId）
	$.ajax({
		type:"post",
		url:"account",
		data:{
			'accountName':accountName,
			'accountType':accountType,
			'money':money,
			'remark':remark,
			'actionName':'updateAccount',
			'accountId':accountId
		},
		success:function(result){
			// =1 成功 提示成功、刷新数据表格、关闭对话框
			if(result == 1){
				// 提示成功
				$.messager.alert('修改账户','修改成功！','info');
				// 刷新数据表格
				$("#accountData").datagrid("reload");
				// 关闭对话框
				closeUpdateDialog();
			}else{
				// =0 失败 提示失败
				$.messager.alert('修改账户','修改失败！','error');
			}
			
		}
	});

}
/***********************************修改账户 end******************************/


/***********************************删除账户 begin******************************/
/**
 * 给工具栏的删除按钮绑定点击事件
 */
function deleteAccount(){
	//判断是否选中记录(使用datagrid的方法：getChecked  在复选框呗选中的时候返回所有行)
	var objs =$("#accountData").datagrid("getChecked");
	console.log(objs);
	if(objs.length < 1){
		// 否，提示用户至少选中一条记录
		$.messager.alert('删除账户','请给我至少选中一条记录好吧！','warning');
		return;
	}
	
	//获取要删除记录的id，并拼接成需要的格式
	var ids = "";
	for(var i = 0; i < objs.length; i++){
		// 拿到当前记录对象
		var obj = objs[i];
		if(i == objs.length - 1){
			ids += obj.id;
		}else{
			ids += obj.id + ",";
		}
	}
	console.log(ids);
    //提示用户是否要删除该记录（使用message的方法）
	$.messager.confirm('删除账户', '您真的确认要删除吗？', function(r){
		if (r){
		    // ajax请求后台进行删除操作，回调函数接受后台的响应result;
			$.ajax({
				type:"post",
				url:"account",
				data:{
					'actionName':'deleteAccount',
					'ids':ids
				},
				success:function(result){
				    // =1 成功 提示成功、刷新数据表格
					if(result == 1){
						// 提示成功
						$.messager.alert('删除账户','删除成功！','info');
						// 刷新数据表格
						$("#accountData").datagrid("reload");
					}else{
						// =0 失败 提示失败
						$.messager.alert('删除账户','删除失败！','error');
					}
					
				}
			});

		}
	});

}
/***********************************删除账户 end******************************/
