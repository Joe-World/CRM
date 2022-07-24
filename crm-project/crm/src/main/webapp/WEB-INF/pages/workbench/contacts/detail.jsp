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
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

    <script type="text/javascript">

        //默认情况下取消和保存按钮是隐藏的
        var cancelAndSaveBtnDefault = true;

        $(function () {
            $("#remark").focus(function () {
                if (cancelAndSaveBtnDefault) {
                    //设置remarkDiv的高度为130px
                    $("#remarkDiv").css("height", "130px");
                    //显示
                    $("#cancelAndSaveBtn").show("2000");
                    cancelAndSaveBtnDefault = false;
                }
            });

            $("#cancelBtn").click(function () {
                //显示
                $("#cancelAndSaveBtn").hide();
                //设置remarkDiv的高度为130px
                $("#remarkDiv").css("height", "90px");
                cancelAndSaveBtnDefault = true;
            });

            /*$(".remarkDiv").mouseover(function(){
                    $(this).children("div").children("div").show();
                });*/
            $("#remarkDivList").on("mouseover", ".remarkDiv", function () {
                $(this).children("div").children("div").show();
            });


            /*$(".remarkDiv").mouseout(function(){
                $(this).children("div").children("div").hide();
            });*/
            $("#remarkDivList").on("mouseout", ".remarkDiv", function () {
                $(this).children("div").children("div").hide();
            });

            /*$(".myHref").mouseover(function(){
                $(this).children("span").css("color","red");
            });*/
            $("#remarkDivList").on("mouseover", ".myHref", function () {
                $(this).children("span").css("color", "red");
            });

            /*$(".myHref").mouseout(function(){
                $(this).children("span").css("color","#E6E6E6");
            });*/
            $("#remarkDivList").on("mouseout", ".myHref", function () {
                $(this).children("span").css("color", "#E6E6E6");
            });

            //给"保存"按钮添加单击事件
            $("#saveCreateContactsRemarkBtn").click(function () {
                //收集参数
                var noteContent = $.trim($("#remark").val());
                var contactsId = '${contacts.id}';
                //表单验证
                if (noteContent == "") {
                    alert("备注内容不能为空");
                    return;
                }
                //发送请求
                $.ajax({
                    url: 'workbench/contacts/saveCreateContactsRemark.do',
                    data: {
                        noteContent: noteContent,
                        contactsId: contactsId
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == "1") {
                            //清空输入框
                            $("#remark").val("");
                            //刷新备注列表
                            var htmlStr = "";
                            htmlStr += "<div id=\"div_" + data.retData.id + "\" class=\"remarkDiv\" style=\"height: 60px;\">";
                            htmlStr += "<img title=\"${sessionScope.sessionUser.name}\" src=\"image/user-thumbnail.png\" style=\"width: 30px; height:30px;\">";
                            htmlStr += "<div style=\"position: relative; top: -40px; left: 40px;\" >";
                            htmlStr += "<h5 id=\"h5_" + data.retData.id + "\">" + data.retData.noteContent + "</h5>";
                            htmlStr += "<font color=\"gray\">联系人</font> <font color=\"gray\">-</font> <b>${contacts.fullname}${contacts.appellation}-${contacts.customerId}</b> <small style=\"color: gray;\"> " + data.retData.createTime + " 由${sessionScope.sessionUser.name}创建 </small>";
                            htmlStr += "<div style=\"position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;\">";
                            htmlStr += "<a class=\"myHref\" name=\"editA\" remarkId=\"" + data.retData.id + "\" href=\"javascript:void(0);\"><span class=\"glyphicon glyphicon-edit\" style=\"font-size: 20px; color: #E6E6E6;\"></span></a>";
                            htmlStr += "&nbsp;&nbsp;&nbsp;&nbsp;";
                            htmlStr += "<a class=\"myHref\" name=\"deleteA\" remarkId=\"" + data.retData.id + "\" href=\"javascript:void(0);\"><span class=\"glyphicon glyphicon-remove\" style=\"font-size: 20px; color: #E6E6E6;\"></span></a>";
                            htmlStr += "</div>";
                            htmlStr += "</div>";
                            htmlStr += "</div>";
                            $("#remarkDiv").before(htmlStr);
                        } else {
                            //提示信息
                            alert(data.message);
                        }
                    }
                });
            });

            //给所有的”删除”图标添加单击事件
            $("#remarkDivList").on("click", "a[name='deleteA']", function () {
                //收集参数
                var id = $(this).attr("remarkId");
                //发送请求
                $.ajax({
                    url: 'workbench/contacts/deleteContactsRemarkById.do',
                    data: {
                        id: id
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == "1") {
                            //刷新备注列表
                            $("#div_" + id).remove();
                        } else {
                            alert(data.message);
                        }
                    }
                });
            });

            //给所有的“修改”图标添加单击事件
            $("#remarkDivList").on("click", "a[name='editA']", function () {
                //获取备注的id和内容
                var id = $(this).attr("remarkId");
                // var noteContent = $("#h5_" + id).text();
                var noteContent = $("#div_" + id + " h5").text();
                //把数据写到修改备注模态窗口中
                $("#edit-id").val(id);
                $("#edit-noteContent").val(noteContent);
                //弹出修改备注的模态窗口
                $("#editRemarkModal").modal("show");
            });

            $("#updateRemarkBtn").click(function () {
                //收集参数
                var id = $("#edit-id").val()
                var noteContent = $.trim($("#edit-noteContent").val());
                //表单验证
                if (noteContent == "") {
                    alert("备注内容不能为空");
                    return;
                }
                $.ajax({
                    url: 'workbench/contacts/saveEditContactsRemark.do',
                    data: {
                        id: id,
                        noteContent: noteContent
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == "1") {
                            //关闭模态窗口
                            $("#editRemarkModal").modal("hide");
                            //刷新备注列表
                            $("#div_" + data.retData.id + " h5").text(data.retData.noteContent);
                            $("#div_" + data.retData.id + " small").text(" " + data.retData.editTime + " 由${sessionScope.sessionUser.name}修改");
                        } else {
                            //提示信息
                            alert(data.message);
                            //模态窗口不关闭
                            $("#editRemarkModal").modal("show");
                        }
                    }
                });
            });


            //给“关联市场活动“按钮添加单击事件”
            $("#bundActivityBtn").click(function () {
                //初始化工作
                /*$("#searchActivityTxt").val("");
                $("#tBody").html("");*/
                if ($("#activityListTBody input[type='checkbox']").size() == 0) {
                    $("#checkAll").prop("checked", false);
                }

                //弹出“关联市场活动“模态窗口
                $("#bundModal").modal("show");
            });

            //给市场活动搜索框添加value改变事件
            $("#searchActivityTxt").on("input", function () {
                queryActivityByContactsIdActivityName();
            });

            //给”关联“按钮添加单击事件
            $("#saveBundActivityBtn").click(function () {
                //收集参数
                //获取列表中所有被选中的checkbox
                var checkedIds = $("#activityListTBody input[type='checkbox']:checked")
                //表单验证
                if (checkedIds.size() == 0) {
                    alert("请选择要关联的市场活动");
                    return;
                }
                var ids = "";
                $.each(checkedIds, function (index, obj) {
                    ids += "activityId=" + this.value + "&";
                });
                ids += "contactsId=${contacts.id}";
                $.ajax({
                    url: 'workbench/contacts/saveBund.do',
                    data: ids,
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == "1") {
                            //在模态窗口中remove掉已经被关联的市场活动
                            $.each(checkedIds, function (index, obj) {
                                $("#tr_" + this.value).remove();
                            });

                            //关闭模态窗口
                            $("#bundModal").modal("hide");
                            //刷新列表
                            var htmlStr = "";
                            $.each(data.retData, function (index, obj) {
                                htmlStr += "<tr id='tr_" + obj.id + "'>";
                                htmlStr += "<td>" + obj.name + "</td>";
                                htmlStr += "<td>" + obj.startDate + "</td>";
                                htmlStr += "<td>" + obj.endDate + "</td>";
                                htmlStr += "<td>" + obj.owner + "</td>";
                                htmlStr += "<td><a href=\"javascript:void(0);\" activityId=\"" + obj.id + "\"  style=\"text-decoration: none;\"><span class=\"glyphicon glyphicon-remove\"></span>解除关联</a></td>";
                                htmlStr += "</tr>";
                            });
                            $("#relatedActivityListTBody").append(htmlStr);
                        } else {
                            alert(data.message);
                            $("#bundModal").modal("show");
                        }
                    }
                });
            });

            //给所有的”解除关联“按钮添加单击事件
            $("#relatedActivityListTBody").on("click", "a", function () {
                var contactsId = "${contacts.id}";
                //自定义属性只能attr
                var activityId = $(this).attr("activityId");

                if (window.confirm("确定解除关联关系？")) {
                    $.ajax({
                            url: 'workbench/contacts/saveUnbund.do',
                            data: {
                                contactsId: contactsId,
                                activityId: activityId
                            },
                            type: 'post',
                            dataType: 'json',
                            success: function (data) {
                                if (data.code == "1") {
                                    //刷新已经关联的市场活动列表
                                    $("#relatedActivityListTBody tr[id='tr_" + activityId + "']").remove();
                                    queryActivityByContactsIdActivityName();
                                } else {
                                    alert(data.message);
                                }
                            }
                        }
                    );
                }
            });

            //给全选按钮添加单击事件
            $("#checkAll").click(function () {
                $("#activityListTBody input[type='checkbox']").prop("checked", this.checked);
            });

            $("#activityListTBody").on("click", "input[type='checkbox']", function () {
                //如果列表中的所有checkbox都选中，则”全选“按钮也选中
                if ($("#activityListTBody input[type='checkbox']").size() == $("#activityListTBody input[type='checkbox']:checked").size()) {
                    $("#checkAll").prop("checked", true);
                } else {
                    //如果列表中所有的checkbox至少有一个没选中，则”全选“按钮也取消
                    $("#checkAll").prop("checked", false);
                }
            });

            $("#createTranBtn").click(function () {
                window.location.href = "workbench/contacts/toSave.do?contactsId=${contacts.id}&fullname=${contacts.fullname}&appellation=${contacts.appellation}";
            });

            $("#deleteTranBtn").click(function () {
                var tranId = $(this).attr("tranId");
                if (window.confirm("确定要删除吗？")) {
                    $.ajax({
                        url: 'workbench/tran/deleteById.do',
                        data: {
                            id:tranId
                        },
                        type: 'post',
                        dateType: 'json',
                        success: function (data) {
                            if (data.code == "1") {
                                $("#tr_" + tranId + "").remove();
                            } else {
                                //提示信息
                                alert(data.message);
                            }
                        }
                    });
                }
            });
        });

        function queryActivityByContactsIdActivityName() {
            //收集参数
            var activityName = $("#searchActivityTxt").val();
            var contactsId = "${contacts.id}";
            //发送请求
            $.ajax({
                url: 'workbench/contacts/queryActivityForDetailByNameContactsId.do',
                data: {
                    activityName: activityName,
                    contactsId: contactsId
                },
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    //遍历data，显示搜索到的市场活动列表
                    var htmlStr = "";
                    $.each(data, function (index, obj) {
                        htmlStr += "<tr id='tr_" + obj.id + "'>";
                        htmlStr += "<td><input type=\"checkbox\" value='" + obj.id + "'/></td>";
                        htmlStr += "<td>" + obj.name + "</td>";
                        htmlStr += "<td>" + obj.startDate + "</td>";
                        htmlStr += "<td>" + obj.endDate + "</td>";
                        htmlStr += "<td>" + obj.owner + "</td>";
                        htmlStr += "</tr>";
                    });
                    $("#activityListTBody").html(htmlStr);
                }
            });
        }
    </script>

