var _ctx = $("meta[name='_ctx']").attr("content");
_ctx = _ctx.substr(0, _ctx.length - 1);

var address="";
var browse = Client.Browse();
var osName = Client.ClientOs();
var EL="";
//防止瞬间点击多次
var clickIndex=0;
$(function(){
	$("#lrs-blog-main-left").css("min-height",($(window).height()-80)+"px");
	//左侧点击
	mobileMenuOutsideClick();
	//初始化表情
	/*
	$('.emotion').qqFace({
		id : 'facebox', 
		assign:'leaveword', 
		path:_ctx+'/face/arclist/'	//表情存放的路径
	});
	*/
	
	//发送留言
	$(".sendLeaveWord").click(function(){
		var content = $("#leaveword").val();
//		content = replace_em(content);
		if(content != ""){
			var userId = $("#user_id").val();
			if(userId == "0"){
				$("#longConfirmModal").modal("show");
				return false;
			}else{
				sendLeaveWord(content);
			}
		}else{
			alert("不能发送空内容");
		}
	});
	//匿名评论
	$("#niming").click(function(){
		var content = $("#leaveword").val();
//		content = replace_em(content);
		sendLeaveWord(content);
		$("#longConfirmModal").modal("hide");
	});
	
	//第三方qq登陆
	$("#qqthird").click(function(){
		//把当前页存入cookie,path cookie存储路径，expiress有效时间，单位天，sucue传输是否安全
		$.cookie("lastUrl",_ctx+"/leaveword",{ path: "/", expiress: 1 ,sucue:true});
		console.log("lastUrl",$.cookie("lastUrl"));
		window.location.href = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101401237&redirect_uri=http://www.lrshuai.top/user/qqredirect&state=lrs_blog";
	});
	
	getLeaveWordBypage(1,5);
	initLocate();
});

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

function sendLeaveWord(content){
	$.ajax({
		type:"post",
		url:_ctx+"/public/leaveword",
		cache:false,
		dataType:"json",
		data:{content:content,address:address,browse_version:browse,os_name:osName,t:new Date().getTime()},
		success:function(data){
			$("#leaveword").val("");
			if(data.status == "success"){
				var leaver = addLeaveWord(data.data);
				$("#leaveWord-list").prepend(leaver);
				$(".fire").click(function(){
					var leavewordId = $(this).attr("lid");
					updateNum(leavewordId,"fire",EL);
				});
				$(".nice").click(function(){
					var leavewordId = $(this).attr("lid");
					updateNum(leavewordId,"nice",EL)
				});
				$(".tread").click(function(){
					var leavewordId = $(this).attr("lid");
					updateNum(leavewordId,"tread",$(this))
				});
			}else{
				alert(data.msg);
			}
			
		}
		
	});
}


//初始化留言列表
function getLeaveWordBypage(pageNo,pageSize){
	$.ajax({
		type:"get",
		url:_ctx+"/public/getLeaveWord",
		cache:false,
		dataType:"json",
		data:{page_no:pageNo,page_size:pageSize,t:new Date().getTime()},
		success:function(data){
			if(data.status == "success"){
				var leaver="";
				if(data.data.length < 1){
					leaver="<div id='leaveWordpage' pageNo='0'><a>暂时没有留言，好冷清....</a></div>";
				}else{
					leaver = addLeaveWord(data.data);
					var page = data.page;
					if(page.totalPage > page.currentPage){
						leaver = leaver+"<div id='leaveWordpage' pageNo='"+Number(page.currentPage+1)+"' pageSize='"+page.showCount+"' ><a href='javascript:void(0)' >查看更多留言....</a></div>";
					}else{
						leaver = leaver+"<div id='leaveWordpage' pageNo='0'><a >没有更多了....</a></div>";
					}
					$("#leaveNum").text(page.totalResult);
				}
				//先清空之前的分页提示
				$("#leaveWordpage").remove();
				$("#leaveWord-list").append(leaver);
				
				$("#leaveWordpage").click(function(){
					var pageNo = $(this).attr("pageNo");
					if(pageNo != 0){
						getLeaveWordBypage(pageNo,$(this).attr("pageSize"));
					}
				});
				$(".fire").click(function(e){
					var leavewordId = $(this).attr("lid");
					updateNum(leavewordId,"fire",$(this));
					clickAnimation(e,"+ <i class='glyphicon glyphicon-fire'></i>");
				});
				$(".nice").click(function(e){
					var leavewordId = $(this).attr("lid");
					updateNum(leavewordId,"nice",$(this))
					clickAnimation(e,"+ <i class='fa fa-ra'></i>");
				});
				$(".tread").click(function(e){
					var leavewordId = $(this).attr("lid");
					updateNum(leavewordId,"tread",$(this));
					clickAnimation(e,"+ <i class='fa fa-yelp'></i>");
				});
			}else{
				alert(data.msg);
			}
			
		}
		
	});
}

