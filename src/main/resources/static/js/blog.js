var isExist = 0;
$(function(){
	$("#fh5co-main-menu ul li:eq(1)").addClass("fh5co-active");
	$(".blog-top").hide();
	$(".snow-canvas").snow();
	$("#wechatCode").click(function(){
		$("#myWechatCode").modal("show");
	});
	
	//回到顶部动画
	$(window).scroll(function(){
		var scrollH  = $(window).scrollTop();
		if(scrollH > $(window).height()){
			if($(".blog-top").find(".topText").length < 1){
				$(".blog-top").append("<label class='topText'>回到顶部</label>");
			}
			$(".blog-top").css({"bottom":"30px"});
			$(".blog-top").show();
		}else{
			$(".blog-top").hide();
		}
	});
	$(".blog-top").click(function(){
		var scrollH = $(window).scrollTop();
		$(".blog-top").stop().animate({"bottom":scrollH+"px"},1000,function(){
			$(".imgtop").remove();
			$(".topText").remove();
		});
		$('html,body').stop().animate({scrollTop: '0px'},1000);
	});
	$(".blog-top").mouseover(function(){
		$(".topText").remove();
		if($(this).find(".imgtop").length < 1){
			$(".blog-top").append("<img class='imgtop' src='/images/top.png'/>");
		}
	}).mouseout(function(){
		$(".imgtop").remove();
		if($(this).find(".topText").length < 1){
			$(".blog-top").append("<label class='topText'>回到顶部</label>");
		}
	});
	//动画结束
	
 isExist=$.cookie("isExist");
 $("#lrs-blog-main-left").css("min-height",$(window).height()+"px");
 $("[data-toggle='tooltip']").tooltip();
 $('.list-group li').each(function() {
     $(this).click(function() {
         location.href = _ctx+"/blog/month/"+ $(this).attr('id');
	})
 });
 //搜索
 $("#searchIcon").click(function(){
	 search();
 });
 
 //显示更多
$("#blog-showMore").click(function(){
	var url = $(this).attr("url");
	var pageNo = $(this).attr("page");
	showMore(url,pageNo);
});

 //构建音乐播放器,不存在则初始化
console.log("isExist",isExist);
if(Number(isExist) < 1 || typeof(isExist) == "undefined"){
	ap = initMusicList();
}else{
	//存在则隐藏
	$("#playerbox").hide();
}
 
 $(".iconb").click(function(){
	 if($("#playerbox").css("margin-left") == "0px"){
		 $("#playerbox").stop().animate({"margin-left":"-470px"})
	 }else{
		 $("#playerbox").stop().animate({"margin-left":"0px"})
		 }
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

//初始化音乐播放器
 function initMusicList(){
	var musics=new Array();
	 $.ajax({
		 type:"get",
		 url:_ctx+"/getMusicList",
		 cache:false,
		 dataType:"json",
		 async:false,
		 data:{_:new Date().getTime()},
		 success:function(data){
			 for(var i=0;i<data.length;i++){
				 var music = createMusic(data[i].author,data[i].title,data[i].url,data[i].pic,data[i].lrc);
				 musics.push(music);
			 }
		 }
	 });
	 var ap = new APlayer({
		    element: document.getElementById('blogPlayer'),
		    narrow: false,
		    autoplay: true,
		    showlrc: 3,
		    mutex: true,
		    theme: '#fff',
		    preload: 'metadata',
		    mode: 'random',
		    listmaxheight: '100px',
	   	 	music: musics
		});
	 //存入cookie
	 isExist=1;
	 $.cookie("isExist",isExist);
	 return ap;
 }	 
 //搜索
 function search(){
	 var keyword = $("#keyword").val();
	 var url=_ctx+"/search?keyword="+keyword;
	 if(keyword != ""){
		 window.location.href=url; 
	 }
 }
 
 
//加载更多
function showMore(url,pageNo){
	 var data={};
	 if("/blog/search" == url){
		 var keyword = $("#keyword").val();
		 data={keyword:keyword,time:new Date().getTime()}
	 }else{
		 data={time:new Date().getTime()};
	 }
	 $.ajax({
		 type:"get",
		 url:_ctx+url+"/"+pageNo,
		 cache:false,
		 dataType:"json",
		 data:data,
		 success:function(data){
			var atcData = data.data;
			var dataStr = "";
			if(atcData.length > 0){
				for(var i=0;i<atcData.length;i++){
					var obj=atcData[i];
					var subNum = 0;
					if(obj.text.length > 150){
						subNum = 150;
					}else{
						subNum = obj.text.length;
					}
					dataStr = dataStr+"<div class='blog-article blog-article-top'><div class='author'><a href='javascript:void(0)' target='_blank'><img src='"
									+_ctx+obj.img+"'></a><a target='_blank' href='"+_ctx+"/atc/show/"+obj.article_id+"'  class='title'>"+obj.title+"</a>"
									+"</div><div class='content'><p>"+obj.text.substring(0,subNum)+"</p></div><div class='footer'><div class='footer-left'>"
									+"<span><i class='glyphicon glyphicon-eye-open blog-info icon-sm'></i> <a target='_blank' href='"+_ctx+"/atc/show/"+obj.article_id+"'>阅读</a>(<span>"+obj.browse_num+"</span>)</span>"
									+"<span><i class='glyphicon glyphicon-comment blog-success icon-sm'></i> <a target='_blank' href='"+_ctx+"/atc/show/"+obj.article_id+"#comment'>评论</a>(<span>"+obj.comment_num+"</span>)</span>"
									+"<span><i class='glyphicon glyphicon glyphicon-thumbs-up icon-sm red'></i> <a target='_blank' href='"+_ctx+"/atc/show/"+obj.article_id+"#praise'>点赞</a>(<span>"+obj.praise_num+"</span>)</span>"
									+"</div><div class='footer-right'>";
					for(var j=0;j<obj.labels.length;j++){
						var label = obj.labels[j];
						dataStr = dataStr+"<span ><label class='label label-"+label.label_class+"'>"+label.label_name+"</label></span>";
					}
					dataStr = dataStr+"<span>"+obj.create_time+"</span></div></div></div>";
				}
			}
			var nextPage = data.page;
			var pageElement="";
			if(nextPage.totalPage > pageNo){
				pageElement="<div class='page-box'><a id='blog-showMore' href='javascript:void(0)' url='"+data.page_url+"' page='"+(++pageNo)+"'  class='page'>显示更多 <i class='fa fa-plus-square-o'></i></a></div>";
			}else{
				pageElement="<div class='page-box'><a href='javascript:void(0)' class='page'>没有更多了 <i class='fa fa-minus-square-o'></i></a></div>";
				
			}
			dataStr=dataStr+pageElement;
			//去除分页box
			$(".page-box").remove();
			$("#lrs-blog-main-left").append(dataStr).find("a#blog-showMore").click(function(){
				var url = $(this).attr("url");
				var pageNo = $(this).attr("page");
				showMore(url,pageNo);
			});
			
		 }
	 });
}
//音乐对象
function createMusic(author,title,url,pic,lrc){
	var obj = new Object();
	obj.author=author;
	obj.title=title;
	obj.url=url;
	obj.pic=pic;
	obj.lrc=lrc;
	return obj;
}

//关闭窗口时调用
function myunload(){
	$.cookie("isExist",0);
}