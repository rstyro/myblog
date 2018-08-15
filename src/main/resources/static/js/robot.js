var _ctx = $("meta[name='_ctx']").attr("content");
_ctx = _ctx.substr(0, _ctx.length - 1);
var robotImg = "/images/rebotImg/robot.png";
$(function(){
	 $("#fh5co-main-menu ul li:eq(5)").addClass("fh5co-active");
	$(".main").css("min-height",($(window).height()-50)+"px");
	$("#wechatCode").click(function(){
		$("#myWechatCode").modal("show");
	});
	 $("[data-toggle='tooltip']").tooltip();
	 
	 addNode(robotImg,"欢迎欢迎，热烈欢迎.....","info-left");
		
	 $("body").keydown(function() {
         if (event.keyCode == "13") {//keyCode=13是回车键
        	 var content = $("#chat-content").val();
 			content = $.trim(content);
 			if(CMD(content)){
 				content = htmlEncodeJQ(content);
 				addNode($("#user_path").val(),content,"info-right");
 				requesAnswer(content);
 			}
 			$("#chat-content").val("");
         }
     });
	
	//点击发送按钮
	$("#sendBtn").click(function(){
		var content = $("#chat-content").val();
		content = $.trim(content);
		if(CMD(content)){
			content = htmlEncodeJQ(content);
			addNode($("#user_path").val(),content,"info-right");
			requesAnswer(content);
		}
	});
	
	$("#submitQuestion").click(function(){
		requesQuest($("#question").val(),$("#answer").val());
	});
	
	//学习模式
	$("#learnModel").click(function(){
		var auth = $("#auth_robot").val();
		if(auth != "1"){
			alert("你没有权限调用");			
		}else{
			$("#updateBrain").modal("show");
		}
	});
	
	//普通模式
	$("#normalModel").click(function(){
		alert("已是普通模式");
	});
	
	//调情模式
	$("#flirtModel").click(function(){
		alert("太污了，暂未开放");
	});
	 
	mobileMenuOutsideClick();
});

//添加信息
function addNode(img,content,direction){
	var rb = Robot(img,content);
	var node = createNode(direction,rb);
	var scrollHeight=document.getElementById("chatBody").scrollHeight;
	var animateH = scrollHeight;
	bottomAnimate($("#chatBody"),animateH,500);
	$(".chat-body").append(node);
}
//指令
function CMD(content){
	$("#chat-content").val("");
	if(content == ""){
		return false;
	}
	if(content == "指令"){
		addNode(robotImg,"1.//:clear  --> 清除聊天记录<br>2.指令   --> 打印所有指令列表<br>","info-left");
		return false;
	}
	if(content == "时间" || content == "time"){
		addNode(robotImg,getNowDateFormat(),"info-left");
		return false;
	}
	if(content == "//:clear"){
		$(".chat-body").empty();
		return false;
	}
	return true;
}

//请求答案
function requesAnswer(content){
	$.ajax({
		type:"post",
		url:_ctx+"/robot/answer",
		cache:false,
		dataType:"json",
		data:{content:content,_t:new Date().getTime()},
		success:function(data){
			if(data.status == "success"){
				addNode(robotImg,replace_em(data.data),"info-left");
			}else{
				addNode(robotImg,data.msg,"info-left");
			}
			$("#chat-content").val("");
		}
	});
}

//请求答案
function requesQuest(question,answer){
	$.ajax({
		type:"post",
		url:_ctx+"/robot/question",
		cache:false,
		dataType:"json",
		data:{question:question,answer:answer,_t:new Date().getTime()},
		success:function(data){
			if(data.status == "success"){
				$("#question").val("");
				$("#answer").val("");
			}else{
				alert(data.msg);
			}
		}
	});
}

//创建 普通 消息体节点
function createNode(direction,obj){
	var node = "<div class='info "+direction+"'><div class='info-header'><img src='"+obj.head_path+"'/></div>"
				+"<span class='info-body'><span class='content'><em></em> "+obj.content+"</span></span></div>";
	return node;
}
//对象
function Robot(head_path,content){
	var obj = new Object();
	obj.head_path=head_path;
	obj.content=content;
	return obj;
}
//动画
function bottomAnimate(el,scrollHeight,speed){
	el.stop().animate({scrollTop: scrollHeight+'px'},speed)
}

//html 过滤编码
function htmlEncodeJQ ( str ) {  
    return $('<span/>').text( str ).html();  
}

//表情格式替换
function replace_em(str){
	str = str.replace(/\n/g,'<br/>');
	var reg = /\[qq_([0-9]*)\]/g;
	var arr=str.match(reg); 
	if(arr != null){
		for(var i=0;i<arr.length;i++){
			var regIndex = arr[i].indexOf('_');
			var num = arr[i].substring(regIndex+1,arr[i].length-1);
			if(num <= 75 && num > 0){
				str = str.replace(arr[i],"<img src='/comment/face/emoji1/"+num+".gif' />");
			}
		}
	}
	//em 表情
	var emReg = /\[em_([0-9]*)\]/g;
	var emArr=str.match(emReg); 
	if(emArr != null){
		for(var j=0;j<emArr.length;j++){
			var emRegIndex = emArr[j].indexOf('_');
			var emNum = emArr[j].substring(emRegIndex+1,emArr[j].length-1);
			if(emNum <= 52 && num > 0){
				str = str.replace(emArr[j],"<img src='/comment/face/emoji2/"+emNum+".png' />");
			}
		}
	}
	return str;

}


//获取当前时间
function getNowDateFormat(){
	var nowDate = new Date();
	var year = nowDate.getFullYear();
	var month = filterNum(nowDate.getMonth()+1);
	var day = filterNum(nowDate.getDate());
	var hours = filterNum(nowDate.getHours());
	var min = filterNum(nowDate.getMinutes());
	var seconds = filterNum(nowDate.getSeconds());
	return year+"-"+month+"-"+day+" "+hours+":"+min+":"+seconds;
}
function filterNum(num){
	if(num < 10){
		return "0"+num;
	}else{
		return num;
	}
}
//Click outside of offcanvass
var mobileMenuOutsideClick = function() {

	//左侧点击
	$('.js-fh5co-nav-toggle').on('click', function(event){
		event.preventDefault();
		var $this = $(this);

		if ($('body').hasClass('offcanvas')) {
			$this.removeClass('active');
			$('body').removeClass('offcanvas');	
		} else {
			$this.addClass('active');
			$('body').addClass('offcanvas');	
		}
	});
	
	$(document).click(function (e) {
    var container = $("#fh5co-aside, .js-fh5co-nav-toggle");
    if (!container.is(e.target) && container.has(e.target).length === 0) {

    	if ( $('body').hasClass('offcanvas') ) {

			$('body').removeClass('offcanvas');
			$('.js-fh5co-nav-toggle').removeClass('active');
		
    	}
    	
    }
	});

	$(window).scroll(function(){
		if ( $('body').hasClass('offcanvas') ) {

			$('body').removeClass('offcanvas');
			$('.js-fh5co-nav-toggle').removeClass('active');
		
    	}
	});

};