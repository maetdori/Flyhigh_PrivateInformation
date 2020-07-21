<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<title>Home</title>
	<link rel="stylesheet" href="/webjars/bootstrap/4.1.0/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="/css/Home.css">
</head>

<body class="my-Home-page">
	<div class="container h-100">
		<div class="row justify-content-md-center h-100">
			<div class="card-wrapper">
				<div class="brand">
					<img src="/img/logo.png">
				</div>
				<div class="card fat">
					<div class="card-body">
						<h4 class="card-title text-center text-dark">
						Welcome to OmniDoc Demo
						<p></p>
						<span class="badge badge-secondary">
						v1.0
						</span>
						</h4>
						<div class="form-group no-margin">
							<form action='/register' method='post'>
								<button type="button" class="btn btn-info btn-block" id="btnRegister">
									인증서 등록
								</button>
							</form>
								<button type="button" class="btn btn-info btn-block" id="btnModify">
									인증서 수정
								</button>
								<button type="button" class="btn btn-info btn-block" id="btnDelete">
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
	<script src="/webjars/jquery/3.3.1/jquery.min.js"></script>
	<script src="/webjars/bootstrap/4.1.0/js/bootstrap.min.js"></script>

</body>
</html>