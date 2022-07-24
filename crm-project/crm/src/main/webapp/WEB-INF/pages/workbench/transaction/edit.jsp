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

	<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
	<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript"
			src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
	<script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>

	<script type="text/javascript">
		$(function () {
			if ("${id}" != "") {
				$("#contactsId").val("${id}");
				$("#edit-contacts").val("${name}");

				$("#searchContactsBtn").remove();
			}

			$("#searchActivityBtn").click(function () {
				$("#searchActivityModal").modal("show");
			});

			$("#searchActivityTxt").keyup(function () {
				var activityName = this.value;
				$.ajax({
					url: 'workbench/transaction/queryActivityForSaveByName.do',
					data: {
						activityName: activityName
					},
					type: 'post',
					dataType: 'json',
					success: function (data) {
						var htmlStr = "";
						$.each(data, function (index, obj) {
							htmlStr += "<tr>";
							htmlStr += "<td><input value='" + obj.id + "' activityName='" + obj.name + "' type=\"radio\" name=\"activity\"/></td>";
							htmlStr += "<td>" + obj.name + "</td>";
							htmlStr += "<td>" + obj.startDate + "</td>";
							htmlStr += "<td>" + obj.endDate + "</td>";
							htmlStr += "<td>" + obj.owner + "</td>";
							htmlStr += "</tr>";
						});
						$("#searchActivityTBody").html(htmlStr);
					}
				});
			});

			$("#searchActivityTBody").on("click", " input[type='radio']", function () {
				var activityName = $(this).attr("activityName");

				$("#edit-activity").val(activityName);
				$("#activityId").val(this.value);

				$("#searchActivityModal").modal("hide");
			});


			$("#searchContactsBtn").click(function () {
				$("#searchContactsModal").modal("show");
			});

			$("#searchContactsTxt").keyup(function () {
				var contactsName = this.value;
				$.ajax({
					url: 'workbench/transaction/queryContactsForSaveByName.do',
					data: {
						contactsName: contactsName
					},
					type: 'post',
					dataType: 'json',
					success: function (data) {
						var htmlStr = "";
						$.each(data, function (index, obj) {
							htmlStr += "<tr>";
							htmlStr += "<td><input value='" + obj.id + "' contactsName='" + (obj.appellation == null ? obj.fullname : obj.fullname + obj.appellation) + "' type=\"radio\" name=\"contacts\"/></td>";
							htmlStr += "<td>" + (obj.appellation == null ? obj.fullname : obj.fullname + obj.appellation) + "</td>";
							htmlStr += "<td>" + obj.email + "</td>";
							htmlStr += "<td>" + obj.mphone + "</td>";
							htmlStr += "</tr>";
						});
						$("#searchContactsTBody").html(htmlStr);
					}
				});
			});

			$("#searchContactsTBody").on("click", " input[type='radio']", function () {
				var contactsName = $(this).attr("contactsName");

				$("#edit-contacts").val(contactsName);
				$("#contactsId").val(this.value);

				$("#searchContactsModal").modal("hide");
			});

			//给“阶段”列表添加change事件
			$("#edit-transactionStage").change(function () {
				//收集参数
				// $(this).find("option:selected").text();
				var stageValue = $("#edit-transactionStage option:selected").text();
				//表单验证
				if (stageValue == "") {
					$("#edit-possibility").val("");
					return;
				}
				$.ajax({
					url: 'workbench/transaction/queryPossibilityByStageValue.do',
					data: {
						stageValue: stageValue
					},
					type: 'post',
					dataType: 'json',
					success: function (data) {
						//把可能性显示到输入框中
						$("#edit-possibility").val(data);
					}
				});
			});

			//当容器加载完成之后，对容器调用工具函数
			$("#edit-accountName").typeahead({
				source: function (jquery, process) {//每次键盘弹起，都自动触发本函数；我们可以向后台送请求，查询客户表中所有的名称，把客户名称以[]字符串形式返回前台，赋值给source
					//process：是个函数，能够将['xxx','xxxxx','xxxxxx',.....]字符串赋值给source，从而完成自动补全
					//jquery：在容器中输入的关键字 //var customerName=$("#customerName").val();
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

			$("#saveEditTranBtn").click(function () {
				//收集参数
				var owner = $("#edit-transactionOwner").val();
				var money = $.trim($("#edit-amountOfMoney").val());
				var name = $.trim($("#edit-transactionName").val());
				var expectedDate = $("#edit-expectedClosingDate").val();
				var customerName = $.trim($("#edit-accountName").val());
				var stage = $("#edit-transactionStage").val();
				var type = $("#edit-transactionType").val();
				var source = $("#edit-clueSource").val();
				var activityId = $("#activityId").val();
				var contactsId = $("#contactsId").val();
				var description = $.trim($("#edit-describe").val());
				var contactSummary = $.trim($("#edit-contactSummary").val());
				var nextContactTime = $("#edit-nextContactTime").val();
				//表单验证
				if (money != "") {
					var regExp = /^(([1-9]\d*)|0)$/;
					if (!regExp.test(money)) {
						alert("金额只能是非负整数");
						return;
					}
				}
				if (name == "") {
					alert("名称不能为空");
					return;
				}
				if (expectedDate == "") {
					alert("预计成交日期不能为空");
					return;
				}
				if (customerName == "") {
					alert("客户名称不能为空");
					return;
				}
				if (stage == "") {
					alert("阶段不能为空");
					return;
				}


				//发送请求
				$.ajax({
					url: 'workbench/transaction/saveEditTran.do',
					data: {
						owner: owner,
						money: money,
						name: name,
						expectedDate: expectedDate,
						customerName: customerName,
						stage: stage,
						type: type,
						source: source,
						activityId: activityId,
						contactsId: contactsId,
						description: description,
						contactSummary: contactSummary,
						nextContactTime: nextContactTime
					},
					type: 'post',
					dataType: 'json',
					success: function (data) {
						if (data.code == "1") {
							//跳转到交易主页面
							window.location.href = "workbench/transaction/index.do";
						} else {
							//提示信息
							alert(data.message);
						}
					}
				});
			});
		});
	</script>
</head>
<body>

<!-- 查找市场活动 -->
<div class="modal fade" id="searchActivityModal" role="dialog">
	<div class="modal-dialog" role="document" style="width: 80%;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span>
				</button>
				<h4 class="modal-title">查找市场活动</h4>
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
				<table id="activityTable3" class="table table-hover"
					   style="width: 900px; position: relative;top: 10px;">
					<thead>
					<tr style="color: #B3B3B3;">
						<td></td>
						<td>名称</td>
						<td>开始日期</td>
						<td>结束日期</td>
						<td>所有者</td>
					</tr>
					</thead>
					<tbody id="searchActivityTBody">
					<%--<tr>
                        <td><input type="radio" name="activity"/></td>
                        <td>发传单</td>
                        <td>2020-10-10</td>
                        <td>2020-10-20</td>
                        <td>zhangsan</td>
                    </tr>--%>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>

<!-- 查找联系人 -->
<div class="modal fade" id="searchContactsModal" role="dialog">
	<div class="modal-dialog" role="document" style="width: 80%;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span>
				</button>
				<h4 class="modal-title">查找联系人</h4>
			</div>
			<div class="modal-body">
				<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
					<form class="form-inline" role="form">
						<div class="form-group has-feedback">
							<input id="searchContactsTxt" type="text" class="form-control" style="width: 300px;"
								   placeholder="请输入联系人名称，支持模糊查询">
							<span class="glyphicon glyphicon-search form-control-feedback"></span>
						</div>
					</form>
				</div>
				<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
					<thead>
					<tr style="color: #B3B3B3;">
						<td></td>
						<td>名称</td>
						<td>邮箱</td>
						<td>手机</td>
					</tr>
					</thead>
					<tbody id="searchContactsTBody">
					<%--<tr>
                        <td><input type="radio" name="activity"/></td>
                        <td>李四</td>
                        <td>lisi@bjpowernode.com</td>
                        <td>12345678901</td>
                    </tr>--%>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>


<div style="position:  relative; left: 30px;">
	<h3>修改交易</h3>
	<div style="position: relative; top: -40px; left: 70%;">
		<button id="saveEditTranBtn" type="button" class="btn btn-primary">保存</button>
		<button type="button" class="btn btn-default" onclick="window.history.back();">取消</button>
	</div>
	<hr style="position: relative; top: -40px;">
</div>
<form class="form-horizontal" role="form" style="position: relative; top: -30px;">
	<div class="form-group">
		<label for="edit-transactionOwner" class="col-sm-2 control-label">所有者<span
				style="font-size: 15px; color: red;">*</span></label>
		<div class="col-sm-10" style="width: 300px;">
			<select class="form-control" id="edit-transactionOwner">
				<c:forEach items="${userList}" var="u">
					<option value="${u.id}">${u.name}</option>
				</c:forEach>
			</select>
		</div>
		<label for="edit-amountOfMoney" class="col-sm-2 control-label">金额</label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-amountOfMoney">
		</div>
	</div>

	<div class="form-group">
		<label for="edit-transactionName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-transactionName">
		</div>
		<label for="edit-expectedClosingDate" class="col-sm-2 control-label">预计成交日期<span
				style="font-size: 15px; color: red;">*</span></label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-expectedClosingDate">
		</div>
	</div>

	<div class="form-group">
		<label for="edit-accountName" class="col-sm-2 control-label">客户名称<span
				style="font-size: 15px; color: red;">*</span></label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-accountName" placeholder="支持自动补全，输入客户不存在则新建">
		</div>
		<label for="edit-transactionStage" class="col-sm-2 control-label">阶段<span
				style="font-size: 15px; color: red;">*</span></label>
		<div class="col-sm-10" style="width: 300px;">
			<select class="form-control" id="edit-transactionStage">
				<option></option>
				<c:forEach items="${stageList}" var="s">
					<option value="${s.id}">${s.value}</option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="form-group">
		<label for="edit-transactionType" class="col-sm-2 control-label">类型</label>
		<div class="col-sm-10" style="width: 300px;">
			<select class="form-control" id="edit-transactionType">
				<option></option>
				<c:forEach items="${transactionTypeList}" var="t">
					<option value="${t.id}">${t.value}</option>
				</c:forEach>
			</select>
		</div>
		<label for="edit-possibility" class="col-sm-2 control-label">可能性</label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-possibility" readonly>
		</div>
	</div>

	<div class="form-group">
		<label for="edit-clueSource" class="col-sm-2 control-label">来源</label>
		<div class="col-sm-10" style="width: 300px;">
			<select class="form-control" id="edit-clueSource">
				<option></option>
				<c:forEach items="${sourceList}" var="s">
					<option value="${s.id}">${s.value}</option>
				</c:forEach>
			</select>
		</div>
		<label for="edit-activity" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);"
																						id="searchActivityBtn"><span
				class="glyphicon glyphicon-search"></span></a></label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="hidden" id="activityId">
			<input type="text" class="form-control" id="edit-activity" readonly>
		</div>
	</div>

	<div class="form-group">
		<label for="edit-contacts" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;
			<a href="javascript:void(0);" id="searchContactsBtn"><span
					class="glyphicon glyphicon-search"></span></a></label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="hidden" id="contactsId">
			<input type="text" class="form-control" id="edit-contacts" readonly>
		</div>
	</div>

	<div class="form-group">
		<label for="edit-description" class="col-sm-2 control-label">描述</label>
		<div class="col-sm-10" style="width: 70%;">
			<textarea class="form-control" rows="3" id="edit-description" value="${tran.description}"></textarea>
		</div>
	</div>

	<div class="form-group">
		<label for="edit-contactSummary" class="col-sm-2 control-label">联系纪要</label>
		<div class="col-sm-10" style="width: 70%;">
			<textarea class="form-control" rows="3" id="edit-contactSummary" value="${tran.contactSummary}></textarea>
		</div>
	</div>

	<div class="form-group">
		<label for="edit-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-nextContactTime" value="${tran.nextContactTime}">
		</div>
	</div>

</form>
</body>
</html>