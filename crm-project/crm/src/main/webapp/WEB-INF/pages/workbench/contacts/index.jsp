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
    <script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

    <script type="text/javascript">

        $(function () {
            //定制字段
            $("#definedColumns > li").click(function (e) {
                //防止下拉菜单消失
                e.stopPropagation();
            });

            $("#createContactsBtn").click(function () {
                $("#createContactsForm")[0].reset();

                $("#createContactsModal").modal("show");
            });

            //当容器加载完成之后，对容器调用工具函数
            $(".customerNameTxt").typeahead({
                source: function (jquery, process) {//每次键盘弹起，都自动触发本函数；我们可以向后台送请求，查询客户表中所有的名称，把客户名称以[]字符串形式返回前台，赋值给source
                    //process：是个函数，能够将['xxx','xxxxx','xxxxxx',.....]字符串赋值给source，从而完成自动补全
                    //jquery：在容器中输入的关键字
                    //发送查询请求
                    $.ajax({
                        url: 'workbench/transaction/queryCustomerNamesByName.do',
                        data: {
                            customerName: jquery
                        },
                        type: 'post',
                        dataType: 'json',
                        success: function (data) {
                            process(data);
                        }
                    });
                }
            });

            $("#saveCreateContactsBtn").click(function () {
                var owner = $("#create-owner").val();
                var source = $("#create-source").val();
                var fullname = $.trim($("#create-fullname").val());
                var appellation = $("#create-appellation").val();
                var job = $.trim($("#create-job").val());
                var mphone = $.trim($("#create-mphone").val());
                var email = $.trim($("#create-email").val());
                var customerName = $.trim($("#create-customerName").val());
                var description = $.trim($("#create-description").val());
                var contactSummary = $.trim($("#create-contactSummary").val());
                var nextContactTime = $.trim($("#create-nextContactTime").val());
                var address = $.trim($("#create-address").val());

                if (fullname == "") {
                    alert("姓名不能为空")
                    return;
                }
                if (email != "") {
                    var emailRegExp = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
                    if (!emailRegExp.test(email)) {
                        alert("邮箱不合法");
                        return;
                    }
                }
                if (mphone != "") {
                    var mphoneRegExp = /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/;
                    if (!mphoneRegExp.test(mphone)) {
                        alert("手机不合法");
                        return;
                    }
                }
                if (customerName == "") {
                    alert("客户名称不能为空！");
                    return;
                }

                $.ajax({
                    url: 'workbench/contacts/saveCreateContacts.do',
                    data: {
                        fullname: fullname,
                        appellation: appellation,
                        owner: owner,
                        job: job,
                        email: email,
                        mphone: mphone,
                        source: source,
                        description: description,
                        contactSummary: contactSummary,
                        nextContactTime: nextContactTime,
                        address: address,
                        customerName: customerName
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == "1") {
                            //关闭模态窗口
                            $("#createContactsModal").modal("hide");
                            //刷新联系人列表，显示第一页数据，保持每页显示条数不变
                            queryContactsByConditionForPage(1, $("#demo_page").bs_pagination("getOption", "rowsPerPage"));
                        } else {
                            //提示信息
                            alert(data.message);
                            //模态窗口不关闭
                            $("#createContactsModal").modal("show");
                        }
                    }
                });
            });

            queryContactsByConditionForPage(1, 10);

            $("#queryContactsByConditionForPage").click(function () {
                queryContactsByConditionForPage(1, $("#demo_page").bs_pagination("getOption", "rowsPerPage"));
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

            $("#deleteContactsBtn").click(function () {
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
                        url: 'workbench/contacts/deleteByIds.do',
                        data: ids,
                        type: 'post',
                        dataType: 'json',
                        success: function (data) {
                            if (data.code == "1") {
                                queryContactsByConditionForPage(1, $("#demo_page").bs_pagination("getOption", "rowsPerPage"));
                            } else {
                                alert(data.message);
                            }
                        }
                    });
                }
            });

            $("#editContactsBtn").click(function () {
                var checkedCon = $("#tBody input[type='checkbox']:checked");
                if (checkedCon.size() == 0) {
                    alert("请选择要修改的联系人");
                    return;
                }
                if (checkedCon.size() > 1) {
                    alert("每次只能修改一条联系人信息");
                    return;
                }
                var id = checkedCon.val();
                $.ajax({
                    url: 'workbench/contacts/queryContactsById.do',
                    data: {
                        id: id
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        $("#edit-id").val(data.id);
                        $("#edit-owner option[value='" + data.owner + "']").prop("selected", true);
                        $("#edit-source option[value='" + data.source + "']").prop("selected", true);
                        $("#edit-fullname").val(data.fullname);
                        $("#edit-appellation option[value='" + data.appellation + "']").prop("selected", true);
                        $("#edit-job").val(data.job);
                        $("#edit-mphone").val(data.mphone);
                        $("#edit-email").val(data.email);
                        $("#edit-customerName").val(data.customerId);
                        $("#edit-description").val(data.description);
                        $("#edit-contactSummary").val(data.contactSummary);
                        $("#edit-nextContactTime").val(data.nextContactTime);
                        $("#edit-address").val(data.address);
                        $("#editContactsModal").modal("show");
                    }
                });
            });

            $("#saveEditContactsBtn").click(function () {
                //收集参数
                var id = $("#edit-id").val();
                var owner = $("#edit-owner").val();
                var source = $("#edit-source").val();
                var fullname = $.trim($("#edit-fullname").val());
                var appellation = $("#edit-appellation").val();
                var job = $.trim($("#edit-job").val());
                var mphone = $.trim($("#edit-mphone").val());
                var email = $.trim($("#edit-email").val());
                var customerName = $.trim($("#edit-customerName").val());
                var description = $.trim($("#edit-description").val());
                var contactSummary = $.trim($("#edit-contactSummary").val());
                var nextContactTime = $.trim($("#edit-nextContactTime").val());
                var address = $.trim($("#edit-address").val());

                //表单验证
                if (fullname == "") {
                    alert("姓名不能为空")
                    return;
                }
                if (email != "") {
                    var emailRegExp = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
                    if (!emailRegExp.test(email)) {
                        alert("邮箱不合法");
                        return;
                    }
                }
                if (mphone != "") {
                    var mphoneRegExp = /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/;
                    if (!mphoneRegExp.test(mphone)) {
                        alert("手机不合法");
                        return;
                    }
                }
                if (customerName == "") {
                    alert("客户名称不能为空！");
                    return;
                }

                //发送请求
                $.ajax({
                    url: 'workbench/contacts/saveEditContacts.do',
                    data: {
                        id:id,
                        fullname: fullname,
                        appellation: appellation,
                        owner: owner,
                        job: job,
                        email: email,
                        mphone: mphone,
                        source: source,
                        description: description,
                        contactSummary: contactSummary,
                        nextContactTime: nextContactTime,
                        address: address,
                        customerName: customerName
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == "1") {
                            //关闭模态窗口
                            $("#editContactsModal").modal("hide");
                            //刷新联系人列表，显示第一页数据，保持每页显示条数不变(作业)
                            queryContactsByConditionForPage(1, $("#demo_page").bs_pagination("getOption", "rowsPerPage"));
                        } else {
                            //提示信息
                            alert(data.message);
                            //模态窗口不关闭
                            $("#editContactsModal").modal("show");
                        }
                    }
                });
            });
        });

        function queryContactsByConditionForPage(pageNo, pageSize) {
            //收集参数
            var owner = $.trim($("#query-owner").val());
            var fullname = $.trim($("#query-fullname").val());
            var customerId = $.trim($("#query-customerId").val());
            var source = $("#query-source").val();
            $.ajax({
                url: 'workbench/contacts/queryContactsByConditionForPage.do',
                data: {
                    owner: owner,
                    fullname: fullname,
                    customerId: customerId,
                    source: source,
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    var htmlStr = "";
                    $.each(data.contactsList, function (index, obj) {
                        htmlStr += "<tr>";
                        htmlStr += "<td><input type=\"checkbox\" value='" + obj.id + "'/></td>";
                        htmlStr += "<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/contacts/detailContacts.do?id=" + obj.id + "';\">" + (obj.appellation==null?obj.fullname:obj.fullname + obj.appellation)+"</a></td>";
                        htmlStr += "<td>" + obj.customerId + "</td>";
                        htmlStr += "<td>" + obj.owner + "</td>";
                        htmlStr += "<td>" + (obj.source==null?"":obj.source) + "</td>";
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
                            queryContactsByConditionForPage(pageObj.currentPage, pageObj.rowsPerPage);
                        }
                    });
                }
            });
        }
    </script>
