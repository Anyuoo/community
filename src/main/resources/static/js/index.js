$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    $("#publishModal").modal("hide");
    //获取标题和内容
    var title = $("#recipient-name").val();
    var content = $("#message-text").val();
    //发送异步请求
    $.post(
        CONTEXT_PATH + "/discuss/add",
        {"title": title, "content": content},
        //回调函数，返回结果
        function (data) {
            console.log(data)
            data = $.parseJSON(data);
            //提示框显示消息
            $("#hintBody").text(data.msg);
            //显示提示框
            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                //刷新页面
                if (data.code == 0) {
                    window.location.reload()
                }
            }, 2000);
        }
    );
}