$(function () {
    $("#sendBtn").click(send_letter);
    $(".close").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");
    var toUsername = $("#recipient-name").val();
    var content = $("#message-text").val();
    $.post(
        CONTEXT_PATH + "/letter/send",
        {"toUsername": toUsername, "content": content},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 403) {
                $("#hintBody").text(data.msg);
            }
            if (data.code === 1) {
                $("#hintBody").text(data.msg);
            }
            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                window.location.reload();
            }, 2000);
        }
    );
}

function delete_msg() {
    var id = $("#delete-id").val();
    $.post(
        CONTEXT_PATH + "/letter/delete",
        {"id": id},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 1) {
                window.location.reload();
            }
        }
    );
    // TODO 删除数据
    $(this).parents(".media").remove();
}