<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script>
        //检索数据集
        var dataList = {
            "review_content": null,
            "review_createDate": null,
            "orderBy": null,
            "isDesc": true
        };
        $(function () {
            //点击查询按钮时
            $("#btn_review_submit").click(function () {
                var review_name = $.trim($("#input_review_name").val());
                var review_content = $.trim($("#input_review_content").val());
                var review_userName = $.trim($("#input_review_userName").val());
                var review_createDate = $("#select_review_createDate").val();
                //封装数据
                dataList.review_name = encodeURI(review_name);
                dataList.review_content = encodeURI(review_content);
                dataList.review_userName = encodeURI(review_userName);
                dataList.review_createDate = review_createDate;
                getData($(this), "admin/review/0/10", dataList);
            });
            //点击刷新按钮时
            $("#btn_review_refresh").click(function () {
                //清除数据
                dataList.review_name = null;
                dataList.review_content = null;
                dataList.review_userName = null;
                dataList.review_createDate = null;
                dataList.orderBy = null;
                dataList.isDesc = true;
                //获取数据
                getData($(this), "admin/review/0/10", null);
                //清除排序样式
                var table = $("#table_review_list");
                table.find("span.orderByDesc,span.orderByAsc").css("opacity","0");
                table.find("th.data_info").attr("data-sort","asc");
            });
            //点击th排序时
            $("th.data_info").click(function () {
                var table = $("#table_review_list");
                if(table.find(">tbody>tr").length <= 1){
                    return;
                }
                //获取排序字段
                dataList.orderBy = $(this).attr("data-name");
                //是否倒序排序
                dataList.isDesc = $(this).attr("data-sort")==="asc";

                getData($(this), "admin/review/0/10", dataList);
                //设置排序
                table.find("span.orderByDesc,span.orderByAsc").css("opacity","0");
                if(dataList.isDesc){
                    $(this).attr("data-sort","desc").children(".orderByAsc.orderBySelect").removeClass("orderBySelect").css("opacity","1");
                    $(this).children(".orderByDesc").addClass("orderBySelect").css("opacity","1");
                } else {
                    $(this).attr("data-sort","asc").children(".orderByDesc.orderBySelect").removeClass("orderBySelect").css("opacity","1");
                    $(this).children(".orderByAsc").addClass("orderBySelect").css("opacity","1");
                }
            });
            //点击table中的数据时
            $("#table_review_list").find(">tbody>tr").click(function () {
                trDataStyle($(this));
            });
        });
        //获取评论数据
        function getData(object,url,dataObject) {
            var table = $("#table_review_list");
            var tbody = table.children("tbody").first();
            $.ajax({
                url: url,
                type: "get",
                data: dataObject,
                traditional: true,
                success: function (data) {
                    //清空原有数据
                    tbody.empty();
                    //设置样式
                    $(".loader").css("display","none");
                    object.attr("disabled",false);
                    //显示评论统计数据
                    $("#review_count_data").text(data.reviewCount);
                    if (data.reviewList.length > 0) {
                        for (var i = 0; i < data.reviewList.length; i++) {
                            var review_id = data.reviewList[i].review_id;
                            var review_content = data.reviewList[i].review_content;
                            var review_createDate = data.reviewList[i].review_createDate;
                            var user_id = data.reviewList[i].review_user.user_name;
                            var product_id = data.reviewList[i].review_product.product_name;
                            //显示评论数据
                            let centent="<tr><td><input type='checkbox' class='cbx_select' id='cbx_review_select_" + review_id + "'><label for='cbx_review_select_" + review_id + "'></label></td><td title='"+product_id+"'>" + product_id + "</td><td title='"+review_content+"'>" + review_content + "</td><td title='"+user_id+"'>" + user_id + "</td><td title='"+review_createDate+"'>" + review_createDate + "</td>" +
                                "<td><span class='td_special' title='查看评论详情'><a href='javascript:void(0);' onclick='getChildPage(this)'>详情</a></span>"+"&nbsp;&nbsp;<span class='td_special' title='删除评论'><a href='javascript:void(0);' onclick='delChildPage(this)'>删除</a></span>";
                            centent+="</td><td hidden><span class='review_id'>" + review_id + "</span></td></tr>";
                            tbody.append(centent);
                        }
                        //绑定事件
                        tbody.children("tr").click(function () {
                            trDataStyle($(this));
                        });
                        //分页
                        var pageUtil = {
                            index: data.pageUtil.index,
                            count: data.pageUtil.count,
                            total: data.pageUtil.total,
                            totalPage: data.totalPage
                        };
                        createPageDiv($(".loader"), pageUtil);
                    }
                },
                beforeSend: function () {
                    $(".loader").css("display","block");
                    object.attr("disabled",true);
                },
                error: function () {

                }
            });
        }

        //获取评论子界面
        function getChildPage(obj) {
            var url;
            var title;
            if(obj === null){
                title = "添加评论";
                url = "review/new";
            } else {
                title = "评论详情";
                url = "review/"+$(obj).parents("tr").find(".review_id").text();
            }

            //设置样式
            $("#div_home_title").children("span").text(title);
            document.title = "Tmall管理后台 - "+title;
            //ajax请求页面
            ajaxUtil.getPage(url,null,true);
        }

        //删除评论子界面
        function delChildPage(obj) {
            let url = "admin/review/del/" + $(obj).parents("tr").find(".review_id").text();
            $(".modal-body").text("您确定要删除该评论吗？");
            $('#modalDiv').modal();
            $("#btn-ok").unbind("click").click(function () {
                $.ajax({
                    url: url,
                    type: "get",
                    traditional: true,
                    success: function (data) {
                        if (data.success) {
                            $('#modalDiv').modal("hide");
                            //清除数据
                            dataList.review_name = null;
                            dataList.review_content = null;
                            dataList.review_userName = null;
                            dataList.review_createDate = null;
                            dataList.orderBy = null;
                            dataList.isDesc = true;
                            //获取数据
                            getData($(this), "admin/review/0/10", null);
                            //清除排序样式
                            var table = $("#table_review_list");
                            table.find("span.orderByDesc,span.orderByAsc").css("opacity","0");
                            table.find("th.data_info").attr("data-sort","asc");
                        } else {
                            $(".modal-body").text("删除失败！");
                        }
                    }
                });
            });
        }
        //获取页码数据
        function getPage(index) {
            getData($(this), "admin/review/" + index + "/10", dataList);
        }
    </script>
    <style rel="stylesheet">
        #text_cut{
            position: relative;
            right: 10px;
            color: #ccc;
        }
        #lbl_review_isEnabled_special{
            margin-right: 20px;
        }
    </style>
