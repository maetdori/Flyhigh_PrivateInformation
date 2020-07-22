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
							<form method="POST">
								<div class="form-group has-feedback">
									<label class="control-label" for="co_name">성명(영문)</label>
									<input class="form-control" type="text" id="co_name" name="co_name" />
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_cert_pw">인증서 비밀번호</label>
									<input class="form-control" type="password" id="co_cert_pw" name="co_cert_pw" />
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="co_cert_der">인증서</label>
									<input class="form-control" type="file" id="co_cert_der" name="co_cert_der" />
								</div>
								<div class="form-group has-feedback">
									<label class="control-label" for="account">계정</label>
									<input class="form-control" type="text" id="account" name="co_domain" />
									<input class="form-control" type="text" id="account" name="co_id" />
									<input class="form-control" type="text" id="account" name="co_pw" />
								</div>
							</form>
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