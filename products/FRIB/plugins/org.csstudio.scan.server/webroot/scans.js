function putScanCommand(id, command)
{
    $.ajax(
    {
        type: "PUT",
        url: "/scan/" + id + "/" + command,
        success: function()
        {
            location.reload();
        },
        error: function(xhr, status, error)
        {
            alert(command.charAt(0).toUpperCase() + command.slice(1)  +
                  " of scan failed: " + error);
            location.reload();
        }
    });
}

function pauseScan(id)
{
    putScanCommand(id, "pause");
}

function resumeScan(id)
{
    putScanCommand(id, "resume");
}

function abortScan(id)
{
    if (! confirm("Abort scan with ID " + id + "?"))
        return;
    putScanCommand(id, "abort");
}

function deleteScan(id)
{
    if (! confirm("Delete scan with ID " + id + "?"))
        return;
    $.ajax(
    {
        type: "DELETE",
        url: "/scan/" + id,
        success: function()
        {
            location.reload();
        },
        error: function(xhr, status, error)
        {
            alert("Deletion of scan failed: " + error);
            location.reload();
        }
    });
}

function deleteCompletedScans()
{
    if (! confirm("Delete completed scans?"))
        return;
    $.ajax(
    {
        type: "DELETE",
        url: "/scans/completed",
        success: function()
        {
            location.reload();
        },
        error: function(xhr, status, error)
        {
            alert("Deletion of scans failed: " + error);
            location.reload();
        }
    });
}


// Invoke GET /scans, display result in table 
$(function()
{
    $.ajax(
    {
        type: "GET",
        url: "/scans",
        dataType: "xml",
        success: function(xml)
        {
            $(xml).find('scan').each(function()
            {
                var scan = $(this);
             
                var row = $('<tr/>');
             
                var id = scan.find('id').text();
                
                var links = "<a href='scan/" + id + "'>" + id + "</a>";
                links += " <a href='scan/" + id + "/commands'>(cmds)</a>";
                links += "<a href='scan/" + id + "/data'>(data)</a>";
                                row.append( $('<td/>').append(links));
             
                var item = scan.find('name').text();
                row.append( $('<td/>').append(item));

                item = new Date(+scan.find('created').text()).toLocaleString()
                row.append( $('<td/>').append(item));

                item = scan.find('state').text();
                if (item == 'Idle'  ||  item == 'Running')
                {
                    item = "<font color='#090'>" + item + "</font>";
                    item += "<button onclick='pauseScan(" + id + ")'>Pause</button>";
                    item += "<button onclick='abortScan(" + id + ")'>Abort</button>";
                }
                else if (item == 'Paused')
                {
                    item = "<font color='#990'>" + item + "</font>";
                    item += "<button onclick='resumeScan(" + id + ")'>Resume</button>";
                    item += "<button onclick='abortScan(" + id + ")'>Abort</button>";
                }
                else if (item == 'Aborted')
                {
                    item = "<font color='#900'>" + item + "</font>";
                    item += "<button onclick='deleteScan(" + id + ")'>Delete</button>";
                }
                else
                {
                    item += "<button onclick='deleteScan(" + id + ")'>Delete</button>";
                }
                
                row.append( $('<td/>').append(item));

                var performed = scan.find('performed_work_units').text();
                var total = scan.find('total_work_units').text();
                if (total > 0)
                    item = (performed / total) + "%";
                else
                    item = "-";
                row.append( $('<td/>').append(item));

                item = scan.find('runtime').text() / 1000;
                row.append( $('<td/>').append(item + " seconds"));

                item = new Date(+scan.find('finish').text()).toLocaleString()
                row.append( $('<td/>').append(item));

                item = scan.find('command').text();
                row.append( $('<td/>').append(item));

                item = scan.find('error').text();
                row.append( $('<td/>').append(item));

                $("#scans tbody").append(row);
            });
        }
    });
});
