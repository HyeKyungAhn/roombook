<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-04-02
  Time: 오전 12:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="kr">
<head>
    <meta charset="utf-8">
    <title>roombook | 부서 목록</title>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/dist/themes/default/style.min.css" />
</head>
<body>
<h1>조직도</h1>
<p>부서를 클릭하여 관련 정보를 수정 및 삭제하세요</p>
<button id="deptMove">부서 이동</button>
<button id="deptAdd">부서 추가</button>
<div id="jstree"></div>
<script src="${pageContext.request.contextPath}/dist/jstree.min.js"></script>
<script>
    document.getElementById('deptMove').addEventListener('click', function (){
       location.href = '<c:url value="/dept/move"/>';
    });

    document.getElementById('deptAdd').addEventListener('click', function (){
        location.href = '<c:url value="/dept/save"/>';
    });
    $(function () {
        const $jstree = $('#jstree');
        $jstree.on('ready.jstree', () => {
            $jstree.jstree('open_all');
        });

        $jstree.jstree({
            'core' : {
                'data' : function(node, cb){
                    $.ajax({
                        type: 'GET',
                        dataType: 'text',
                        contentType: 'application/json;charset:UTF-8',
                        url: '/dept/tree',
                        }).done(function (data){
                            const nodes = JSON.parse(data);
                            for(let node of nodes){
                                node.a_attr = {
                                    href: '<c:url value="/dept/dept"/>'+'?deptCd='+node.id
                                }
                            }
                            cb(nodes);
                        });
                }
            },
            'sort': function (a, b) {
                let a1 = this.get_node(a);
                let b2 = this.get_node(b);
                return (a1.data.odr > b2.data.odr) ? 1 : -1;
            },
            'types': {
                "default" : {
                    "icon" : "glyphicon glyphicon-flash"
                },
                "demo" : {
                    "icon" : "glyphicon glyphicon-ok"
                }
            },
            'plugins': [
                'changed', 'sort', 'types'
            ]});

        $jstree.on('select_node.jstree', function (e, data) {
            $('#jstree').jstree(true).deselect_node(data.node.id);
            window.location.href = data.node.a_attr.href;
        });
    });

    window.onpageshow = (e) => {
        if(e.persisted){
            const $jstree = $('#jstree');
            $jstree.jstree('refresh');
        }
    }
</script>
</body>
</html>
