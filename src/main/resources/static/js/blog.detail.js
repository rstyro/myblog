var tid = $("#tid").val();
var replyUid = $("#author_id").val();
var task=0;
var task_count=2;//执行两次，每500ms 执行一次 
var userName="匿名";
var replyUserName="帅大叔";
var tableId=tid;
var address="地球";
var browse_version=Client.Browse();
var os_name=Client.ClientOs();
var replyName="";
var floorNum = 1;
var newfloorNum = 1;

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
	
	$(".smile").emoji({
		content_el:"#content",
        list: [{name:"QQ表情",code:"qq_",path:_ctx+"/comment/face/emoji1/",suffix:".gif",max_number:75},{name:"emoji表情",code:"em_",path:_ctx+"/comment/face/emoji2/",suffix:".png",max_number:52}]
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
	
	
	//第三方qq登陆
	$("#qqthird").click(function(){
		//把当前页存入cookie,path cookie存储路径，expiress有效时间，单位天，sucue传输是否安全
		$.cookie("lastUrl",_ctx+"/atc/show/"+tid,{ path: "/", expiress: 7 ,sucue:true});
		window.location.href = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101401237&redirect_uri=http://www.lrshuai.top/user/qqredirect&state=lrs_blog";
	});
	mobileMenuOutsideClick();
	initLocate();
	
	$(".btn-send").click(function(){
		var flag = true;
		var content= $("#content").val().trim();
		if(content == ""){
			alert("不能发送空的内容");
			return false;
		}else{
			var userName = $("[name='nick_name']").val().trim();
			if(userName == ""){
				flag = confirm("你确定匿名评论吗，最好加个昵称，好让博主知道你叫啥！！！");
			}
			if(flag){
				var obj = new Object();
				obj.comment_id="";
				obj.parent_id="0";
				obj.table_id=tableId;
				obj.user_id="1";
				obj.userName= userName;
				obj.userPath=_ctx+"/comment/images/user1.png";
				obj.replyUserName="帅大叔";
				obj.content=content;
				obj.reply_user_id=replyUid;
				obj.praise_num=0;
				obj.address=address;
				obj.browse_version=address;
				obj.os_name=os_name;
				obj.create_time="2017-12-20 12:00:10";
				newfloorNum = $(".comment-list").comment({obj:obj,direction:"before",floorNum:newfloorNum,comment_method:"commentAction",reply_method:"replyAction",praise_method:"praiseAction",userName:obj.userName});
				$("#content").val("");
			}
		}
	});
	floorNum = reloadLeaveword(floorNum,1,5);
	
	//分页点击
	$("#pageText").click(function(){
		var flag = $(this).attr("flag");
		var pageNo = $(this).attr("on");
		if(flag == "true"){
			floorNum = reloadLeaveword(floorNum,++pageNo,5)
		}
	});
})

//加载评论
function reloadLeaveword(floorNum,page_no,page_size){
	$.ajax({
		type:"get",
		url:_ctx+"/public/getTalk",
		dataType:"json",
		data:{table_type:"comment",table_id:tableId,page_no:page_no,page_size:page_size},
		cache:false,
		async: false,
		success:function(data){
			if(data.status != "success"){
				alert(data.msg);
			}else{
				floorNum = $(".comment-list").initComment({data:data.data,floorNum:floorNum,userName:$("[name='nick_name']").val(),reply_method:"replyAction",praise_method:"praiseAction"});
				$("#leaveNum").text(data.page.totalResult);
				newfloorNum = data.page.totalResult + 1;
				reloadPage(data.page);
			}
		}
		
	});
	return floorNum;
}

function reloadPage(page){
	if(page.totalPage <= page.currentPage){
		$("#pageText").text("我是有底线的...");
		$("#pageText").attr("flag",false);
	}else if(page.totalPage > page.currentPage){
		$("#pageText").text("加载更多...");
		$("#pageText").attr("flag",true);
	}
	$("#pageText").attr("on",page.currentPage);
}


function commentAction(){
	var body = $(".comment-list").getCommentBody();
	var result = "";
	$.ajax({
		type:"post",
		url:_ctx+"/public/talk",
		dataType:"json",
		data:{table_name:"comment",reply_user_id:body.reply_user_id,table_id:tableId,userName:$("[name='nick_name']").val(),email:$("[name='email']").val(),userPath:body.userPath,content:body.content,address:body.address,browse_version:body.browse_version,os_name:body.os_name},
		cache:false,
		async: false,
		success:function(data){
			if(data.status != "success"){
				alert(data.msg);
			}
			result = data;
		}
		
	});
	task = setInterval("commentTask()",500);
	return result;
}
	
function replyAction(){
	var body = $(".comment-list").getCommentBody();
	if(body.userPath == "" || typeof(body.userPath) == "undefined"){
		body.userPath=_ctx+"/images/niming.png";
	}
	var result = "";
	$.ajax({
		type:"post",
		url:_ctx+"/public/talk",
		dataType:"json",
		data:{table_name:"comment",table_id:tableId,userName:$("[name='nick_name']").val(),email:$("[name='email']").val(),userPath:body.userPath,content:body.content,address:address,browse_version:browse_version,os_name:os_name,parent_id:body.parent_id,reply_user_id:body.reply_user_id,t:new Date().getTime()},
		cache:false,
		async: false,
		success:function(data){
			if(data.status != "success"){
				alert(data.msg);
			}
			result = data;
		}
		
	});
	return result;
}

function praiseAction(){
	var result = "";
	var body = $(".comment-list").getCommentBody();
	if(body.praise_flag == 1){
		var obj = new Object();
		obj.status="outnumber";
		return obj;
	}else{
		$.ajax({
			type:"post",
			url:_ctx+"/public/praise",
			dataType:"json",
			data:{table_type:"comment",table_id:body.comment_id,t:new Date().getTime()},
			cache:false,
			async: false,
			success:function(data){
				if(data.status != "success"){
					alert(data.msg);
				}
				result = data;
			}
			
		});
		return result;
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

