<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<title>Register</title>
	<link rel="stylesheet" href="/webjars/bootstrap/4.1.0/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="/css/Home.css">
<<<<<<< HEAD
	<script src="/webjars/jquery/3.3.1/jquery.min.js"></script>
	<script src="/webjars/bootstrap/4.1.0/js/bootstrap.min.js"></script>
	
	<script type='text/javascript'>
	
 	$(function() {
 		$("#add").on("click", function() {
 			//clone
 			$.trClone = $("#accountTable tr:last").clone().html();
 			$.newTr = $("<tr>" + $.trClone + "</tr>");
 			
 			//append
 			$("#accountTable").append($.newTr);
 			
 			//delete Button 추가
 			$.btnDelete = $(document.createElement("input"));
 			$.btnDelete.attr({
 				name : "btnRemove",
 				type : "button",
 				value : "삭제"
 			});
 			$("#accountTable tr:last td:last").html("");
 			$("#accountTable tr:last td:last").append($.btnDelete);
 			
 			//버튼에 클릭 이벤트 추가
 			$("#accountTable tr>td:last>input[type='button']").on('click', function() {
 				$(this).parent().parent().remove();
 			});
 		});
 	});
 	
	</script>
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
							인증서 등록
							<p></p>
							</h4>
<<<<<<< HEAD
								<div class="form-group has-feedback">
									<label class="control-label" for="co_name"><strong>성명(영문)</strong></label>
									<input class="form-control" type="text" id="co_name" name="co_name" placeholder="Name" autofocus required/>
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_cert_pw"><strong>인증서 비밀번호</strong></label>
									<input class="form-control" type="password" id="co_cert_pw" name="co_cert_pw" placeholder="Certificate Password" required/>
=======
							<form method="POST">
								<div class="form-group has-feedback">
									<label class="control-label" for="co_name">성명(영문)</label>
									<input class="form-control" type="text" id="co_name" name="co_name" />
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_cert_pw">인증서 비밀번호</label>
									<input class="form-control" type="password" id="co_cert_pw" name="co_cert_pw" />
>>>>>>> 5aacec6ed8a2d42c50de466845e048b783ad2925
								</div>
								<div class="form-group has-feedback" id="certs">
									<label class="control-label" for="co_cert_der">인증서</label>
									<input type="radio" name="cert_type" value="der/key" checked="checked"/> der/key
									<input type="radio" name="cert_type" value="pfx"  /> pfx
									<div>
										<label class="control-label" for="co_cert_der">der</label>
										<input class="form-control" type="file" id="co_cert_der" name="co_cert_der" accept=".der" />
										<label class="control-label" for="co_cert_key">key</label>
										<input class="form-control" type="file" id="co_cert_key" name="co_cert_key" accept=".key" /></div></div>
<<<<<<< HEAD
								<div style="width:100%; height:200px; overflow:auto">
									<table id="accountTable" class="table" style="margin-top: 20px;">
										<colgroup>
											<col width="45%"/>
											<col width="25%"/>
											<col width="25%"/>
										</colgroup>
										<thead>
											<tr>
												<th>도메인 이름</th>
												<th>ID</th>
												<th>PW</th>
												<th></th>
											</tr>
										</thead>
										<tbody id="accountTbody">
											<tr>
												<td><input class="form-control form-control-sm" type="text" id="co_domain" name="co_domain" placeholder="Domain" required></td>
												<td><input class="form-control form-control-sm" type="text" id="co_id" name="co_id" placeholder="ID" required></td>
												<td><input class="form-control form-control-sm" type="password" id="co_pw" name="co_pw" placeholder="PW" required></td>
												<td></td>
											</tr>  
										</tbody>
									</table>
									<input type="button" value="추가" id="add">
								</div>
								<input class="btn btn-info btn-block" type="submit" value="등록" onclick="register()" />
							</div>
						</div>
					</div>
				<div class="footer">
					Copyright © <a href="https://www.flyhigh-x.com/" class="badge badge-info">FLYHIGH</a> 2020
=======
								<div class="form-group has-feedback">
									<label class="control-label" for="account">계정</label>
									<input type ="button" value="+"  onclick="addSite()" style="WIDTH: 20pt;"/>
									<input type ="button" value="-"  onclick="deleteSite()" style="WIDTH: 20pt;"/></div>
								<div id ="sites">
									<div>
										<input class="form-control" type="text" id="co_domain" name="co_domain" placeholder="url" />
										<input class="form-control" type="text" id="co_id" name="co_id" placeholder="id"/>
										<input class="form-control" type="text" id="co_pw" name="co_pw" placeholder="pw"/>
										<hr/>
									</div>
								</div>
								<div class="form-group has-feedback">
										<input type="button" value="등록" class="btn btn-info btn-block" onclick="register()"/>
								</div>
							</form>
						</div>
					</div>
					<div class="footer">
						Copyright © <a href="https://www.flyhigh-x.com/" class="badge badge-info">FLYHIGH</a> 2020
					</div>
