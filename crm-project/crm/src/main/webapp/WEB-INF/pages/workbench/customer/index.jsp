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
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

    <script type="text/javascript">

        $(function () {
            $("input").attr("autocomplete", "off");

            //定制字段
            $("#definedColumns > li").click(function (e) {
                //防止下拉菜单消失
                e.stopPropagation();
            });

            $("#createCustomerBtn").click(function () {
                $("#createCustomerForm")[0].reset();

                $("#createCustomerModal").modal("show");
            });

            $("#saveCreateCustomerBtn").click(function () {
                var owner = $("#create-customerOwner").val();
                var name = $.trim($("#create-customerName").val());
                var website = $.trim($("#create-website").val());
                var phone = $.trim($("#create-phone").val());
                var description = $.trim($("#create-description").val());
                var contactSummary = $.trim($("#create-contactSummary").val());
                var nextContactTime = $.trim($("#create-nextContactTime").val());
                var address = $.trim($("#create-address").val());

                //表单验证
                if (name == "") {
                    alert("名称不能为空")
                    return;
                }
                if (website != "") {
                    var websiteRegExp = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
                    if (!websiteRegExp.test(website)) {
                        alert("公司网站不合法");
                        return;
                    }
                }
                if (phone != "") {
                    var phoneRegExp = /\d{3}-\d{8}|\d{4}-\d{7}/;
                    if (!phoneRegExp.test(phone)) {
                        alert("公司座机不合法");
                        return;
                    }
                }
                $.ajax({
                    url: 'workbench/customer/saveCreateCustomer.do',
                    data: {
                        owner: owner,
                        name: name,
                        website: website,
                        phone: phone,
                        description: description,
                        contactSummary: contactSummary,
                        nextContactTime: nextContactTime,
                        address: address
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == "1") {
                            //关闭模态窗口
                            $("#createCustomerModal").modal("hide");
                            //刷新线索列表，显示第一页数据，保持每页显示条数不变(作业)
                            queryCustomerByConditionForPage(1, $("#demo_page").bs_pagination("getOption", "rowsPerPage"));
                        } else {
                            //提示信息
                            alert(data.message);
                            //模态窗口不关闭
                            $("#createCustomerModal").modal("show");
                        }
                    }
                });
            });

            queryCustomerByConditionForPage(1, 10);

            $("#queryCustomerBtn").click(function () {
                queryCustomerByConditionForPage(1, $("#demo_page").bs_pagination("getOption", "rowsPerPage"));
            });

            $("#checkAll").click(function () {
                $("#tBody input[type='checkbox']").prop("checked", this.checked);
            });

            $("#tBody").on("click", " input[type='checkbox']", function () {
                if ($("#tBody input[type='checkbox']").size() == $("#tBody input[type='checkbox']:checked").size()) {
                    $("#checkAll").prop("checked", true);
                } else {
                    $("#checkAll").prop("checked", false);
                }
            });

            $("#deleteCustomerBtn").click(function () {
                var checkedBoxs = $("#tBody input[type='checkbox']:checked");
                if (checkedBoxs.size() == 0) {
                    alert("请选择要删除的客户信息");
                    return;
                }
                if (window.confirm("确定要删除吗？")) {
                    var ids = "";
                    $.each(checkedBoxs, function (index, obj) {
                        ids += "id=" + this.value + "&";
                    });
                    ids = ids.substr(0, ids.length - 1);
                    $.ajax({
                        url: 'workbench/customer/deleteByIds.do',
                        data: ids,
                        type: 'post',
                        dataType: 'json',
                        success: function (data) {
                            if (data.code == "1") {
                                queryCustomerByConditionForPage(1, $("#demo_page").bs_pagination("getOption", "rowsPerPage"));
                            } else {
                                alert(data.message);
                            }
                        }
                    });
                }
            });

            $("#editCustomerBtn").click(function () {
                var checkedCus = $("#tBody input[type='checkbox']:checked");
                if (checkedCus.size() == 0) {
                    alert("请选择要修改的客户");
                    return;
                }
                if (checkedCus.size() > 1) {
                    alert("每次只能修改一条线索");
                    return;
                }
                var id = checkedCus.val();
                $.ajax({
                    url: 'workbench/customer/queryCustomerById.do',
                    data: {
                        id: id
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        $("#edit-id").val(data.id);
                        $("#edit-customerOwner option[value='" + id + "']").prop("selected", true);
                        $("#edit-customerName").val(data.name);
                        $("#edit-website").val(data.website);
                        $("#edit-phone").val(data.phone);
                        $("#edit-describe").val(data.description);
                        $("#edit-contactSummary").val(data.contactSummary);
                        $("#edit-nextContactTime").val(data.nextContactTime);
                        $("#edit-address").val(data.address);

                        $("#editCustomerModal").modal("show");
                    }
                });
            });

            $("#saveEditCustomerBtn").click(function () {
                //收集参数
                var id = $("#edit-id").val();
                var owner = $("#edit-customerOwner").val();
                var name = $.trim($("#edit-customerName").val());
                var phone = $.trim($("#edit-phone").val());
                var website = $.trim($("#edit-website").val());
                var description = $.trim($("#edit-describe").val());
                var contactSummary = $.trim($("#edit-contactSummary").val());
                var nextContactTime = $.trim($("#edit-nextContactTime").val());
                var address = $.trim($("#edit-address").val());
                //表单验证
                if (name == "") {
                    alert("名称不能为空")
                    return;
                }
                if (phone != "") {
                    var phoneRegExp = /\d{3}-\d{8}|\d{4}-\d{7}/;
                    if (!phoneRegExp.test(phone)) {
                        alert("公司座机不合法");
                        return;
                    }
                }
                if (website != "") {
                    var websiteRegExp = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
                    if (!websiteRegExp.test(website)) {
                        alert("公司网站不合法");
                        return;
                    }
                }
                //发送请求
                $.ajax({
                    url: 'workbench/customer/saveEditCustomer.do',
                    data: {
                        id: id,
                        owner: owner,
                        name: name,
                        phone: phone,
                        website: website,
                        description: description,
                        contactSummary: contactSummary,
                        nextContactTime: nextContactTime,
                        address: address
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == "1") {
                            //关闭模态窗口
                            $("#editCustomerModal").modal("hide");
                            //刷新线索列表，显示第一页数据，保持每页显示条数不变(作业)
                            queryCustomerByConditionForPage(1, $("#demo_page").bs_pagination("getOption", "rowsPerPage"));
                        } else {
                            //提示信息
                            alert(data.message);
                            //模态窗口不关闭
                            $("#editCustomerModal").modal("show");
                        }
                    }
                });
            });
        });

        function queryCustomerByConditionForPage(pageNo, pageSize) {
            var name = $.trim($("#query-name").val());
            var owner = $.trim($("#query-owner").val());
            var phone = $.trim($("#query-phone").val());
            var website = $.trim($("#query-website").val());

            $.ajax({
                url: 'workbench/customer/queryCustomerByConditionForPage.do',
                data: {
                    name: name,
                    owner: owner,
                    phone: phone,
                    website: website,
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    var htmlStr = "";
                    $.each(data.customerList, function (index, obj) {
                        htmlStr += "<tr>";
                        htmlStr += "<td><input value='" + obj.id + "' type=\"checkbox\" /></td>";
                        htmlStr += "<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/customer/queryCustomerForDetailById.do?id="+obj.id+"';\">" + obj.name + "</a></td>";
                        htmlStr += "<td>" + obj.owner + "</td>";
                        htmlStr += "<td>" + (obj.phone==null?"":obj.phone) + "</td>";
                        htmlStr += "<td>" + (obj.website==null?"":obj.website) + "</td>";
                        htmlStr += "</tr>";
                    });

                    $("#tBody").html(htmlStr);
                    $("#checkAll").prop("checked", false);

                    var totalPages = 1;
                    if ((data.totalRows % pageSize) == 0) {
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
                            queryCustomerByConditionForPage(pageObj.currentPage, pageObj.rowsPerPage);
                        }
                    });
                }
            });
        }
    </script>
