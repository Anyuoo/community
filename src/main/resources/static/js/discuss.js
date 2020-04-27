function like(btn, entityType, entityId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId},
        function (data) {
            data = $.parseJSON(data)
            console.log(data)
            if (data.code === 1) {
                $(btn).children('i').text(data.likeCount)
                $(btn).children('b').text(data.likeStatus === 1 ? '已赞' : '赞')
            }
        }
    );

}