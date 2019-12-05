//判断选项卡是否存在
function openTab(title,icon,url){
	var flag = $('#tabs').tabs('exists',title); 
	console.log(flag);
	if(flag){
		// 存在：选中该选项卡
		$('#tabs').tabs('select',title);
	}else{
		// 不存在：添加选项卡并选中
		$('#tabs').tabs('add',{    
		    title:title,
		    iconCls:icon,
		    selected:true,
		    content:"<iframe src='"+url+"' style='width:100%;height:100%'></iframe>",    
		    closable:true,    
		});  
	}

}
