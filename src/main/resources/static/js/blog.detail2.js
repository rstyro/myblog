var tid = $("#tid").val();
var task=0;
var task_count=2;//执行两次，每500ms 执行一次 
var address="";
var thisEL="";
var floor=1;
var totalfloor=0;
var tmpFloor=0;
$(window).resize(function (){
	$("#fh5co-main").css("min-height",$(window).height()+"px");
});
$(document).ready(function() {
    var markdownView;
    
    markdownView = editormd.markdownToHTML("markdownView", {
        emoji           : true,
        taskList        : true,
        tex             : true,  // 默认不解析
        flowChart       : true,  // 默认不解析
        sequenceDiagram : true,  // 默认不解析
        previewTheme: "dark",//预览主题
    });
    $("#lrs-blog-main-left").css("min-height",$(window).height()+"px");
    
    $('#tree').ztree_toc({
		//is_auto_number: true,
		documment_selector: '#markdownView',
		ztreeStyle: {
			overflow: 'auto',
			position: 'absolute',
//			position: 'fixed',
			'z-index': 2147483647,
			border: '0px none',
		}
	});
    //cookie皮肤
    cookieSkin();
	$("[data-toggle='tooltip']").tooltip();
	
	//初始化评论列表
	getCommentByPage(1,5);
	$('.emotion').qqFace({
		id : 'facebox', 
		assign:'comment', 
		path:_ctx+'/face/arclist/'	//表情存放的路径
	});
	
	$("#praise").click(function(e){
		var iel = $(this).find("i");
		var praise = $(this).find("span").first();
		var numel = $(this).find("span.num");
		var num = numel.text();
		e = e || window.event;
		xponit = e.pageX || e.clientX + document.body.scroolLeft;
		yponit = e.pageY || e.clientY + document.body.scrollTop;
		var elment="";
		if(iel.hasClass("fa-heart-o")){
			iel.removeClass("fa-heart-o");
			iel.addClass("fa-heart");
			var newNum = Number(num)+1;
			numel.text(newNum);
			praise.text("已赞");
			elment = "<div class='pointanim' style='clear:both;position:absolute;top:"+(yponit-30)+"px;left:"+(xponit-30)+"px;color:red;text-align:center;z-index: 2147483647;font-size:2em;'><i class='fa fa-heart'> + 1</div>";
		}else if(iel.hasClass("fa-heart")){
			iel.removeClass("fa-heart");
			iel.addClass("fa-heart-o");
			var newNum = Number(num)-1;
			numel.text(newNum);
			praise.text("求赞");
			elment = "<div class='pointanim' style='clear:both;position:absolute;top:"+(yponit-30)+"px;left:"+(xponit-30)+"px;color:red;text-align:center;z-index: 2147483647;font-size:2em;'><i class='fa fa-heart-o'></div>";
		}
		console.log("yponit",yponit);
		$('body').append(elment);
		$(".pointanim").stop().animate({opacity:'0.5',top:'0'},yponit/0.8,function(){$(".pointanim").remove()});
		var aid = $(this).attr("aid");
		updatePraiseNum(aid,"article");
	});
	
	$("#reward").click(function(){
		$("#rewardModal").modal('show');
	});
	
	$("#wechatCode").click(function(){
		$("#myWechatCode").modal('show');
	});
	
	
	//黑色皮肤
	$("#black-skin").click(function(){
		blackSkin();
	});
	//白色皮肤
	$("#white-skin").click(function(){
		whiteSkin();
	});
	
	//点击发送按钮的事件
	$(".sendComment").click(function(){
		var userId = $("#user_id").val();
		if(userId == "0"){
			thisEL = $(this);
			$("#longConfirmModal").modal("show");
			return false;
		}else{
			sendData($(this));
		}
	});
	//添加回复框
	$(".addReplyBox").click(function(){
		addTextareabox($(this));
	});
	
	//匿名评论
	$("#niming").click(function(){
		sendData(thisEL);
		$("#longConfirmModal").modal("hide");
	});
	initLocate();
	
	//第三方qq登陆
	$("#qqthird").click(function(){
		//把当前页存入cookie,path cookie存储路径，expiress有效时间，单位天，sucue传输是否安全
		$.cookie("lastUrl",_ctx+"/atc/show/"+tid,{ path: "/", expiress: 7 ,sucue:true});
		window.location.href = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101401237&redirect_uri=http://www.lrshuai.top/user/qqredirect&state=lrs_blog";
	});
	mobileMenuOutsideClick();
    
})

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

