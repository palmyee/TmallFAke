<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script>
        $(function () {
            //单击关闭按钮时
            $("#btn_review_cancel").click(function () {
                $(".menu_li[data-toggle=review]").click();
            });
        });
        //获取用户子界面
        function getUserPage(id) {
            //设置样式
            $("#div_home_title").children("span").text("用户详情");
            document.title = "Tmall管理后台 - 用户详情";
            //ajax请求页面
            ajaxUtil.getPage("user/" + id, null, true);
        }
        //获取产品子界面
        function getChildPage(id) {
            //设置样式
            $("#div_home_title").children("span").text("产品详情");
            document.title = "Tmall管理后台 - 产品详情";
            //ajax请求页面
            ajaxUtil.getPage("product/" +id, null, true);
        }
    </script>
    <style rel="stylesheet">
    </style>
</head>
<body>
<div class="details_div_first">
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_order_user">评论人</label>
        <span class="details_value td_wait"><a id="span_order_user" href="javascript:void(0)"
                                               onclick="getUserPage(${requestScope.review.review_user.user_id})">${requestScope.review.review_user.user_nickname}</a></span>
    </div>
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_review_createDate">评论时间</label>
        <span class="details_value" id="span_review_createDate">${requestScope.review.review_createDate}</span>
    </div>
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_product_name">评论产品</label>
    </div>
    <div class="frm_div">
        <span><a id="span_product_name" href="javascript:void(0)"
           onclick="getChildPage(${requestScope.review.review_product.product_id})">${requestScope.review.review_product.product_name}</a></span>
    </div>
    <div class="frm_div">
        <label class="frm_label text_info" id="lbl_review_content">评论内容</label>
    </div>
    <div class="frm_div">
        <span>${requestScope.review.review_content}</span>
    </div>
</div>
<div class="details_tools_div">
    <input class="frm_btn frm_clear" id="btn_review_cancel" type="button" value="关闭"/>
</div>
<div class="loader"></div>
</body>
</html>
