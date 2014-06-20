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
            error: function(xhr, status, error)
            {
                alert('Error: ' + error);
            },
            success: function(xml)
            {
                // var id = +$(xml).find('id').text();
                // alert('Submitted: ' + id);
                document.location = '/scans.html';
            }
        });
    });
});