</head>
<body>
<!-- 修改联系人备注的模态窗口 -->
<div class="modal fade" id="editRemarkModal" role="dialog">
    <!-- 备注的id -->
    <input type="hidden" id="remarkId">
    <div class="modal-dialog" role="document" style="width: 40%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">修改备注</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" role="form">
                    <div class="form-group">
                        <input type="hidden" id="edit-id">
                        <label for="edit-noteContent" class="col-sm-2 control-label">内容</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="edit-noteContent"></textarea>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="updateRemarkBtn">更新</button>
            </div>
        </div>
    </div>
</div>


<!-- 联系人和市场活动关联的模态窗口 -->
<div class="modal fade" id="bundModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 80%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title">关联市场活动</h4>
            </div>
            <div class="modal-body">
                <div class="btn-group" style="position: relative; top: 18%; left: 8px;">
                    <form class="form-inline" role="form">
                        <div class="form-group has-feedback">
                            <input id="searchActivityTxt" type="text" class="form-control" style="width: 300px;"
                                   placeholder="请输入市场活动名称，支持模糊查询">
                            <span class="glyphicon glyphicon-search form-control-feedback"></span>
                        </div>
                    </form>
                </div>
                <table id="activityTable2" class="table table-hover"
                       style="width: 900px; position: relative;top: 10px;">
                    <thead>
                    <tr style="color: #B3B3B3;">
                        <td><input type="checkbox" id="checkAll"/></td>
                        <td>名称</td>
                        <td>开始日期</td>
                        <td>结束日期</td>
                        <td>所有者</td>
                        <td></td>
                    </tr>
                    </thead>
                    <tbody id="activityListTBody">
                    <%--<tr>
                        <td><input type="checkbox"/></td>
                        <td>发传单</td>
                        <td>2020-10-10</td>
                        <td>2020-10-20</td>
                        <td>zhangsan</td>
                    </tr>--%>
                    </tbody>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" id="saveBundActivityBtn">关联</button>
            </div>
        </div>
    </div>
