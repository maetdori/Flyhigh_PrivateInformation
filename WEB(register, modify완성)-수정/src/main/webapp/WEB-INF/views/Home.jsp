<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="java.util.List"%>
    <%@page import="com.web.domain.CertVO" %>
    <% List<CertVO> list = (List<CertVO>) request.getAttribute("getList");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<title>Home</title>
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
						<div class="card-body" align="center">
							<h4 class="card-title text-center text-dark">
							Welcome to OmniDoc Demo
							<p></p>
							</h4>
							<div style="width:90%; height:200px; overflow:auto">
								<table class="table table-striped table-hover">
									<thead>
								        <tr>
								        	<th width="40%"></th>
								            <th width="60%">인증서목록</th>
								        </tr>
								    </thead>
								    <tbody>
									<%for(CertVO cert : list){%>
								        <tr>
								        	<td style="text-align:center;"><input type="checkbox" id="co_name" name="co_name" value=<%= cert.getCo_name() %>></td>
								        	<!-- <td><label for="checkbox"><%= cert.getCo_name() %></label></td> -->
								            <td><a href='/modifyPage?co_name=<%= cert.getCo_name() %>'>
								            	<%= cert.getCo_name() %>
								            </a></td>
								        </tr>
									<%}%>
									</tbody>
								</table>
							</div>
							<div class="form-group margin-top20">
								<button type="button" class="btn btn-info btn-block" id="btnRegister"
										onclick="location.href='/registerPage'">
									인증서 등록
								</button>
								<button type="button" class="btn btn-info btn-block" id="btnDelete" onclick="delete()">
									인증서 삭제
								</button>
							</div>
						</div>
					</div>
					<div class="footer">
						Copyright © <a href="https://www.flyhigh-x.com/" class="badge badge-info">FLYHIGH</a> 2020
					</div>
				</div>
			</div>
		</div>
	</section>

	<script src="/webjars/jquery/3.3.1/jquery.min.js"></script>
	<script src="/webjars/bootstrap/4.1.0/js/bootstrap.min.js"></script>

</body>

<script type="text/javascript">
function deleteFunc() {
	var checkbox = $("input:checkbox[name=co_name]:checked");
	console.log(checkbox);
	$.each(checkbox,function (i,item) {
		var tr = checkbox.parent().parent().eq(i);
		
		console.log(i);
		console.log(item);
		
		var co_name = $(item).attr("value");
		console.log(co_name);
		var request = new Object();
		request.subject = co_name;
		
		console.log("request:\n" + JSON.stringify(request));
		
		
		//fetch
		fetch('/private/delete',{
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
	    }) // private/register로 request 보냄
		  .then(function(response) {
		    return response.json(); //response를 json객체로
		  })
		  .then(function(myJson) {
			  //do something with json
		    console.log("response: \n" + JSON.stringify(myJson));
			
		  });
	});
	
	//refresh to see result
	
}
</script>
</html>