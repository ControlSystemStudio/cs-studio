// Simulate scan
$(function()
{
    $('#simulate').click(function()
    {
        var commands = $('#scan').val();
        
        $.ajax(
        {
            type: 'POST',
            url: '/simulate/',
            processData: false,
            contentType: 'text/xml',
            data: commands,
            error: function(xhr, status, error)
            {
                alert('Error: ' + error);
            },
            success: function(xml)
            {
                var log = $(xml).find('log').text();
                var seconds = $(xml).find('seconds').text();
                $('#simulation').val(log);
                $('#runtime').text(seconds);
            }
        });
    });
});