>>>>>>> 5aacec6ed8a2d42c50de466845e048b783ad2925
				</div>
			</div>
		</div>
	</section>
	<script type="text/javascript">
		var isDer = true;
		var rad = document.getElementsByName("cert_type");
		console.log(rad);
		
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
		});
		
		function readFile(file, callback) {
			return new Promise((resolve,reject) =>  {
				var fileReader = new FileReader();
				 fileReader.onload = () => {
					callback(fileReader.result);
					resolve();// promise는 resolve가 호출될때까지 기다린다.(resolve();가 promise에선 return;과 같다.)
				} 
				fileReader.onError = reject;
				
				fileReader.readAsBinaryString(file.files[0]);
			});
		}
		
		async function register() {
			//jsonBody
			var co_name = document.getElementById("co_name").value;
			var co_cert_pw = document.getElementById("co_cert_pw").value;
			var co_cert_type = ((isDer) ? (1) : (2));
			var co_cert_der = null;
			var co_cert_key = null;
			var co_certification = null;
			console.log("name : " +co_name);
			console.log("pw : " +co_name);
			console.log("type : " +co_cert_type);
			
			
			var onload = false;
			if(isDer) {
				await readFile(document.getElementById("co_cert_der"),function(e) {
					co_cert_der = btoa(e);
					//console.log("co_cert_der : " +co_cert_der);
				});
				await readFile(document.getElementById("co_cert_key"),function(e) {
					co_cert_key = btoa(e);
					//console.log("co_cert_key : " +co_cert_key);
				});
				console.log("co_cert_der : " +co_cert_der);
				console.log("co_cert_key : " +co_cert_key);
			} else {
				await readFile(document.getElementById("co_cert_pfx"),function(e) {
					co_certification = btoa(e);
				});
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
			json.subject = co_name;
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
			fetch('/private/register',{
		        method: 'POST', // *GET, POST, PUT, DELETE, etc.
		        mode: 'cors', // no-cors, cors, *same-origin
		        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
		        credentials: 'same-origin', // include, *same-origin, omit
		        headers: {
		            'Content-Type': 'application/json',
		            // 'Content-Type': 'application/x-www-form-urlencoded',
		        },
		        redirect: 'follow', // manual, *follow, error
		        referrer: 'no-referrer', // no-referrer, *client
		        body: JSON.stringify(json), // body data type must match "Content-Type" header
		    }) // private/register로 request 보냄
			  .then(function(response) {
			    return response.json(); //response를 json객체로
			  })
			  .then(function(myJson) {
				  //do something with json
			    console.log("response: \n" + JSON.stringify(myJson));
			  });
			
			
			//href
		}
		
<<<<<<< HEAD
		/*
=======
>>>>>>> 5aacec6ed8a2d42c50de466845e048b783ad2925
		function addSite() {
			var sites = document.getElementById("sites");
			var newDomain = document.createElement("div");
			var urlNode = document.createElement("input");
			var idNode = document.createElement("input");
			var pwNode = document.createElement("input");
			var border= document.createElement("hr");
			urlNode.name = "co_domain";
			urlNode.className = "form-control"
			urlNode.placeholder = "url";
			
			idNode.name = "co_id";
			idNode.className = "form-control"
			idNode.placeholder = "id";
			
			pwNode.name = "co_pw";
			pwNode.className = "form-control"
			pwNode.placeholder = "pw";
			
			newDomain.appendChild(urlNode);
			newDomain.appendChild(idNode);
			newDomain.appendChild(pwNode);
			newDomain.appendChild(border);
			
			sites.appendChild(newDomain);
		}
		function deleteSite() {
			var sites = document.getElementById("sites");
			console.log(sites.childElementCount);
	        if (sites.childElementCount > 1)
	           sites.removeChild(sites.lastChild);
		}
<<<<<<< HEAD
		*/
=======
>>>>>>> 5aacec6ed8a2d42c50de466845e048b783ad2925
	</script>

	<script src="/webjars/jquery/3.3.1/jquery.min.js"></script>
	<script src="/webjars/bootstrap/4.1.0/js/bootstrap.min.js"></script>

</body>
</html>