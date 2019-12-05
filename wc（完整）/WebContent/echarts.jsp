<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>报表管理</title>

<!-- 静态包含引入必要的CSS以及JS文件 -->
<%@ include file="/commons/commons.jsp" %>

<!-- 导入 ECharts -->
<script type="text/javascript" src="/wc/statics/js/echarts.js"></script>

</head>
<body>
    <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
    <div id="main" style="width: 100%;height:400px;"></div>
</body>
<script type="text/javascript">
	$.ajax({
		type:"post",
		url:"account",
		data:{
			'actionName':'queryAccountListByUid'
		},
		dataType:"json",
		success:function(data){
			// 准备数组，拼接需要的数据
			var accountNameArr = [];
			var moneyArr=[];
			// 循环遍历拼接数据
			for(var i = 0;i < data.length;i++){
				var obj = data[i];
				// 将数据添加到数组的最后面
				accountNameArr.push(obj.accountName);
				moneyArr.push(obj.money);
			}
			console.log(accountNameArr);
			// 将数据加载到echarts实例中
			loadAcountCharts(accountNameArr,moneyArr);
		}
		
	})
	
	// 加载账户分析图
	function loadAcountCharts(accountNameArr,moneyArr){
		
		   // 基于准备好的dom，初始化echarts实例
	    var myChart = echarts.init(document.getElementById('main'));

	    // 指定图表的配置项和数据
	    var option = {
	    	// 标题
	        title: {
	            text: '账户金额分析图',
	            x: 'center' // 居中
	        },
	        // 提示框组件
	        tooltip: {},
	        // 图例组件
	        legend: {
	            data:['金额'],// 要对应series中的name属性
	            left: 'right'
	        },
	        xAxis: {
	            data: accountNameArr
	            /*["衬衫","羊毛衫","雪纺衫","裤子","高跟鞋","袜子"]  */
	        },
	        yAxis: {},
	        series: [{
	            name: '金额',
	            type: 'bar',
	            data: moneyArr
	            /* [5, 20, 36, 10, 10, 20] */
	        }]
	    };

	    // 使用刚指定的配置项和数据显示图表。
	    myChart.setOption(option);
		
	}

</script>
</html>