</div>

<!-- 返回按钮 -->
<div style="position: relative; top: 35px; left: 10px;">
    <a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left"
                                                                         style="font-size: 20px; color: #DDDDDD"></span></a>
</div>

<!-- 大标题 -->
<div style="position: relative; left: 40px; top: -30px;">
    <div class="page-header">
        <h3>${contacts.fullname}${contacts.appellation} <small> - ${contacts.customerId}</small></h3>
    </div>
</div>

<br/>
<br/>
<br/>

<!-- 详细信息 -->
<div style="position: relative; top: -70px;">
    <div style="position: relative; left: 40px; height: 30px;">
        <div style="width: 300px; color: gray;">所有者</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${contacts.owner}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">来源</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${contacts.source}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 10px;">
        <div style="width: 300px; color: gray;">客户名称</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${contacts.customerId}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">姓名</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;">
            <b>${contacts.fullname}${contacts.appellation}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 20px;">
        <div style="width: 300px; color: gray;">邮箱</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${contacts.email}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">手机</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${contacts.mphone}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 30px;">
        <div style="width: 300px; color: gray;">职位</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${contacts.job}</b></div>
        <div style="visibility: hidden;width: 300px;position: relative; left: 450px; top: -40px; color: gray;">&nbsp;
        </div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>&nbsp;</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="visibility: hidden;height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 40px;">
        <div style="width: 300px; color: gray;">创建者</div>
        <div style="width: 500px;position: relative; left: 200px; top: -20px;">
            <b>${contacts.createBy}&nbsp;&nbsp;</b><small
                style="font-size: 10px; color: gray;">${contacts.createTime}</small></div>
        <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 50px;">
        <div style="width: 300px; color: gray;">修改者</div>
        <div style="width: 500px;position: relative; left: 200px; top: -20px;">
            <b>${contacts.editBy}&nbsp;&nbsp;</b><small
                style="font-size: 10px; color: gray;">${contacts.editTime}</small></div>
        <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 60px;">
        <div style="width: 300px; color: gray;">描述</div>
        <div style="width: 630px;position: relative; left: 200px; top: -20px;">
            <b>
                ${contacts.description}
            </b>
        </div>
        <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 70px;">
        <div style="width: 300px; color: gray;">联系纪要</div>
        <div style="width: 630px;position: relative; left: 200px; top: -20px;">
            <b>
                &nbsp;${contacts.contactSummary}
            </b>
        </div>
        <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 80px;">
        <div style="width: 300px; color: gray;">下次联系时间</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>&nbsp;${contacts.nextContactTime}</b>
        </div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 90px;">
        <div style="width: 300px; color: gray;">详细地址</div>
        <div style="width: 630px;position: relative; left: 200px; top: -20px;">
            <b>
                ${contacts.address}
            </b>
        </div>
        <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
