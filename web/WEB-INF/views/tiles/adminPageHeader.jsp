<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-09-26
  Time: 오후 5:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<div class="globalNav adminGlobalNav fontColor_white">
    <div class="globalNavContent">
        <div class="adminHeaderLogo">
            <div class="logoWrapper">
                <a href="${pageContext.request.contextPath}/admin/spaces">
                    <img class="logoImg invertLogoImage" alt="roombook logo" src="${pageContext.request.contextPath}/resources/img/logo.png"/>
                </a>
            </div>
            <span class="adminHeaderLogoText">관리자 페이지</span>
        </div>
        <div class="navMenuWrapper">
            <div class="navMenuContent">
                <ol class="navMenu">
                    <li class="navMenuItem">공간</li>
                    <li class="navMenuItem">부서</li>
                    <li class="navMenuItem">사용자 관리</li>
                </ol>
                <div class="navSubMenuWrapper">
                    <div class="navSubMenuContent">
                        <ol class="navSubMenu">
                            <li class="navSubMenuItem">
                                <a href="${pageContext.request.contextPath}/admin/spaces">공간 목록</a>
                            </li>
                            <li class="navSubMenuItem">
                                <a href="${pageContext.request.contextPath}/admin/spaces/new">새 공간 등록</a>
                            </li>
                        </ol>
                        <ol class="navSubMenu">
                            <li class="navSubMenuItem">
                                <a href="${pageContext.request.contextPath}/dept/list">부서 목록</a>
                            </li>
                            <li class="navSubMenuItem">
                                <a href="${pageContext.request.contextPath}/dept/save">부서 생성</a>
                            </li>
                            <li class="navSubMenuItem">
                                <a href="${pageContext.request.contextPath}/dept/move">부서 이동</a>
                            </li>
                        </ol>
                        <ol class="navSubMenu">
                            <li class="navSubMenuItem">
                                <a href="${pageContext.request.contextPath}/admin/empls">권한 관리</a>
                            </li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <div class="userHomePageLinkWrapper">
            <a href="${pageContext.request.contextPath}/"  class="userHomePageLink">
                <span class="userHomePageLinkText rightArrowAfter fontColor_white">사용자 페이지로</span>
            </a>
        </div>
    </div>
</div>