</head>
<body>

<!-- 创建客户的模态窗口 -->
<div class="modal fade" id="createCustomerModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel1">创建客户</h4>
            </div>
            <div class="modal-body">
                <form id="createCustomerForm" class="form-horizontal" role="form">

                    <div class="form-group">
                        <label for="create-customerOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="create-customerOwner">
                                <c:forEach items="${userList}" var="u">
                                    <option value="${u.id}">${u.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <label for="create-customerName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-customerName">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="create-website" class="col-sm-2 control-label">公司网站</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-website">
                        </div>
                        <label for="create-phone" class="col-sm-2 control-label">公司座机</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-phone">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="create-description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="create-description"></textarea>
                        </div>
                    </div>
                    <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>

                    <div style="position: relative;top: 15px;">
                        <div class="form-group">
                            <label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="create-contactSummary"></textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-nextContactTime">
                            </div>
                        </div>
                    </div>

                    <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                    <div style="position: relative;top: 20px;">
                        <div class="form-group">
                            <label for="create-address" class="col-sm-2 control-label">详细地址</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="1" id="create-address"></textarea>
                            </div>
                        </div>
                    </div>
                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="saveCreateCustomerBtn">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 修改客户的模态窗口 -->
<div class="modal fade" id="editCustomerModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">修改客户</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" role="form">
                    <input type="hidden" id="edit-id">
                    <div class="form-group">
                        <label for="edit-customerOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-customerOwner">
                                <c:forEach items="${userList}" var="u">
                                    <option value="${u.id}">${u.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <label for="edit-customerName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-customerName" value="动力节点">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-website" class="col-sm-2 control-label">公司网站</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-website"
                                   value="http://www.bjpowernode.com">
                        </div>
                        <label for="edit-phone" class="col-sm-2 control-label">公司座机</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-phone" value="010-84846003">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="edit-describe"></textarea>
                        </div>
                    </div>

                    <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>

                    <div style="position: relative;top: 15px;">
                        <div class="form-group">
                            <label for="edit-contactSummary" class="col-sm-2 control-label">联系纪要</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="edit-contactSummary"></textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="edit-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-nextContactTime">
                            </div>
                        </div>
                    </div>

                    <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                    <div style="position: relative;top: 20px;">
                        <div class="form-group">
                            <label for="edit-address" class="col-sm-2 control-label">详细地址</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="1" id="edit-address">北京大兴大族企业湾</textarea>
                            </div>
                        </div>
                    </div>
                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="saveEditCustomerBtn">更新</button>
            </div>
        </div>
    </div>
</div>


<div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>客户列表</h3>
        </div>
    </div>
</div>

<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">

    <div style="width: 100%; position: absolute;top: 5px; left: 10px;">

        <div class="btn-toolbar" role="toolbar" style="height: 80px;">
            <form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">名称</div>
                        <input class="form-control" type="text" id="query-name">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">所有者</div>
                        <input class="form-control" type="text" id="query-owner">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">公司座机</div>
                        <input class="form-control" type="text" id="query-phone">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">公司网站</div>
                        <input class="form-control" type="text" id="query-website">
                    </div>
                </div>

                <button type="button" id="queryCustomerBtn" class="btn btn-default">查询</button>

            </form>
        </div>
        <div class="btn-toolbar" role="toolbar"
             style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
            <div class="btn-group" style="position: relative; top: 18%;">
                <button id="createCustomerBtn" type="button" class="btn btn-primary"><span
                        class="glyphicon glyphicon-plus"></span> 创建
                </button>
                <button id="editCustomerBtn" type="button" class="btn btn-default"><span
                        class="glyphicon glyphicon-pencil"></span> 修改
                </button>
                <button id="deleteCustomerBtn" type="button" class="btn btn-danger"><span
                        class="glyphicon glyphicon-minus"></span> 删除
                </button>
            </div>

        </div>
        <div style="position: relative;top: 10px;">
            <table class="table table-hover">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td><input type="checkbox" id="checkAll"/></td>
                    <td>名称</td>
                    <td>所有者</td>
                    <td>公司座机</td>
                    <td>公司网站</td>
                </tr>
                </thead>
                <tbody id="tBody">
                <%--<tr>
                    <td><input type="checkbox" /></td>
                    <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">动力节点</a></td>
                    <td>zhangsan</td>
                    <td>010-84846003</td>
                    <td>http://www.bjpowernode.com</td>
                </tr>--%>
                </tbody>
            </table>
            <div id="demo_page"></div>
        </div>
    </div>

</div>
</body>
</html>