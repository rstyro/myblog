//初始化定位
function initLocate(){
	var geolocation =new qq.maps.Geolocation("IB2BZ-2XSKO-XWUWX-SAGLU-LDL2K-UQBFH", "blogkey",{timeout: 8000});
	//定位
	geolocation.getLocation(preciseSuccess, preciseErr);
}

//精准回调
function preciseSuccess(data){
	console.log("preciseL",data);
	if(data.province == "" || data.city == ""){
		geolocation.getIpLocation(roughSuccess,roughErr,{timeout: 8000});
	}else{
		address = data.province +data.city;
		if(typeof(data.addr) != "undefined"){
			address = address+data.addr;
		}
	}
}
//粗糙回调
function roughSuccess(data){
	address = data.province +data.city;
}
//定位错误回调
function preciseErr() {
	console.log("precise is error");
	geolocation.getIpLocation(roughSuccess,roughErr,{timeout: 8000});
}
//定位错误回调
function roughErr() {
	address="地球";
}


