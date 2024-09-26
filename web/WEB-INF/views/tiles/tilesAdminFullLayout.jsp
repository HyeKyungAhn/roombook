<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-09-26
  Time: 오후 4:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE>
<html lang="kr">
<head>
    <title></title>
</head>
<body>
    <div>
        <tiles:insertAttribute name="adminPageHeader"/>
    </div>
    <div>
        <div id="asideNav">
            <tiles:insertAttribute name="adminPageAsideNav"/>
        </div>
        <div>
            <tiles:insertAttribute name="adminPageContent"/>
        </div>
    </div>
    <div>
        <tiles:insertAttribute name="adminPageFooter"/>
    </div>
</body>
</html>