</div>
<!-- 备注 -->
<div id="remarkDivList" style="position: relative; top: 20px; left: 40px;">
    <div class="page-header">
        <h4>备注</h4>
    </div>

    <c:forEach items="${remarkList}" var="r">
        <div id="div_${r.id}" class="remarkDiv" style="height: 60px;">
            <img title="${r.createBy}" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
            <div style="position: relative; top: -40px; left: 40px;">
                <h5>${r.noteContent}</h5>
                <font color="gray">联系人</font> <font color="gray">-</font> <b>${contacts.fullname}${contacts.appellation}-${contacts.customerId}</b>
                <small
                        style="color: gray;"> ${r.editFlag=='1'?r.editTime:r.createTime}由${r.editFlag=='1'?r.editBy:r.createBy}${r.editFlag=='1'?'修改':'创建'}</small>
                <div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
                    <a class="myHref" name="editA" remarkId="${r.id}" href="javascript:void(0);"><span
                            class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <a class="myHref" name="deleteA" remarkId="${r.id}" href="javascript:void(0);"><span
                            class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
                </div>
            </div>
        </div>
    </c:forEach>

    <!-- 备注1 -->
    <%--<div class="remarkDiv" style="height: 60px;">
        <img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
        <div style="position: relative; top: -40px; left: 40px;" >
            <h5>哎呦！</h5>
            <font color="gray">联系人</font> <font color="gray">-</font> <b>李四先生-北京动力节点</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
            <div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
                <a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
            </div>
        </div>
    </div>--%>

    <div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
        <form role="form" style="position: relative;top: 10px; left: 10px;">
            <textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"
                      placeholder="添加备注..."></textarea>
            <p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
                <button id="cancelBtn" type="button" class="btn btn-default">取消</button>
                <button id="saveCreateContactsRemarkBtn" type="button" class="btn btn-primary">保存</button>
            </p>
        </form>
    </div>