function blackSkin(){
	if(!$("#markdownView").hasClass("editormd-preview-theme-dark")){
		$("#markdownView").addClass("editormd-preview-theme-dark");
		$.cookie("showskin","black")
	}
}
function whiteSkin(){
	if($("#markdownView").hasClass("editormd-preview-theme-dark")){
		$("#markdownView").removeClass("editormd-preview-theme-dark");
		$("#markdownView").addClass("editormd-preview-theme");
		$.cookie("showskin","white")
	}
}
//初始化皮肤
function cookieSkin(){
	 if($.cookie("showskin") != null){
		 var skin = $.cookie("showskin");
		 if(skin == 'white'){
			 whiteSkin();
		 }else{
			 blackSkin();
		 }
	  }
}
//更新文章的点赞数
function updatePraiseNum(aid,type){
	$.ajax({
		type:"post",
		url:_ctx+"/public/praise",
		dateType:"json",
		cache:false,
		data:{table_id:aid,table_type:type,time:new Date().getTime()},
		success:function(data){
			if(data.status == "failed"){
				alert(data.msg);
			}
		}
	})
}

//添加回复框
function addTextareabox(el){
	var parent_id = el.parent().parent().attr("pid");
	if(el.parent().parent().find(".replybody").length > 0){
		$(".replybody").remove();
	}else{
		var uid = el.parent().parent().attr("uid");
		$(".replybody").remove();
		var bodyStr = "<div class='replybody' tid='"+tid+"' uid='"+uid+"' pid='"+parent_id+"'><textarea cols='80' id='replytextarea' rows='50' placeholder='喷他......' ></textarea>"
		+"<a href='javascript:void(0)' class='emotion' title='添加表情'><i class='fa fa-smile-o'></i></a>"
		+"<a href='javascript:void(0)' class='btn btn-xs btn-info pull-right sendComment' >回复</a></div>";
		el.parent().parent().append(bodyStr).find(".sendComment").click(function(){
			
			var userId = $("#user_id").val();
			if(userId == "0"){
				thisEL = $(this);
				$("#longConfirmModal").modal("show");
				return false;
			}else{
				sendData($(this));
			}
		});
		//回复框里的表情初始化
		$('.emotion').qqFace({
			id : 'facebox', 
			assign:'replytextarea', 
			path:_ctx+'/face/arclist/'
		});
	}
}

//公共的发送数据的方法
function sendData(el){
	var content = el.prev().prev().val();
	if(content.length < 5){
		alert("不能评论少于5个字符");
		return false;
	}
	var browse = Client.Browse();
	var osName = Client.ClientOs();
	var uid = el.parent().attr("uid");
	var tid = el.parent().attr("tid");
	var pid = el.parent().attr("pid");
	var thisText = el.text();
	$.ajax({
		type:"post",
		url:_ctx+"/public/comment",
		cache:false,
		dataType:"json",
		data:{table_id:tid,reply_user_id:uid,parent_id:pid,content:content,browse_version:browse,os_name:osName,address:address,time:new Date().getTime()},
		success:function(data){
			if(data.status == "success"){
				task = setInterval("commentTask()",500);
				if(thisText == "评论"){
					var str = createCommentBox(data.data,totalfloor);
					if($(".blog-comment-list .comment-list-box").length < 1){
						$(".blog-comment-list").empty();
					}
					str=str.replace("回复","");
					//添加评论的
					$(".blog-comment-list").prepend(str);
					$("#comment").val('');
				}else{
					var replybox = createReplyCommentBox(data.data[0]);
					replybox=replybox.replace("回复","");
					replybox=replybox.replace("fa fa-comment-o","");
					//添加回复的
					el.parent().parent().parent().append(replybox);
					$(".replybody").remove();
				}
			}else{
				alert(data.msg);
			}
		}
	});
}
//弹出框任务setInterval，参数后面是多少时间执行一次
function commentTask(){
	$("#commentModal").modal('show');
	 if(task_count>0){
		 task_count--;
     }else if(task_count<=0){
    	 //清除任务
         window.clearInterval(task); 
         task_count = 2;
         $("#commentModal").modal('hide');
     }
}

