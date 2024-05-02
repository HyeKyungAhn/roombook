<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-04-05
  Time: 오후 5:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <meta charset="utf-8">
    <title>roombook | 부서 추가</title>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/dist/themes/default/style.min.css" />
</head>
<body>
    <div>
        <h1>${param.deptNm}</h1>
        <h1>${param.engDeptNm}</h1>
        <h1>${param.parent}</h1>
        <h1>${param.mngr}</h1>
    </div>

    <div id="jstree"></div>
    <button>저장</button>
    <script src="${pageContext.request.contextPath}/dist/jstree.min.js"></script>
    <script>
        $(function () {
            $('#jstree').on('ready.jstree', (e, data) => {
                data.instance.sort = () => {};
            });

            $('#jstree').jstree({
                'core' : {
                    'check_callback': true,
                    'data' : function(node, cb){
                        $.ajax({
                            type: 'GET',
                            dataType: 'text',
                            data: {
                                'deptNm': '${param.deptNm}',
                                'parent': '${param.parent}',
                                'mngr': '${param.mngr}'
                            },
                            contentType: 'application/json',
                            url: '/dept/tree2',
                        }).done(function (data){
                            const NO_DEPT_CD = 0;
                            const nodes = JSON.parse(data);
                            console.log(nodes);
                            for(let node of nodes){
                                if(parseInt(node.id)===NO_DEPT_CD){
                                    node.state = {
                                        selected: true
                                    }
                                }
                            }
                            cb(nodes);
                        })
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
                    'changed', 'dnd', 'sort', 'types'
                ]});


            $('#jstree').on("move_node.jstree", function (e, data) {
                let old_odr = data.old_position;
                let old_parent = data.old_parent;
                let new_parent = data.parent;
                let new_odr = data.position;
                let nodes = data.old_instance._model.data;

                if(old_parent === new_parent){
                    const isMovingUp = old_odr > new_odr;
                    const odrChange = isMovingUp ? 1 : -1;

                    for (const node in nodes) {
                        if (nodes[node].parent === old_parent) {
                            const { odr } = nodes[node].data;
                            if ((isMovingUp && odr >= new_odr && odr < old_odr) ||
                                (!isMovingUp && odr > old_odr && odr <= new_odr)) {
                                nodes[node].data.odr += odrChange;
                            }
                        }
                    }
                } else {
                    for(let node in nodes){
                        if(nodes[node].parent === old_parent && old_odr < nodes[node].data.odr) {
                            nodes[node].data.odr -= 1;
                        } else if(nodes[node].parent === new_parent && new_odr <= nodes[node].data.odr){
                            nodes[node].data.odr += 1;
                        }
                    }
                }
                data.node.data.odr = new_odr;
            });

            $('button').on('click', function () {
                const nodes = $('#jstree').jstree(true).get_json('#', {flat:true});

                for(let node of nodes){
                    node.engDeptNm = '';

                    if(parseInt(node.id)===0){
                        node.engDeptNm = '${param.engDeptNm}';
                        node.mngr = '${param.mngr}';
                    }
                }

                fetch('/dept/save2', {
                    method : 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(nodes)
                }).then(response => {
                    alert("부서 정보가 추가되었습니다");
                    return response.text();
                }).then(url => {
                    location.href = url;
                }).catch(error => {
                    console.error('Error sending data:', error);
                });
            });
        });

        window.onbeforeunload = function() {
            return false;
        };
    </script>
</body>
</html>