//点击动画
function clickAnimation(e,text){
	e = e || window.event;
	xponit = e.pageX || e.clientX + document.body.scroolLeft;
	yponit = e.pageY || e.clientY + document.body.scrollTop;
	var elment = "<div class='pointanim' style='clear:both;position:absolute;top:"+(yponit-30)+"px;left:"+(xponit-30)+"px;color:red;text-align:center;z-index: 2147483647;font-size:2em;'>"+text+"</div>";
	$('body').append(elment);
	$(".pointanim").animate({opacity:'0.5',top:'0'},2000,function(){$(".pointanim").remove()})
}

function updateNum(id,type,el){
	++clickIndex;
	if(clickIndex > 1){
		return false;
	}
	var num = el.find("small").text();
	$.ajax({
		type:"post",
		url:_ctx+"/public/editLeaveWord",
		cache:false,
		dataType:"json",
		data:{leaveword_id:id,type:type,t:new Date().getTime()},
		success:function(data){
			if(data.status == "success"){
				num = Number(Number(num)+1);
				el.find("small").text(num);
			}else{
				alert(data.msg);
			}
			clickIndex=0;
		}
		
	});
}

//创建楼层的结构
function addLeaveWord(arr){
	var commentList = "";
	for(var i=0;i<arr.length;i++){
		var obj = arr[i];
		var commentString = "<div class='comment-list-box comment-box2'>"+"<header><img src='"+_ctx+obj.img+"'/></header>"
						+"<div class='comment-list-info'><h4>"+obj.name+"</h4>"
						+"<div class='comment-list-box-content'>"
						+"<p class='comment-time'><span><i class='fa fa-clock-o blog-info'></i> <small>"
						+obj.create_time+"</small></span> <span><i class='fa fa-map-marker blog-info'></i> "
						+"<small>"+obj.address+"</small></span></p><p class='comment-content'>"+obj.content+"</p></div>"
						+"<footer><span>来自: <small>"+obj.os_name+"</small></span>  <span><i class='fa fa-globe green'></i>"
						+" <small>"+obj.browse_version+"</small></span><p class='pull-right'>"
						+"<span><a href='javascript:void(0)' class='fire' lid='"+obj.leaveword_id+"'><i class='glyphicon glyphicon-fire red'></i> 加火(<small>"+obj.fire_num+"</small>)</a></span> <span><a href='javascript:void(0)' class='nice' lid='"+obj.leaveword_id+"'><i class='fa fa-ra blog-info'></i> 很美(<small>"+obj.nice_num+"</small>)</a></span> <span><a href='javascript:void(0)' class='tread' lid='"+obj.leaveword_id+"'><i class='fa fa-yelp green'></i> 踩它(<small>"+obj.tread_num+"</small>)</a></span></p>"
						+"</footer>";
		commentString = commentString+"</div></div>";
		commentList = commentList+commentString;
	}
	return commentList;
}

//表情格式替换
function replace_em(str){
	str = str.replace(/\</g,'&lt;');
	str = str.replace(/\>/g,'&gt;');
	str = str.replace(/\n/g,'<br/>');
	str = str.replace(/\[em_([0-9]*)\]/g,"<img src='"+_ctx+"/face/arclist/$1.gif' border='0' />");
	return str;

}