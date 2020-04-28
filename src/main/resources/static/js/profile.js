$(function () {
    $(".follow-btn").click(follow);
});

function follow() {
    var btn = this;
    var entityId = $("#entity-id").val();
    var entityType = "USER";

    $.post(
        CONTEXT_PATH + "/follow",
        {"entityType": entityType, "entityId": entityId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 403) {
                console.log(data.msg);
            }
            if (data.code === 1) {
                // 关注TA
                $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");

            }
            if (data.code === 0) { // 取消关注
                $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
            }
            console.log(data.msg);
            window.location.reload();
        }
    )
}