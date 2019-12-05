/**
 * 解决iframe  session失效后直接在iframe内联框架中显示页面问题
 */
if(top != self){
	if(top.location != self.location){
		top.location = self.location
	}
}

$("#loginBtn").click(function(){
	// 获取用户名和密码
	var uname = $("#uname").val();
	var upwd = $("#upwd").val();
	
	// 非空判断
	if(isEmpty(uname)){
		$("#msg").html("*用户名不能为空！");
		return;
	}
	if(isEmpty(upwd)){
		$("#msg").html("*密码不能为空！");
		return;
	}
	
	// 提交表单
	$("#loginForm").submit();
	
});