$(function(){
		$("#submit").click(function(){
			$("#seachForm").submit();
		});
		
		//更改
		$("#submitQuestion").click(function(){
			requesQuest($("#_id").val(),$("#question").val(),$("#answer").val());
		});
	})
	
	//更改
	function requesQuest(id,question,answer){
		$.ajax({
			type:"post",
			url:_ctx+"/robot/update",
			cache:false,
			dataType:"json",
			data:{_id:id,question:question,answer:answer,_t:new Date().getTime()},
			success:function(data){
				if(data.status == "success"){
					$("#updateBrain").modal("hide");
					setTimeout('reloadPage()',500);
				}else{
					alert(data.msg);
				}
			}
		});
	}
	
	//编辑
	function edit(id){
		$.ajax({
			type:"get",
			url:_ctx+"/robot/find",
			cache:false,
			dataType:"json",
			data:{id:id},
			success:function(data){
				if(data.status == "success"){
					var obj = data.data;
					$("#question").val(obj.question);
					$("#answer").val(obj.answer);
					$("#_id").val(obj._id);
				}else{
					alert(data.msg);
				}
			}
		});
		$("#updateBrain").modal("show");
	}
	
	//删除
	function del(id){
		if(confirm("你确定要删除吗？")){
			$.ajax({
				type:"post",
				url:_ctx+"/robot/del",
				cache:false,
				dataType:"json",
				data:{id:id},
				success:function(data){
					if(data.status == "success"){
						 setTimeout('reloadPage()',500);
					}else{
						alert(data.msg);
					}
				}
			});
		}
		
	}
	function reloadPage(){
		 window.location.reload();
	}