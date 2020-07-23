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
						<div class="card-body">
							<h4 class="card-title text-center text-dark">
							인증서 관리
							<p></p>
							</h4>
							<table border="1">
						        <tr>
						        	<th>  </th>
						            <th> subject </th>
						            <th> cert_pw </th>
						            <th> not_before </th>
						            <th> not_after </th>
						        </tr>
							<%
							    for( CertVO cert : list){
							%>
							        <tr>
							        	<td><input type="checkbox" name="co_name" value=<%= cert.getCo_name() %>>
							            <td>
								            <a href='/modifyPage'>
								            	<%= cert.getCo_name() %>
								            </a>
							            </td>
							            <td><%= cert.getCo_cert_pw() %> </td>
							        </tr>
							<% } %>
							    </table>
							<div class="form-group margin-top20">
								<form action='/registerPage' >
									<button type="submit" class="btn btn-info btn-block" id="btnRegister">
										인증서 등록
									</button>
								</form>
								<form>
									<button  type="button" class="btn btn-info btn-block" id="btnDelete">
										인증서 삭제
									</button>
								</form>
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
</html>