<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-09-26
  Time: 오후 4:30
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE>
<html lang="kr">
<head>
    <title><tiles:getAsString name="title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/globalStyle.css"/>
    <sec:authorize access="isAuthenticated()">
    <script src="${pageContext.request.contextPath}/js/accountDropdown.js"></script>
    </sec:authorize>
</head>
<body>
    <div>
        <div>
            <tiles:insertAttribute name="header"/>
        </div>
        <div class="contentWrapper rootWrapper">
            <tiles:insertAttribute name="content"/>
        </div>
        <div>
            <tiles:insertAttribute name="footer"/>
        </div>
    </div>
</body>
</html>
