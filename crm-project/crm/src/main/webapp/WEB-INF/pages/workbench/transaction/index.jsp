<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css"
          rel="stylesheet"/>
    <link type="text/css" rel="stylesheet" href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css">

    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

    <script type="text/javascript">

        $(function () {
            $("#createTranBtn").click(function () {
                window.location.href = "workbench/transaction/toSave.do";
            });

            queryTranByConditionForPage(1, 10);

            $("#searchTranBtn").click(function () {
                queryTranByConditionForPage(1, $("#demo_page").bs_pagination("getOption", "rowsPerPage"));
            });

            $("#deleteTranBtn").click(function () {
                var checkedIds = $("#tBody input[type='checkbox']:checked");
                if (checkedIds.size() == 0) {
                    alert("请选择要删除的交易");
                    return;
                }
                if (window.confirm("确定要删除吗？")) {
                    var ids = "";
                    $.each(checkedIds, function () {
                        ids += "id=" + this.value + "&";
                    });
                    ids = ids.substr(0, ids.length - 1);
                    $.ajax({
                        url:'workbench/tran/deleteByIds.do',
                        data: ids,
                        type:'post',
                        dataType:'json',
                        success: function (data) {
                            if (data.code == "1") {
                                queryTranByConditionForPage(1, $("#demo_page").bs_pagination("getOption", "rowsPerPage"));
                            } else {
                                alert(data.message);
                            }
                        }
                    });
                }
            });

            $("#checkAll").click(function () {
                $("#tBody input[type='checkbox']").prop("checked",this.checked);
            });

            $("#tBody").on("click", "input[type='checkbox']", function () {
                if ($("#tBody input[type='checkbox']").size() == $("#tBody input[type='checkbox']:checked").size()) {
                    $("#checkAll").prop("checked", true);
                } else {
                    $("#checkAll").prop("checked", false);
                }
            });

            $("#editTranBtn").click(function () {
                var checkedIds = $("#tBody input[type='checkbox']:checked");
                if (checkedIds.size() == 0) {
                    alert("请选择要修改的交易信息");
                    return;
                }
                if (checkedIds.size() > 1) {
                    alert("每次只能修改一条交易信息");
                    return;
                }
                var id = checkedIds.val();
                window.location.href = "workbench/tran/toEdit.do?id="+id+"";
            });
        });

        function queryTranByConditionForPage(pageNo, pageSize) {
            //收集参数
            var name = $.trim($("#query-name").val());
            var source = $("#query-source").val();
            var owner = $.trim($("#query-owner").val());
            var stage = $("#query-stage").val();
            var customerId = $.trim($("#query-customerId").val());
            var type = $("#query-type").val();
            var contactsId = $.trim($("#query-contactsId").val());

            $.ajax({
                url: 'workbench/tran/queryTranByConditionForPage.do',
                data: {
                    name: name,
                    contactsId: contactsId,
                    type: type,
                    source: source,
                    owner: owner,
                    customerId: customerId,
                    stage: stage,
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    var htmlStr = "";
                    $.each(data.tranList, function (index, obj) {
                        htmlStr += "<tr>";
                        htmlStr += "<td><input type=\"checkbox\" value='" + obj.id + "'/></td>";
                        htmlStr += "<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/transaction/detailTran.do?tranId=" + obj.id + "';\">" + obj.name + "</a></td>";
                        htmlStr += "<td>" + obj.customerId + "</td>";
                        htmlStr += "<td>" + obj.stage + "</td>";
                        htmlStr += "<td>" + (obj.type == null ? "" : obj.type) + "</td>";
                        htmlStr += "<td>" + obj.owner + "</td>";
                        htmlStr += "<td>" + (obj.source == null ? "" : obj.source) + "</td>";
                        htmlStr += "<td>" + obj.contactsId + "</td>";
                        htmlStr += "</tr>";
                    });
                    $("#tBody").html(htmlStr);
                    $("#checkAll").prop("checked", false);

                    var totalPages = 1;
                    if ((data.totalRows) % pageSize == 0) {
                        totalPages = data.totalRows / pageSize;
                    } else {
                        totalPages = parseInt(data.totalRows / pageSize) + 1;
                    }

                    $("#demo_page").bs_pagination({
                        currentPage: pageNo,
                        rowsPerPage: pageSize,
                        totalRows: data.totalRows,
                        totalPages: totalPages,
                        visiblePageLinks: 5,
                        showGoToPage: true,
                        showRowsPerPage: true,
                        showRowsInfo: true,
                        onChangePage: function (event, pageObj) {
                            queryTranByConditionForPage(pageObj.currentPage, pageObj.rowsPerPage);
                        }
                    });
                }
            });
        }
    </script>
</head>
<body>


<div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>交易列表</h3>
        </div>
    </div>
</div>

<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">

    <div style="width: 100%; position: absolute;top: 5px; left: 10px;">

        <div class="btn-toolbar" role="toolbar" style="height: 80px;">
            <form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">所有者</div>
                        <input class="form-control" type="text" id="query-owner">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">名称</div>
                        <input class="form-control" type="text" id="query-name">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">客户名称</div>
                        <input class="form-control" type="text" id="query-customerId">
                    </div>
                </div>

                <br>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">阶段</div>
                        <select class="form-control" id="query-stage">
                            <option></option>
                            <c:forEach items="${stageList}" var="s">
                                <option value="${s.id}">${s.value}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">类型</div>
                        <select class="form-control" id="query-type">
                            <option></option>
                            <c:forEach items="${transactionTypeList}" var="t">
                                <option value="${t.id}">${t.value}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">来源</div>
                        <select class="form-control" id="query-source">
                            <option></option>
                            <c:forEach items="${sourceList}" var="s">
                                <option value="${s.id}">${s.value}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">联系人名称</div>
                        <input class="form-control" type="text" id="query-contactsId">
                    </div>
                </div>

                <button id="searchTranBtn" type="button" class="btn btn-default">查询</button>

            </form>
        </div>
        <div class="btn-toolbar" role="toolbar"
             style="background-color: #F7F7F7; height: 50px; position: relative;top: 10px;">
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-primary" id="createTranBtn"><span
                        class="glyphicon glyphicon-plus"></span> 创建
                </button>
                <button id="editTranBtn" type="button" class="btn btn-default"><span
                        class="glyphicon glyphicon-pencil"></span> 修改
                </button>
                <button id="deleteTranBtn" type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
            </div>


        </div>
        <div style="position: relative;top: 10px;">
            <table class="table table-hover">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td><input type="checkbox" id="checkAll"/></td>
                    <td>名称</td>
                    <td>客户名称</td>
                    <td>阶段</td>
                    <td>类型</td>
                    <td>所有者</td>
                    <td>来源</td>
                    <td>联系人名称</td>
                </tr>
                </thead>
                <tbody id="tBody">
                <%--<tr>
                    <td><input type="checkbox"/></td>
                    <td><a style="text-decoration: none; cursor: pointer;"
                           onclick="window.location.href='workbench/transaction/detailTran.do?tranId=96d74f5184bc4c5b9d2cc95b8fd4e4e2';">动力节点-交易01</a>
                    </td>
                    <td>动力节点</td>
                    <td>谈判/复审</td>
                    <td>新业务</td>
                    <td>zhangsan</td>
                    <td>广告</td>
                    <td>李四</td>
                </tr>--%>
                </tbody>
            </table>

            <div id="demo_page"></div>
        </div>

    </div>

</div>
</body>
</html>