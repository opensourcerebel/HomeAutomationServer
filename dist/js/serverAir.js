function loadStatus()
{
    $.ajax(
    {
        type : "POST",
        url : "airstat/",
        data :
        {},
        success : setStatusFromReply
    });
}

function setStatusFromReply(data, status)
{
    setStatus(JSON.parse(data))
}

function setStatus(obj)
{
    console.log("setStatus:[" + JSON.stringify(obj) + "]")
    $("#working_state").show();
    if (obj.WORKINGSTATE == "ON")
    {
        updateRes("working_state_on", "off")
    }
    else if (obj.WORKINGSTATE == "OFF")
    {
        updateRes("working_state_off", "off")
    }
    else
    {
        alert("WORKINGSTATE unknown " + obj.WORKINGSTATE);
    }

    $("#working_mode").show();
    if (obj.MODE == "COOL")
    {
        updateRes("mode_cool", "heat")
    }
    else if (obj.MODE == "HEAT")
    {
        updateRes("mode_heat", "heat")
    }
    else
    {
        alert("MODE unknown " + obj.MODE);
    }

    $("#fan_state").show();
    if (obj.FAN == "FANAUTO")
    {
        updateRes("fan_auto", "heat")
    }
    else if (obj.FAN == "FANSILENT")
    {
        updateRes("fan_silent", "heat")
    }
    else
    {
        alert("FAN unknown " + obj.FAN);
    }

    $("#hswing_state").show();
    if (obj.HSWING == "SWINGON")
    {
        updateRes("hswing_on", "on")
    }
    else if (obj.HSWING == "SWINGOFF")
    {
        updateRes("hswing_off", "off")
    }
    else
    {
        alert("HSWING unknown " + obj.HSWING);
    }

    $("#vswing_state").show();
    if (obj.VSWING == "SWINGON")
    {
        updateRes("vswing_on", "on")
    }
    else if (obj.VSWING == "SWINGOFF")
    {
        updateRes("vswing_off", "off")
    }
    else
    {
        alert("VSWING unknown " + obj.VSWING);
    }

    $("#deg").show();
    $("#deg_btn").show();
    $("#deg_lbl").text(obj.DEGREES);
}

function updateRes(idx, offString)
{
    var btn = $("button[id='" + idx + "']");
    fixButton(btn, offString);
}

$(document).ready(function()
{
    $("#working_state").hide();
    $("#working_mode").hide();
    $("#fan_state").hide();
    $("#hswing_state").hide();
    $("#vswing_state").hide();
    $("#deg").hide();
    $("#deg_btn").hide();
    loadStatus();
});

function postDataToServerAndUpdateUI(wState, mode, fanState, deg, hsw, vsw, btn)
{
    $.ajax(
    {
        url : "aircon/",
        data :
        {
            "WORKINGSTATE" : wState,
            "MODE" : mode,
            "DEGREES" : deg,
            "FAN" : fanState,
            "HSWING" : hsw,
            "VSWING" : vsw
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

$(function()
{
    $("div[data-mode='status-switch'] button").on('click', function()
    {
        var btn = $(this);
        var id = btn.attr('id');
        console.log("clicked:" + id)
        var airState = getState($("div[id='working_state']"))
        var airMode = getState($("div[id='working_mode']"))
        var airFan = getState($("div[id='fan_state']"))
        var airHswing = getState($("div[id='hswing_state']"))
        var airVswing = getState($("div[id='vswing_state']"))
        var airDeg = $("#deg_lbl").text()

        if (id === "working_state_on" || id === "working_state_off")
        {
            airState = btn.attr('data-id')
        }
        else if (id === "mode_cool" || id === "mode_heat")
        {
            airMode = btn.attr('data-id')
        }
        else if (id === "fan_silent" || id === "fan_auto")
        {
            airFan = btn.attr('data-id')
        }
        else if (id === "hswing_on" || id === "hswing_off")
        {
            airHswing = btn.attr('data-id')
        }
        else if (id === "vswing_on" || id === "vswing_off")
        {
            airVswing = btn.attr('data-id')
        }
        else if (id === "deg_up")
        {
            var data = parseInt(airDeg) + 1
            airDeg = "" + data
        }
        else if (id === "deg_down")
        {
            var data = parseInt(airDeg) - 1
            airDeg = "" + data
        }

        postDataToServerAndUpdateUI(airState, airMode, airFan, airDeg, airHswing, airVswing, btn);
    });
})

function fixButton(button, offString)
{
    var currentElHTML = button.data('key');

    if (currentElHTML == "off")
    {
        button.addClass('btn-danger');
    }
    else
    {
        button.addClass('btn-success');
    }

    var parent = $(button[0].offsetParent);
    if (parent != undefined && parent.hasClass("btn-group"))
    {
        var i = 0;
        for (i; i < parent[0].children.length; i++)
        {
            if ($(parent[0].children[i]).data('key') != currentElHTML)
            {
                $(parent[0].children[i]).removeClass("btn-danger");
                $(parent[0].children[i]).removeClass("btn-success");
                $(parent[0].children[i]).addClass("btn-default");
            }
        }
    }
}

function getState(parent)
{
    var i = 0;
    for (i; i < parent[0].children.length; i++)
    {
        if ($(parent[0].children[i]).hasClass("btn-success"))
        {
            return $(parent[0].children[i]).attr('data-id');
        }

        if ($(parent[0].children[i]).hasClass("btn-danger"))
        {
            return $(parent[0].children[i]).attr('data-id');
        }
    }
}