</head>
<body>
<div class="frm_div text_info">
    <div class="frm_group">
        <label class="frm_label" id="lbl_review_name" for="input_review_content">评论产品</label>
        <input class="frm_input" id="input_review_name" type="text" maxlength="50"/>
        <label class="frm_label" id="lbl_review_content" for="input_review_content">评论内容</label>
        <input class="frm_input" id="input_review_content" type="text" maxlength="50"/>
        <label class="frm_label" id="lbl_review_userName" for="input_review_content">评论人</label>
        <input class="frm_input" id="input_review_userName" type="text" maxlength="50"/>
       <label class="frm_label" id="lbl_review_category_id" for="select_review_createDate">评论时间</label>
        <input class="frm_input" type="date" id="select_review_createDate" data-size="8">
        <input class="frm_btn" id="btn_review_submit" type="button" value="查询"/>
        <input class="frm_btn frm_clear" id="btn_clear" type="button" value="重置"/>
    </div>
    <div class="frm_group_last">
        <input class="frm_btn frm_refresh" id="btn_review_refresh" type="button" value="刷新评论列表"/>
        <span class="frm_error_msg" id="text_tools_msg"></span>
    </div>
</div>
<div class="data_count_div text_info">
    <svg class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2522" width="16" height="16">
            <path d="M401.976676 735.74897c-88.721671 0-172.124196-34.635845-234.843656-97.526197-62.724577-62.86784-97.271394-146.453537-97.271394-235.358379s34.546817-172.490539 97.276511-235.361449c62.715367-62.887282 146.117892-97.522104 234.838539-97.522104 88.719624 0 172.135452 34.633798 234.881518 97.522104 62.704111 62.875003 97.235578 146.4607 97.235578 235.361449 0 88.901773-34.530444 172.487469-97.231485 235.358379C574.112128 701.116195 490.6963 735.74897 401.976676 735.74897zM401.976676 121.204479c-75.012438 0-145.533584 29.290093-198.572568 82.474386-109.585861 109.834524-109.585861 288.539602-0.004093 398.36901 53.043077 53.188386 123.564223 82.47848 198.577684 82.47848 75.015507 0 145.553027-29.291117 198.620663-82.47848C710.126918 492.220514 710.126918 313.511343 600.593246 203.678866 547.530726 150.496619 476.992183 121.204479 401.976676 121.204479z" p-id="2523" fill="#FF7874">
            </path>
            <path d="M932.538427 958.228017c-6.565533 0-13.129019-2.508123-18.132986-7.52437L606.670661 642.206504c-9.989515-10.014074-9.969049-26.231431 0.045025-36.220946s26.230408-9.969049 36.220946 0.045025l307.73478 308.497143c9.989515 10.014074 9.969049 26.231431-0.045025 36.220946C945.627537 955.735244 939.081447 958.228017 932.538427 958.228017z" p-id="2524" fill="#FF7874">
            </path>
        </svg>
    <span class="data_count_title">查看合计</span>
    <span>评论总数:</span>
    <span class="data_count_value" id="review_count_data">${requestScope.reviewCount}</span>
    <span class="data_count_unit">条</span>
