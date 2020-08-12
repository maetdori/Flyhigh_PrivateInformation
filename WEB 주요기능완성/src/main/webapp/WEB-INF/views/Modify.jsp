<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="java.util.List"%>
    <%@page import="com.web.domain.CertVO" %>
    <%@page import="com.web.domain.PersonalInfoVO" %>
    <%@page import="com.web.domain.SiteVO" %>
    <% PersonalInfoVO pv = (PersonalInfoVO) request.getAttribute("getPv"); %>
    <% CertVO cert = (CertVO) request.getAttribute("getCert");%>
    <% List<SiteVO> siteList = (List<SiteVO>) request.getAttribute("getSiteList");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<title>Modify</title>
	<link rel="stylesheet" href="/webjars/bootstrap/4.1.0/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="/css/Home.css">
</head>

<body class="my-Home-page">
	<section class="h-100">
		<div class="container h-100">
			<div class="row justify-content-md-center h-100">
				<div class="card-wrapper">
					<div class="brand">
						<img src="/img/logo.png">
					</div>
					<div class="card fat">
						<div class="card-body">
							<h4 class="card-title text-center text-dark">
							개인정보 수정
							<p></p>
							</h4>
							<form method="get" name="modifyForm" onsubmit="modify()" target="dummy">
								<div class="form-group has-feedback" id="certs">
									<label class="control-label" for="user_type"><strong>이용자 타입</strong></label>
									<input type="radio" name="user_type" value="gein" checked="checked"/> 개인
									<input type="radio" name="user_type" value="bupin"  /> 법인
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_name"><strong>사용자명(id) (필수)</strong></label>
									<input class="form-control" type="text" id="co_name" name="co_name" value = "<%=cert.getCo_name() == null ? "" : cert.getCo_name()%>" readonly/>
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_kname"><strong>성명/법인명(한글) (필수)</strong></label>
									<input class="form-control" type="text" id="co_kname" name="co_kname" placeholder="성명(한글)"
									onKeyup="this.value=this.value.replace(/[^가-힣 ㄱ-ㅎ ㅏ-ㅣ | \x20]/g,'');" value = "<%=pv.getCo_kname() == null ? "" : pv.getCo_kname()%>" required autofocus/>
								</div>
								
								<div class="form-group has-feedback">
									<label class="control-label" for="co_ename"><strong>성명/법인명(영문)</strong></label>
									<input class="form-control" type="text" id="co_ename" name="co_ename" value = "<%=pv.getCo_ename() == null ? "" : pv.getCo_ename()%>" placeholder="영문 이름"
									onKeyup="this.value=this.value.replace(/[^a-z A-Z | \x20]/g,'');" />
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_rrn1"><strong>주민번호 (개인-필수)</strong></label>
										<input class="form-control" type="text" id="co_rrn1" name="co_rrn1" placeholder="앞자리 6자" style="float:left ; width:40%;" 
										maxlength="6" onKeyup="this.value=this.value.replace(/[^0-9 | \x20]/g,'');" value = "<%=pv.getCo_rrn1() == null ? "" : pv.getCo_rrn1()%>" required/>
										<!--  주민번호 뒷자리는 일단 안받는걸로
										<div style="float:left ;" >&nbsp;-&nbsp;</div>
										<input class="form-control" type="text" id="co_rrn2" name="co_rrn2" placeholder="뒷자리 7자" style="float:left; width:40%;"/>
										-->
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_saupja_num"><strong>사업자 번호 (법인-필수)</strong></label>
									<input class="form-control" type="text" id="co_saupja_num" name="co_saupja_num" placeholder="사업자 번호 10자리" 
									maxlength="10" onKeyup="this.value=this.value.replace(/[^0-9 | \x20]/g,'');" value = "<%=pv.getCo_saupja_num() == null ? "" : pv.getCo_saupja_num()%>"/>
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_tel"><strong>전화번호</strong></label>
									<input class="form-control" type="text" id="co_tel" name="co_tel" placeholder="-없이 기재"
									maxlength="12"  onKeyup="this.value=this.value.replace(/[^0-9 | \x20]/g,'');" value = "<%=pv.getCo_tel() == null ? "" : pv.getCo_tel()%>" />
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_addr"><strong>주소</strong></label>
									<button type="button" class="btn btn-warning" onclick="goPopup()">주소검색</button>
									<input type="text" id="co_addr" name="co_addr" class="form-control" placeholder="비워둘 경우 앱에서 자동으로 입력"
									 value = "<%=(pv.getCo_addr1() == null ? "" : pv.getCo_addr1() ) + " " + (pv.getCo_addr2() == null ? "" : pv.getCo_addr2()) + " " + (pv.getCo_addr3() == null ? "" : pv.getCo_addr3())%>" readonly="true"/>
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_relation"><strong>관계 (개인-필수)</strong></label>
									<select id="co_relation" name="co_relation" required>
									    <option value="">===선택===</option>
									    <option value="조부">조부</option>
									    <option value="조모">조모</option>
									    <option value="부">부</option>
									    <option value="모">모</option>
									    <option value="형제">형제</option>
									    <option value="자녀">자녀</option>
									    <option value="배우자">배우자</option>
									</select>
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_relation_name"><strong>관계자 이름 (개인-필수)</strong></label>
									<input class="form-control" type="text" id="co_relation_name" name="co_relation_name" placeholder="관계자 이름"
									value = "<%=pv.getCo_relation_name() == null ? "" : pv.getCo_relation_name()%>" required />
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_house_hold"><strong>호주 이름 (개인-필수)</strong></label>
									<input class="form-control" type="text" id="co_house_hold" name="co_house_hold" placeholder="호주 이름"
									value = "<%=pv.getCo_house_hold() == null ? "" : pv.getCo_house_hold()%>" required/>
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_hojuk_name"><strong>호적상 이름</strong></label>
									<input class="form-control" type="text" id="co_hojuk_name" name="co_hojuk_name" placeholder="주민등록증의 이름과 같다면 비워두세요"
									value = "<%=pv.getCo_hojuk_name() == null ? "" : pv.getCo_hojuk_name()%>" onKeyup="this.value=this.value.replace(/[^가-힣 ㄱ-ㅎ ㅏ-ㅣ | \x20]/g,'');"/>
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_car"><strong>차량 번호</strong></label>
									<input class="form-control" type="text" id="co_car" name="co_car" placeholder="띄어쓰기 없이 입력" 
									value = "<%=pv.getCo_car() == null ? "" : pv.getCo_car()%>" onKeyup="this.value=this.value.replace(/[\x20]/g,'');"/>
								</div>
								
								<div class="form-group has-feedback">
									<label class="control-label" for="co_cert_pw"><strong>인증서 비밀번호</strong></label>
									<input class="form-control" type="text" id="co_cert_pw" name="co_cert_pw" 
									value = "<%=cert.getCo_cert_pw() == null ? "" : cert.getCo_cert_pw() %>" placeholder="인증서 비밀번호" required/>
								</div>
								<div class="form-group has-feedback" id="certs">
									<label class="control-label" for="co_cert_der"><strong>인증서</strong></label>
									<input type="radio" name="cert_type" value="der/key" checked="checked"/> der/key
									<input type="radio" name="cert_type" value="pfx"  /> pfx
									<div>
										<label class="control-label" for="co_cert_der"><strong>der</strong></label>
										<input class="form-control" type="file" id="co_cert_der" name="co_cert_der" accept=".der"/>
										<label class="control-label" for="co_cert_key"><strong>key</strong></label>
										<input class="form-control" type="file" id="co_cert_key" name="co_cert_key" accept=".key"/></div></div>
								<div class="form-group has-feedback">
									<label class="control-label" for="account"><strong>계정</strong></label>
								<div id ="sites"><%int i = 0;for(SiteVO site : siteList) { %><div>
										<input class="form-control" type="text" name="co_domain" value="<%=site.getCo_domain() == null ? "" : site.getCo_domain() %>" placeholder="도메인" />
										<input class="form-control" type="text" name="co_id" value="<%=site.getCo_id() == null ? "" : site.getCo_id() %>" placeholder="아이디"/>
										<input class="form-control" type="text" name="co_pw" value="<%=site.getCo_pw() == null ? "" : site.getCo_pw() %>" placeholder="패스워드"/>
									</div><%i++;}%></div>
									<input type ="button" value="+"  onclick="addSite()" style="WIDTH: 30pt; margin-bottom: 10px"/>
								<div class="form-group has-feedback">
										<input type="submit" value="수정" class="btn btn-info btn-block"/>
								</div>
							</form>
							<iframe name='dummy' width='0' height='0' frameborder='0'></iframe>
						</div>
					</div>
					<div class="footer">
						Copyright © <a href="https://www.flyhigh-x.com/" class="badge badge-info">FLYHIGH</a> 2020
					</div>
				</div>
			</div>
		</div>
	</section>
	<script type="text/javascript">
		var addr1 = "<%= pv.getCo_addr1() == null ? "" : pv.getCo_addr1() %>";
		var addr2 = "<%= pv.getCo_addr2() == null ? "" : pv.getCo_addr2() %>";
		var addr3 = "<%= pv.getCo_addr3() == null ? "" : pv.getCo_addr3() %>";
		
		function goPopup(){
			// 주소검색을 수행할 팝업 페이지를 호출합니다.
			// 호출된 페이지(jusopopup.jsp)에서 실제 주소검색URL(http://www.juso.go.kr/addrlink/addrLinkUrl.do)를 호출하게 됩니다.
			var pop = window.open("/popup/jusoPopup.jsp","pop","width=570,height=420, scrollbars=yes, resizable=yes"); 
			
			// 모바일 웹인 경우, 호출된 페이지(jusopopup.jsp)에서 실제 주소검색URL(http://www.juso.go.kr/addrlink/addrMobileLinkUrl.do)를 호출하게 됩니다.
		    //var pop = window.open("/popup/jusoPopup.jsp","pop","scrollbars=yes, resizable=yes"); 
		}
		
		function jusoCallBack(roadFullAddr, siNm, sggNm, rn , buldMnnm, buldSlno){
			// 팝업페이지에서 주소입력한 정보를 받아서, 현 페이지에 정보를 등록합니다.	
			document.getElementById("co_addr").value = roadFullAddr;	
			addr1 = siNm;
			addr2 = sggNm;
			if(buldSlno == 0)
				addr3 = rn + " " + buldMnnm;
			else
				addr3 = rn + " " + buldMnnm + "-" + buldSlno;
			console.log(addr1 + " " + addr2 + " " + addr3)
		}
		
		function numberOnly(e) {
		    if(!((e.keyCode > 95 && e.keyCode < 106)
		      || (e.keyCode > 47 && e.keyCode < 58) 
		      || e.keyCode == 8)) {
		        return false;
		    }
		}
		
		var gein = document.getElementsByName("user_type")[0];
		gein.addEventListener('change',function () {
			console.log("gein!");
			document.getElementById("co_rrn1").required = true;
			document.getElementById("co_relation").required = true;
			document.getElementById("co_relation_name").required = true;
			document.getElementById("co_house_hold").required = true;
			
			document.getElementById("co_saupja_num").required = false;
		});
		
		var bupin = document.getElementsByName("user_type")[1];
		function bupinCheckCallback() {
			console.log("bupin!");
			document.getElementById("co_rrn1").required = false;
			document.getElementById("co_relation").required = false;
			document.getElementById("co_relation_name").required = false;
			document.getElementById("co_house_hold").required = false;
			
			document.getElementById("co_saupja_num").required = true;
		}
		bupin.addEventListener('change',bupinCheckCallback);
		
		var select = document.getElementById("co_relation");
		select.addEventListener('change',function () {
			console.log("change");
			if(select.value == "") {
				document.getElementById("co_relation_name").value = "";
				document.getElementById("co_relation_name").readOnly = true;
			}
			else
				document.getElementById("co_relation_name").readOnly = false;
		});
		
	
		function addButton(button) {
			button.addEventListener('click',function() {deleteSite(button)});
			console.log(button);
		}
		
		window.onload = function () {
			//사이트 목록 추가
			var sites = document.getElementById("sites");
			var childs = sites.childNodes;
			console.log(sites.childElementCount);
			buttons = new Array();
			for(var i = 0; i < childs.length ;i++) {
				console.log(childs[i]);
				var button = document.createElement("input");
				var border= document.createElement("hr");
				
				button.setAttribute('type',"button");
				button.setAttribute('value',"-");
				button.setAttribute('style',"WIDTH: 30pt;");
				addButton(button);
				
				childs[i].appendChild(button);
				childs[i].appendChild(border);
			}
			
			// 개인/법인체크,required속성 변경
			if(<%=pv.isCo_corp()%>) {
				bupin.checked = true;
				bupinCheckCallback();
			}
			
			// relation select
			var opts = select.options;
			console.log("relation : <%=pv.getCo_relation()%>");
			  for (var opt, j = 0; opt = opts[j]; j++) {
				  console.log("opt :" + opt.value);
			    if (opt.value == "<%=pv.getCo_relation()%>") {
			      select.selectedIndex = j;
			      break;
			    }
			  }
		};
		var isDer = true;
		var rad = document.getElementsByName("cert_type");
		console.log(rad);
		
		/*
		일단 pfx는 못올리는 걸로 해둠
		*/
		/*
		rad[0].addEventListener('change',function () {
			console.log("der/key!");
			var certs = document.getElementById("certs");
			
			if(certs.childElementCount > 3){
				console.log("certs" + certs.childElementCount);
				console.log("certs" + certs.lastChild);
				certs.removeChild(certs.lastChild);
			}
				
			var pickerContainer = document.createElement("div");
			
			var derLabel = document.createElement("label");
			derLabel.setAttribute('class',"control-label");
			derLabel.setAttribute('for',"co_cert_der");
			derLabel.innerHTML = "der";
			
			var derPicker = document.createElement("input");
			derPicker.setAttribute('class',"form-control");
			derPicker.setAttribute('type',"file");
			derPicker.setAttribute('id',"co_cert_der");
			derPicker.setAttribute('name',"co_cert_der");
			derPicker.setAttribute('accept',".der");
			
			var keyLabel = document.createElement("label");
			keyLabel.setAttribute('class',"control-label");
			keyLabel.setAttribute('for',"co_cert_key");
			keyLabel.innerHTML = "key";
			
			var keyPicker = document.createElement("input");
			keyPicker.setAttribute('class',"form-control");
			keyPicker.setAttribute('type',"file");
			keyPicker.setAttribute('id',"co_cert_key");
			keyPicker.setAttribute('name',"co_cert_key");
			keyPicker.setAttribute('accept',".key");
			
			pickerContainer.appendChild(derLabel);
			pickerContainer.appendChild(derPicker);
			pickerContainer.appendChild(keyLabel);
			pickerContainer.appendChild(keyPicker);
			certs.appendChild(pickerContainer);
			isDer = true;
		
		});
		rad[1].addEventListener('change',function () {
			console.log("pfx!");
			var certs = document.getElementById("certs");
			if(certs.childElementCount > 3) {
				console.log("certs" + certs.childElementCount);
				console.log("certs" + certs.lastChild);
				certs.removeChild(certs.lastChild);
			}
			var pickerContainer = document.createElement("div");
			
			var pfxLabel = document.createElement("label");
			pfxLabel.setAttribute('class',"control-label");
			pfxLabel.setAttribute('for',"co_cert_pfx");
			pfxLabel.innerHTML = "pfx";
			
			var pfxPicker = document.createElement("input");
			pfxPicker.setAttribute('class',"form-control");
			pfxPicker.setAttribute('type',"file");
			pfxPicker.setAttribute('id',"co_cert_pfx");
			pfxPicker.setAttribute('name',"co_cert_pfx");
			pfxPicker.setAttribute('accept',".pfx");
			
			pickerContainer.appendChild(pfxLabel);
			pickerContainer.appendChild(pfxPicker);
			certs.appendChild(pickerContainer);
			isDer = false;
		});*/
		
		function readFile(file, callback) {
			return new Promise((resolve,reject) =>  {
				const derMaxSize = 4096;
				const pfxMaxSize = 8192;
				
				if(file.files[0] == null) {
					resolve();
				}
				
				if(file.files[0].size == 0) {
					reject(new Error("[msg : file size is 0] [code : ]"));
				}
				if(isDer) {
					if(file.files[0].size > derMaxSize) {
						reject(new Error("[msg : Der/key File size bigger than 4kb] [code : ]"));
					}
				} else {
					if(file.files[0].size > pfxMaxSize) {
						reject(new Error("[msg : pfx File size bigger than 8kb] [code : ]"));
					}
				}
				var fileReader = new FileReader();
				 fileReader.onload = () => {
					callback(fileReader.result);
					resolve();// promise는 resolve가 호출될때까지 기다린다.(resolve();가 promise에선 return;과 같다.)
				} 
				fileReader.onError = reject;
				
				fileReader.readAsBinaryString(file.files[0]);
			});
		}
		
		async function modify() {
			//jsonBody
			//certinfo
			var co_name = document.getElementById("co_name").value;
			var co_cert_pw = document.getElementById("co_cert_pw").value;
			var co_cert_type = ((isDer) ? (1) : (2));
			var co_cert_der = null;
			var co_cert_key = null;
			var co_certification = null;
			
			//privateinfo
			var co_kname = document.getElementById("co_kname").value;
			var co_ename = document.getElementById("co_ename").value;
			var co_corp = bupin.checked;
			var co_rrn1 = document.getElementById("co_rrn1").value;
			var co_rrn2 = null;
			var co_tel = document.getElementById("co_tel").value;
			// addr1 : 전역변수
			// addr2 : 전역변수
			// addr3 : 전역변수
			var co_relation = document.getElementById("co_relation").value;
			var co_relation_name = document.getElementById("co_relation_name").value;
			var co_house_hold = document.getElementById("co_house_hold").value;
			var co_hojuk_name = document.getElementById("co_hojuk_name").value;
			var co_car = document.getElementById("co_car").value;
			var co_saupja_num = document.getElementById("co_saupja_num").value;

			
			var onload = false;
			if(isDer) {
				try {
					await readFile(document.getElementById("co_cert_der"),function(e) {
						co_cert_der = btoa(e);
						//console.log("co_cert_der : " +co_cert_der);
					});
				} catch(err) {
					alert(err);
					//alert('err : ${err.name}: ${err.message}');
					//return false;
				}
				
				await readFile(document.getElementById("co_cert_key"),function(e) {
					co_cert_key = btoa(e);
					//console.log("co_cert_key : " +co_cert_key);
				});
				console.log("co_cert_der : " +co_cert_der);
				console.log("co_cert_key : " +co_cert_key);
			} else {
				try {
				await readFile(document.getElementById("co_cert_pfx"),function(e) {
					co_certification = btoa(e);
				});
				} catch(err) {
					alert(err);
					//alert('err : ${err.name}: ${err.message}');
					//return false;
				}
				console.log("co_certification : " +co_certification);
			}
			
			var urls = document.getElementsByName("co_domain");
			var ids = document.getElementsByName("co_id");
			var pws = document.getElementsByName("co_pw");
			
			
			for(var i = 0 ; i < urls.length;i++) {
				console.log("url" + "[" + i + "] : " +urls[i].value);
				console.log("id" + "[" + i + "] : " +ids[i].value);
				console.log("pw" + "[" + i + "] : " +pws[i].value);
			}
			
			var json = new Object();
			var personalInfo = new Object();
			json.subject = co_name;
			
			personalInfo.kname = co_kname;
			personalInfo.ename = co_ename;
			personalInfo.corp = co_corp;
			personalInfo.rrn1 = co_rrn1;
			personalInfo.rrn2 = co_rrn2;
			personalInfo.tel = co_tel;
			personalInfo.addr1 = addr1;
			personalInfo.addr2 = addr2;
			personalInfo.addr3 = addr3;
			personalInfo.relation = co_relation;
			personalInfo.relation_name = co_relation_name;
			personalInfo.house_hold = co_house_hold;
			personalInfo.hojuk_name = co_hojuk_name;
			personalInfo.car = co_car;
			personalInfo.saupja_num = co_saupja_num;
			
			json.personalInfo = personalInfo;
			
			json.cert_pw = co_cert_pw;
			json.cert_type = co_cert_type;
			var certification = new Object();
			certification.der = co_cert_der;
			certification.key = co_cert_key;
			certification.pfx = co_certification;
			json.certification = certification;
			
			var account = new Array();
			for(var i = 0; i < urls.length;i++) {
				var element = new Object();
				element.site = urls[i].value;
				element.id = ids[i].value;
				element.pw = pws[i].value;
				account.push(element);
			}
			json.account = account;
			json.count = urls.length;
			
			console.log("request: \n" + JSON.stringify(json));
			
			var request = json;

			//fetch
			fetch('/private/modify',{
		        method: 'POST', // *GET, POST, PUT, DELETE, etc.
		        mode: 'cors', // no-cors, cors, *same-origin
		        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
		        credentials: 'same-origin', // include, *same-origin, omit
		        headers: {
		            'Content-Type': 'application/json'
		        },
		        redirect: 'follow', // manual, *follow, error
		        referrer: 'no-referrer', // no-referrer, *client
		        body: JSON.stringify(request) // body data type must match "Content-Type" header
		    }).then(function(response) {
	    		//console.log(JSON.stringify(response.json()));
		    	if(response.ok) {
				  if(confirm("수정완료")) {
					  window.location.href="/";
				  }
				} 
		    	return response.json();  
			}).then(function(myjson) {
				  console.log(JSON.stringify(myjson));
				  if(myjson.hasOwnProperty("code")) //json 객체가 code 값을 포함할 때 (에러발생)
				 	 alert(myjson["message"]);
			});
		}

			function addSite() {
			var sites = document.getElementById("sites");
			var newDomain = document.createElement("div");
			var urlNode = document.createElement("input");
			var idNode = document.createElement("input");
			var pwNode = document.createElement("input");
			var button = document.createElement("input");
			var border= document.createElement("hr");
			
			console.log(sites.childElementCount);
			
			urlNode.name = "co_domain";
			urlNode.className = "form-control"
			urlNode.placeholder = "url";
			
			idNode.name = "co_id";
			idNode.className = "form-control"
			idNode.placeholder = "id";
			
			pwNode.name = "co_pw"
			pwNode.className = "form-control"
			pwNode.placeholder = "pw";
			//<input type ="button" value="+"  onclick="addSite()" style="WIDTH: 30pt;"/>
			
			
			button.setAttribute('type',"button");
			button.setAttribute('value',"-");
			button.setAttribute('style',"WIDTH: 30pt;");
			var x = sites.childElementCount;
			button.addEventListener('click',function() {deleteSite(button)});
			
			newDomain.appendChild(urlNode);
			newDomain.appendChild(idNode);
			newDomain.appendChild(pwNode);
			newDomain.appendChild(button);
			newDomain.appendChild(border);
			
			sites.appendChild(newDomain);
			console.log(sites.lastChild);
		}
			
		function deleteSite(node) {
			var sites = document.getElementById("sites");
			console.log(sites.childElementCount);
			console.log(node);
			console.log(node.parentElement);
	        if (sites.childElementCount > 0)
	        	node.parentElement.remove();
		}
	</script>

	<script src="/webjars/jquery/3.3.1/jquery.min.js"></script>
	<script src="/webjars/bootstrap/4.1.0/js/bootstrap.min.js"></script>

</body>
</html>