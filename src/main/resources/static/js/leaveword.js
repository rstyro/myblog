var _ctx = $("meta[name='_ctx']").attr("content");
_ctx = _ctx.substr(0, _ctx.length - 1);

var userName="匿名";
var replyUserName="帅大叔";
var tableId="0";
var address="地球";
var browse_version=Client.Browse();
var os_name=Client.ClientOs();
var replyName="";
var floorNum = 1;
var newfloorNum = 1;
$(function(){
	$("#fh5co-main-menu ul li:eq(2)").addClass("fh5co-active");
	$("#wechatCode").click(function(){
		$("#myWechatCode").modal("show");
	});
	$(".smile").emoji({
		content_el:"#content",
        list: [{name:"QQ表情",code:"qq_",path:_ctx+"/comment/face/emoji1/",suffix:".gif",max_number:75},{name:"emoji表情",code:"em_",path:_ctx+"/comment/face/emoji2/",suffix:".png",max_number:52}]
	});
	
	$("#lrs-blog-main-left").css("min-height",($(window).height()-80)+"px");
	//左侧点击
	mobileMenuOutsideClick();
	
	//第三方qq登陆
	$("#qqthird").click(function(){
		//把当前页存入cookie,path cookie存储路径，expiress有效时间，单位天，sucue传输是否安全
		$.cookie("lastUrl",_ctx+"/leaveword",{ path: "/", expiress: 1 ,sucue:true});
		window.location.href = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101401237&redirect_uri=http://www.lrshuai.top/user/qqredirect&state=lrs_blog";
	});
	
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
				obj.table_id="1";
				obj.user_id="1";
				obj.userName= userName;
				obj.userPath=_ctx+"/comment/images/user1.png";
				obj.replyUserName="帅大叔";
				obj.content=content;
				obj.reply_user_id="";
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
	
	initLocate();
})

//加载留言
function reloadLeaveword(floorNum,page_no,page_size){
	$.ajax({
		type:"get",
		url:_ctx+"/public/getTalk",
		dataType:"json",
		data:{table_type:"leaveword",page_no:page_no,page_size:page_size},
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
		data:{table_name:"leaveword",userName:$("[name='nick_name']").val(),email:$("[name='email']").val(),userPath:body.userPath,content:body.content,address:body.address,browse_version:body.browse_version,os_name:body.os_name},
		cache:false,
		async: false,
		success:function(data){
			console.log("data",data);
			if(data.status != "success"){
				alert(data.msg);
			}
			result = data;
		}
		
	});
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
		data:{table_name:"leaveword",userName:$("[name='nick_name']").val(),email:$("[name='email']").val(),userPath:body.userPath,content:body.content,address:address,browse_version:browse_version,os_name:os_name,parent_id:body.parent_id,reply_user_id:body.reply_user_id,t:new Date().getTime()},
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
			data:{table_type:"leaveword",table_id:body.comment_id,t:new Date().getTime()},
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
