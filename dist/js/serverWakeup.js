function loadStatus()
{
    $.ajax(
    {
        type : "POST",
        url : "wakeupstat/",
        data :
        {},
        success : setStatusFromReply
    });
}

function setStatusFromReply(data, status)
{
    setStatus(JSON.parse(data))
}

$(document).ready(function()
{
    console.log("loading status")
    $("#wakeup_controls").hide();
    loadStatus();
});

function setStatus(obj)
{
    console.log("setStatusWakeup:[" + JSON.stringify(obj) + "]")
    $("#wakeup_controls").show();
    $("#hour").val(obj.HOUR);
    $("#min").val(obj.MIN);
    $("#tz").val(obj.TZ);
    $("#period").val(obj.PERIOD);
    $("#params").val(obj.PARAMS);
    var ws = false;
    if (obj.ISWORKING == "ON")
    {
        ws = true
    }
    $("#ws").prop("checked", ws);
    $("#future_info").text(obj.FUTURE_INFO);
}

function postDataToServerAndUpdateUI(hour, min, tz, period, params, iswokring)
{
    $.ajax(
    {
        url : "wakeupcon/",
        data :
        {
            "HOUR" : hour,
            "MIN" : min,
            "TZ" : tz,
            "PERIOD" : period,
            "PARAMS" : params,
            "ISWORKING" : iswokring
        },
        type : 'post',
        error : errorSendingData,
        success : dataSentOk
    });
}

function errorSendingData(XMLHttpRequest, textStatus, errorThrown)
{
    alert('Problem connecting to server, status:[' + XMLHttpRequest.status + '], status text: [' + XMLHttpRequest.statusText + ']');
}

function dataSentOk(data, status, jqXHR)
{
    setStatus(JSON.parse(data))
}

function sendData()
{
    var btn = $(this);
    var id = btn.attr('id');
    console.log("clicked:" + id)

    var hour = $("#hour").val();
    var min = $("#min").val();
    var tz = $("#tz").val();
    var period = $("#period").val();
    var params = $("#params").val();
    var operation = $("#ws").is(':checked');
    var isworking = "OFF"
    if (operation)
    {
        isworking = "ON"
    }

    postDataToServerAndUpdateUI(hour, min, tz, period, params, isworking)
}

$(function()
{
    $("button").on('click', function()
    {
        sendData()
    });
    $("input[type='checkbox']").on('click', function()
    {
        sendData()
    });
})