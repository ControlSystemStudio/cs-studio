// Invoke GET /scans, display result in table 
$(function()
{
    $.get("/scans", "xml")
     .success(function(xml)
     {
         $(xml).find('scan').each(function()
         {
             var scan = $(this);
             
             var row = $('<tr/>');
             
             var item = scan.find('id').text();
             row.append( $('<td/>').append(item));
             
             item = scan.find('name').text();
             row.append( $('<td/>').append(item));

             item = new Date(+scan.find('created').text()).toLocaleString()
             row.append( $('<td/>').append(item));

             item = scan.find('state').text();
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
     });
});