</div>

<!-- 交易 -->
<div>
    <div style="position: relative; top: 20px; left: 40px;">
        <div class="page-header">
            <h4>交易</h4>
        </div>
        <div style="position: relative;top: 0px;">
            <table id="activityTable3" class="table table-hover" style="width: 900px;">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td>名称</td>
                    <td>金额</td>
                    <td>阶段</td>
                    <td>可能性</td>
                    <td>预计成交日期</td>
                    <td>类型</td>
                    <td></td>
                </tr>
                </thead>
                <tbody id="tranListTBody">
                <c:forEach items="${tranList}" var="tran">
                    <tr id="tr_${tran.id}">
                        <td><a href="workbench/transaction/detailTran.do?tranId=${tran.id}"
                               style="text-decoration: none;">${tran.name}</a></td>
                        <td>${tran.money}</td>
                        <td>${tran.stage}</td>
                        <td>${tran.possibility}</td>
                        <td>${tran.expectedDate}</td>
                        <td>${tran.type}</td>
                        <td><a href="javascript:void(0);" id="deleteTranBtn" tranId="${tran.id}"
                               style="text-decoration: none;"><span class="glyphicon glyphicon-remove"></span>删除</a>
                        </td>
                    </tr>
                </c:forEach>

                <%--<tr>
                    <td><a href="../transaction/detail.jsp" style="text-decoration: none;">动力节点-交易01</a></td>
                    <td>5,000</td>
                    <td>谈判/复审</td>
                    <td>90</td>
                    <td>2017-02-07</td>
                    <td>新业务</td>
                    <td><a href="javascript:void(0);" data-toggle="modal" data-target="#unbundModal" style="text-decoration: none;"><span class="glyphicon glyphicon-remove"></span>删除</a></td>
                </tr>--%>
                </tbody>
            </table>
        </div>

        <div>
            <a id="createTranBtn" href="javascript:void(0);" style="text-decoration: none;"><span class="glyphicon glyphicon-plus"></span>新建交易</a>
        </div>
    </div>
</div>

<!-- 市场活动 -->
<div>
    <div style="position: relative; top: 60px; left: 40px;">
        <div class="page-header">
            <h4>市场活动</h4>
        </div>
        <div style="position: relative;top: 0px;">
            <table id="activityTable" class="table table-hover" style="width: 900px;">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td>名称</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                    <td>所有者</td>
                    <td></td>
                </tr>
                </thead>
                <tbody id="relatedActivityListTBody">
                <c:forEach items="${activityList}" var="activity">
                    <tr id="tr_${activity.id}">
                        <td>${activity.name}</td>
                        <td>${activity.startDate}</td>
                        <td>${activity.endDate}</td>
                        <td>${activity.owner}</td>
                        <td><a href="javascript:void(0);" activityId="${activity.id}"
                               style="text-decoration: none;"><span
                                class="glyphicon glyphicon-remove"></span>解除关联</a></td>
                    </tr>
                </c:forEach>

                <%--<tr>
                    <td><a href="../activity/detail.jsp" style="text-decoration: none;">发传单</a></td>
                    <td>2020-10-10</td>
                    <td>2020-10-20</td>
                    <td>zhangsan</td>
                    <td><a href="javascript:void(0);" data-toggle="modal" data-target="#unbundActivityModal" style="text-decoration: none;"><span class="glyphicon glyphicon-remove"></span>解除关联</a></td>
                </tr>--%>
                </tbody>
            </table>
        </div>

        <div>
            <a href="javascript:void(0);" id="bundActivityBtn" style="text-decoration: none;"><span class="glyphicon glyphicon-plus"></span>关联市场活动</a>
        </div>
    </div>
</div>


<div style="height: 200px;"></div>
</body>
</html>