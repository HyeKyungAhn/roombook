<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-09-26
  Time: 오후 4:59
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE>
<html lang="kr">
<head>
    <title><tiles:getAsString name="title"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/globalStyle.css" />
</head>
<body>
    <div>
        <div>
            <tiles:insertAttribute name="adminPageHeader"/>
        </div>
        <div>
            <div class="contentWrapper rootWrapper">
                <tiles:insertAttribute name="adminPageContent"/>
            </div>
        </div>
    </div>
</body>
</html>
