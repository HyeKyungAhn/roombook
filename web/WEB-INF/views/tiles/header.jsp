<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-09-26
  Time: 오후 4:30
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<nav class="globalNav">
    <div class="globalNavContent">
        <div class="logoWrapper">
            <a href="${pageContext.request.contextPath}/">
                <img class="logoImg" alt="roombook logo" src="${pageContext.request.contextPath}/img/logo.png"/>
            </a>
        </div>
        <div class="globalNavMenuList">
            <div class="menuListItem">
                <a href="${pageContext.request.contextPath}/spaces">공간 예약</a>
            </div>
            <div class="menuListItem">
                <a href="#">공지사항</a>
            </div>
        </div>
        <div class="globalNavAccount">
        <sec:authorize access="isAnonymous()">
            <div class="accountItem">
                <a href="${pageContext.request.contextPath}/signin">로그인</a>
            </div>
            <div class="accountItem">
                <a class="signupBtn" href="${pageContext.request.contextPath}/signup">회원가입</a>
            </div>
        </sec:authorize>
        <sec:authorize access="isAuthenticated()">
            <div class="accountItem">
                <a href="${pageContext.request.contextPath}/signout">로그아웃</a>
            </div>
            <sec:authorize access="hasRole('ROLE_USER')">
            <div class="accountItem">
                <span id="myAccountBtn" class="myAccountBtn"></span>
                <div id="myAccountDropdown" class="myAccountDropdown dropdown">
                    <a href="#">내 계정</a>
                    <a href="${pageContext.request.contextPath}/mybook">내 예약</a>
                </div>
            </div>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ROLE_RSC_ADMIN', 'ROLE_EMPL_ADMIN', 'ROLE_SUPER_ADMIN')">
            <div class="accountItem">
                <a href="${pageContext.request.contextPath}/admin/spaces">관리자 페이지</a>
            </div>
            </sec:authorize>
        </sec:authorize>
        </div>
    </div>
</nav>