//分页获取评论
function getCommentByPage(pageNo,pageSize){
	$.ajax({
		type:"get",
		url:_ctx+"/public/getComment",
		cache:false,
		dataType:"json",
		data:{page_no:pageNo,page_size:pageSize,table_id:tid},
		success:function(data){
			var arr = data.data;
			var commentList="";
			if(data.data.length < 1){
				commentList="<div id='commentpage' pageNo='0'><a>暂时没有评论，你要不评几句镇楼....</a></div>";
			}else{
				commentList = createCommentBox(arr,0);
				var page = data.page;
				console.log("page",page);
				totalfloor = page.totalResult;
				console.log("totalfloor",totalfloor);
				if(page.totalPage > page.currentPage){
					commentList = commentList+"<div id='commentpage' pageNo='"+Number(page.currentPage+1)+"' pageSize='"+page.showCount+"' ><a href='javascript:void(0)' >查看更多评论....</a></div>";
				}else{
					commentList = commentList+"<div id='commentpage' pageNo='0'><a >没有更多了....</a></div>";
				}
			}
			//先清空之前的分页提示
			$("#commentpage").remove();
			//添加评论列表
			$(".blog-comment-list").append(commentList).find(".addReplyBox").click(function(){
				addTextareabox($(this));
			});
			$("#commentpage").click(function(){
				var pageNo = $(this).attr("pageNo");
				if(pageNo != 0){
					getCommentByPage(pageNo,$(this).attr("pageSize"));
				}
			});
			
		}
	});
}

function htmlEncodeJQ (str) {  
    return $('<span/>').text(str).html();  
}
  
function htmlDecodeJQ (str) {  
    return $('<span/>').html(str).text();  
} 


//创建楼层的结构
function createCommentBox(arr,totalf){
	var commentList = "";
	for(var i=0;i<arr.length;i++){
		tmpFloor = floor;
		if(totalf > 0){
			totalfloor++;
			tmpFloor = totalfloor;
		}else{
			floor++;
		}
		var obj = arr[i];
		var commentString = "<div class='comment-list-box comment-box2'>"+"<header><img src='"+_ctx+obj.img+"'/></header>"
						+"<div class='comment-list-info'><h4>"+obj.name+"</h4>"
						+"<div class='comment-list-box-content'>"
						+"<p class='comment-time'><span><i class='fa fa-home blog-info'></i> "+tmpFloor+"楼</span><span><i class='fa fa-clock-o blog-info'></i> <small>"
						+obj.create_time+"</small></span> <span><i class='fa fa-map-marker blog-info'></i> "
						+"<small>"+obj.address+"</small></span></p><p class='comment-content'>"+obj.content+"</p></div>"
						+"<footer uid='"+obj.user_id+"' pid='"+obj.comment_id+"'><span>来自: <small>"+obj.os_name+"</small></span>  <span><i class='fa fa-globe green'></i>"
						+" <small>"+obj.browse_version+"</small></span><p class='pull-right'><a href='javascript:void(0)' class='addReplyBox'>回复</a></p>"
						+"</footer><div class='reply-comment-list'>";
		var replyArr = obj.replybody;
		if(replyArr.length > 0){
			for(var j=0;j<replyArr.length;j++){
				var replyObj = replyArr[j];
				commentString = commentString+createReplyCommentBox(replyObj);
			}
		}
		commentString = commentString+"</div></div></div>";
		commentList = commentList+commentString;
	}
	return commentList;
}

//创建回复体的结构
function createReplyCommentBox(replyObj){
	var reply="<div class='reply' uid='"+replyObj.user_id+"' pid='"+replyObj.parent_id+"'><a href='javascript:void(0)'>"+replyObj.name+"</a>:<a href='javascript:void(0)'>@"+replyObj.reply_user_name+"</a> <span>"
				+replyObj.content+"</span><p><small>"+replyObj.create_time+"</small> <a href='javascript:void(0)' class='addReplyBox'><i class='fa fa-comment-o'></i> 回复</a></p></div>";
	return reply;
}

//表情格式替换
function replace_em(str){
	str = str.replace(/\</g,'&lt;');
	str = str.replace(/\>/g,'&gt;');
	str = str.replace(/\n/g,'<br/>');
	str = str.replace(/\[em_([0-9]*)\]/g,"<img src='"+_ctx+"/face/arclist/$1.gif' border='0' />");
	return str;

}