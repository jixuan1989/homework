<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
<title>文件上传</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">

  <style type="text/css">
     .show-img{
    max-width:320px; //firefox
    max-height:240px;
    scale:expression((this.offsetWidth / this.offsetHeight > 320/240)?(this.style.width = this.offsetWidth >= 320 ? "320px" : "auto"):(this.style.height = this.offsetHeight >= 240 ? "240px" : "auto")); //ie
    display:inline !important;

}
</style>
</head>
<body>
	<!-- ${pageContext.request.contextPath}/upload/execute_upload.do -->
	<!-- ${pageContext.request.contextPath}/upload2/upload2.do -->
	<form action="image.html" enctype="multipart/form-data" method="post">
		文件:<input type="file" name="image"> <input type="submit"
			value="上传并搜索" />
	</form>
	<br />
	<s:fielderror />
	${fn:length(results)}
	<c:if test="${fn:length(results)!=0}">
		<dl>
		<c:forEach var="t" items="${results}">
			<dt>${t }</dt>
			<dd><p><img class="show-img" src="${pageContext.request.contextPath}${t}"/></p></dd>
			</c:forEach>
		</dl>
	</c:if>
	
</body>
</html>
