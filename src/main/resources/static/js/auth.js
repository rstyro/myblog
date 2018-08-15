var _ctx = $("meta[name='_ctx']").attr("content");
_ctx = _ctx.substr(0, _ctx.length - 1);
$(document).ready(function() {
	changeCode();
	$("#authCode").bind("click", changeCode);
	$("#applyRmBlackList").click(function(){
		var code = $("#auth_code").val();
		$.ajax({
			type:"post",
			url:_ctx+"/public/delBlack",
			cache:false,
			dataType:"json",
			data:{"auth_code":code,t_:new Date().getTime()},
			success:function(data){
				if(data.status == "success"){
					window.location.href=_ctx+"/blog";
				}else{
					alert(data.msg);
				}
			}
		});
	});
});

function changeCode() {
	$("#authCode").attr("src", _ctx+"/error/authCode?t=" + genTimestamp());
}
function genTimestamp() {
	var time = new Date();
	return time.getTime();
}