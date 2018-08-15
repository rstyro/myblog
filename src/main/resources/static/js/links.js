var _ctx = $("meta[name='_ctx']").attr("content");
_ctx = _ctx.substr(0, _ctx.length - 1);

$(function(){
	$(".main").css("min-height",($(window).height()-80)+"px");
	$("[data-toggle='tooltip']").tooltip();
	$("#fh5co-main-menu ul li:eq(4)").addClass("fh5co-active");
	$("#wechatCode").click(function(){
		$("#myWechatCode").modal("show");
	});
	
	$("#addLinks").click(function(){
		var linkName = $("#link_name").val();
		var link = $("#link").val();
		var desc = $("#link_desc").val();
		if(linkName != "" && link != "" && desc != ""){
			$.ajax({
				type:"post",
				url:_ctx+"/public/saveLink",
				cache:false,
				dataType:"json",
				data:{link_name:linkName,link:link,description:desc,t_:new Date().getTime()},
				success:function(data){
					if(data.status == "success"){
						alert("提交成功");
						$("#link_name").val("");
						$("#link").val("");
						$("#link_desc").val("");
					}else{
						alert(data.msg);
					}
				}
			});
		}else{
			alert("3个信息都不能为空");
		}
		return false;
	});
	//左侧点击
	mobileMenuOutsideClick();
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