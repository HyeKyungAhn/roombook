<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-04-02
  Time: 오전 12:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="kr">
<head>
    <meta charset="utf-8">
    <title>roombook | 부서 이동</title>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/dist/themes/default/style.min.css" />
</head>
<body>
<div id="jstree"></div>
<button>저장</button>

<script src="${pageContext.request.contextPath}/dist/jstree.min.js"></script>
<script>
    $(function () {
        $('#jstree').on('ready.jstree', (e, data) => {
            data.instance.sort = () => {}; //drag and drop 시 sort 되지 않게 하기 위함
        });

        $('#jstree').jstree({
            'core' : {
                'check_callback': true,
                'data' : function(node, cb){
                    $.ajax({
                        type: 'GET',
                        dataType: 'text',
                        contentType: 'application/json',
                        url: '/dept/tree',
                        }).done(function (d){cb(JSON.parse(d));})
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
            fetch('/dept/move', {
                method : 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(nodes)
            }).then(response => {
                    return response.text();
                }).then(text => {
                    const modifiedNum = parseInt(text);
                    if(modifiedNum > 0){
                        alert(text + '개의 부서가 수정되었습니다.');
                    } else if(modifiedNum === 0){
                        alert('변경사항이 없습니다.');
                    } else {
                        alert('잘못된 접근입니다.');
                    }
                }).catch(error => {
                    console.error('Error sending data:', error);
                });
        });
    });
</script>
</body>
</html>
