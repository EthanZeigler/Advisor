function queryserver() {
    console.log("making the request");
    var text = String($('#paste_field').val());
    var jqXHR = $.ajax({
        type: "POST",
        url: "http://nossl.ethanzeigler.com/test",
        data: text,
        contentType: "charset=utf-8",
        dataType: "text",
        success: function (data, textStatus, jqXHR) {
            console.log(textStatus);
            $('#card-content').empty().append(data.replace("\n", "<br>"));
            $('#response-card').css("display", "initial");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(textStatus);
            $('#server_status').empty().append("Something went wrong. If it continues, contact the developer at ethanzeigler (at] g mail dottcom (Why this? <a href='http://www.scrapebox.com/email-scraper'>Email Scrapers.</a>)<br><br>NOTICE: THIS SERVICE IS HTTP ONLY for the time being. If you are seeing this error, make sure you are viewing this page using HTTP and not HTTPS!");
        }
    });
}
//# sourceMappingURL=paws.js.map