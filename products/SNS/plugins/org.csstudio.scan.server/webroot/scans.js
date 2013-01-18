
function abortScan(id)
{
    if (! confirm("Abort scan with ID " + id + "?"))
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
            alert("Abort of scan failed: " + error);
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
                    item += "<button onclick='abortScan(" + id + ")'>Abort</button>";
                row.append( $('<td/>').append(item));

                item = scan.find('percentage').text() + '%';
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