</head>
<body>


<!-- 创建联系人的模态窗口 -->
<div class="modal fade" id="createContactsModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" onclick="$('#createContactsModal').modal('hide');">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabelx">创建联系人</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" id="createContactsForm" role="form">

                    <div class="form-group">
                        <label for="create-owner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="create-owner">
                                <c:forEach items="${userList}" var="u">
                                    <option value="${u.id}">${u.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <label for="create-source" class="col-sm-2 control-label">来源</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="create-source">
                                <option></option>
                                <c:forEach items="${sourceList}" var="s">
                                    <option value="${s.id}">${s.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="create-fullname" class="col-sm-2 control-label">姓名<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-fullname">
                        </div>
                        <label for="create-appellation" class="col-sm-2 control-label">称呼</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="create-appellation">
                                <option></option>
                                <c:forEach items="${appellationList}" var="a">
                                    <option value="${a.id}">${a.value}</option>
                                </c:forEach>
                            </select>
                        </div>

                    </div>

                    <div class="form-group">
                        <label for="create-job" class="col-sm-2 control-label">职位</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-job">
                        </div>
                        <label for="create-mphone" class="col-sm-2 control-label">手机</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-mphone">
                        </div>
                    </div>

                    <div class="form-group" style="position: relative;">
                        <label for="create-email" class="col-sm-2 control-label">邮箱</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-email">
                        </div>

                        <label for="create-customerName" class="col-sm-2 control-label">客户名称</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control customerNameTxt" id="create-customerName"
                                   placeholder="支持自动补全，输入客户不存在则新建">
                        </div>
                    </div>

                    <div class="form-group" style="position: relative;">
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
                <button type="button" class="btn btn-primary" id="saveCreateContactsBtn">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 修改联系人的模态窗口 -->
<div class="modal fade" id="editContactsModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel1">修改联系人</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" id="editContactsForm" role="form">

                    <div class="form-group">
                        <label for="edit-owner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="hidden" id="edit-id">
                            <select class="form-control" id="edit-owner">
                                <c:forEach items="${userList}" var="u">
                                    <option value="${u.id}">${u.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <label for="edit-source" class="col-sm-2 control-label">来源</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-source">
                                <option></option>
                                <c:forEach items="${sourceList}" var="s">
                                    <option value="${s.id}">${s.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-fullname" class="col-sm-2 control-label">姓名<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-fullname">
                        </div>
                        <label for="edit-appellation" class="col-sm-2 control-label">称呼</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-appellation">
                                <option></option>
                                <c:forEach items="${appellationList}" var="a">
                                    <option value="${a.id}">${a.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-job" class="col-sm-2 control-label">职位</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-job">
                        </div>
                        <label for="edit-mphone" class="col-sm-2 control-label">手机</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-mphone">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-email" class="col-sm-2 control-label">邮箱</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-email">
                        </div>
                        <label for="edit-customerName" class="col-sm-2 control-label">客户名称</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control customerNameTxt" id="edit-customerName"
                                   placeholder="支持自动补全，输入客户不存在则新建">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="edit-description"></textarea>
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
                                <textarea class="form-control" rows="1" id="edit-address"></textarea>
                            </div>
                        </div>
                    </div>
                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="saveEditContactsBtn">更新</button>
            </div>
        </div>
    </div>
</div>


<div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>联系人列表</h3>
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
                        <div class="input-group-addon">姓名</div>
                        <input class="form-control" type="text" id="query-fullname">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">客户名称</div>
                        <input class="form-control" type="text" id="query-customerId">
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

                <button type="button" id="queryContactsByConditionForPage" class="btn btn-default">查询</button>

            </form>
        </div>
        <div class="btn-toolbar" role="toolbar"
             style="background-color: #F7F7F7; height: 50px; position: relative;top: 10px;">
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-primary" id="createContactsBtn"><span
                        class="glyphicon glyphicon-plus"></span> 创建
                </button>
                <button type="button" class="btn btn-default" id="editContactsBtn"><span
                        class="glyphicon glyphicon-pencil"></span> 修改
                </button>
                <button id="deleteContactsBtn" type="button" class="btn btn-danger"><span
                        class="glyphicon glyphicon-minus"></span> 删除
                </button>
            </div>


        </div>
        <div style="position: relative;top: 20px;">
            <table class="table table-hover">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td><input type="checkbox" id="checkAll"/></td>
                    <td>姓名</td>
                    <td>客户名称</td>
                    <td>所有者</td>
                    <td>来源</td>
                </tr>
                </thead>
                <tbody id="tBody">
                <%--<tr>
                    <td><input type="checkbox"/></td>
                    <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">李四</a>
                    </td>
                    <td>动力节点</td>
                    <td>zhangsan</td>
                    <td>广告</td>
                </tr>--%>
                </tbody>
            </table>

            <div id="demo_page"></div>
        </div>
    </div>

</div>
</body>
</html>