</div>
<div class="table_normal_div">
    <table class="table_normal" id="table_review_list">
        <thead class="text_info">
        <tr>
            <th><input type="checkbox" class="cbx_select" id="cbx_select_all"><label for="cbx_select_all"></label></th>
            <th class="data_info" data-sort="asc" data-name="review_product_id">
                <span>评论产品</span>
                <span class="orderByDesc"></span>
                <span class="orderByAsc orderBySelect"></span>
            </th>
            <th class="data_info" data-sort="asc" data-name="review_content">
                <span>评论内容</span>
                <span class="orderByDesc"></span>
                <span class="orderByAsc orderBySelect"></span>
            </th>
            <th class="data_info" data-sort="asc" data-name="review_user_id">
                <span>评论人</span>
                <span class="orderByDesc"></span>
                <span class="orderByAsc orderBySelect"></span>
            </th>
            <th class="data_info" data-sort="desc" data-name="review_createdate">
                <span>评论时间</span>
                <span class="orderByDesc"></span>
                <span class="orderByAsc orderBySelect"></span>
            </th>
            <th>操作</th>
            <th hidden>评论ID</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${requestScope.reviewList}" var="review">
            <tr>
                <td><input type="checkbox" class="cbx_select" id="cbx_review_select_${review.review_id}"><label for="cbx_review_select_${review.review_id}"></label></td>
                <td title="${review.review_product.product_name}">${review.review_product.product_name}</td>
                <td title="${review.review_content}">${review.review_content}</td>
                <td title="${review.review_user.user_name}">${review.review_user.user_name}</td>
                <td title="${review.review_createDate}">${review.review_createDate}</td>
                <td><span class="td_special" title="查看评论详情"><a href="javascript:void(0)" onclick="getChildPage(this)">详情</a></span>&nbsp;&nbsp;<span class="td_special" title="删除评论"><a href="javascript:void(0)" onclick="delChildPage(this)">删除</a></span>
                </td>
                <td hidden><span class="review_id">${review.review_id}</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%@ include file="include/page.jsp" %>
    <div class="loader"></div>
</div>

<%-- 模态框 --%>
<div class="modal fade" id="modalDiv" tabindex="-1" role="dialog" aria-labelledby="modalDiv" aria-hidden="true"
     data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">提示</h4>
            </div>
            <div class="modal-body">您确定要删除评论吗？</div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary" id="btn-ok">确定</button>
                <button type="button" class="btn btn-default" data-dismiss="modal" id="btn-close">关闭</button>
            </div>
        </div>
        <%-- /.modal-content %--%>
    </div>
    <%-- /.modal %--%>
</div>

</body>
</html>
