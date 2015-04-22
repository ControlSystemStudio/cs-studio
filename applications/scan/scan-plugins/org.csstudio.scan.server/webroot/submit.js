// Submit scan
$(function()
{
    $('#submit').click(function()
    {
        var name = $('input[name=name]').val();
        var commands = $('textarea').val();
        
        $.ajax(
        {
            type: 'POST',
            url: '/scan/' + name,
            processData: false,
            contentType: 'text/xml',
            data: commands,
            error: function(xhr, status, message)
            {
                var message = $(xhr.responseXML).find("message").text()
                var trace = $(xhr.responseXML).find("trace").text()
                alert(message + trace);
            },
            success: function(xml)
            {
                var id = +$(xml).find('id').text();
                alert('Submitted: ' + id);
                document.location = '/scans.html';
            }
        });
    });
});
