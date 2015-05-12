function isDemoOk()
{
     IsOk=true;
     bver=Math.round(parseFloat(navigator.appVersion) * 1000);
     if (navigator.appName.substring(0,8) == "Netscape")
     {
         if ((bver<5000) && (navigator.appVersion.indexOf("Mac")> 0))
             IsOk=false;
         else if (bver<4060)
             IsOk=false;
     }
	 if (navigator.appName.substring(0,9) == "Microsoft")
     {
         if(bver<4000)
             IsOk=false;
     }
     plugins=navigator.plugins;
     if (plugins!=null && IsOk==false)
     {
         for(i=0;i!=plugins.length;i++)
              if((plugins[i].name.indexOf("1.0")<0) && (plugins[i].name.indexOf("Java Plug-in")>=0))
                  IsOk=true;
     }
     return IsOk;
}

function openDemo(htmlFile,htmlWidth,htmlHeight)
{
var bua = navigator.userAgent;

     s = 'resizable=0,toolbar=0,menubar=0,scrollbars=0,status=0,location=0,directory=0,width=350,height=200';
     if(!isDemoOk())
     {
          open("http://www.turbodemo.com/error.html",'',s);
     }
     else
     {

     if (bua.indexOf("Opera")!= -1) 
      {
          window.open(htmlFile+".htm",'','width='+htmlWidth+',height='+htmlHeight+',top=10,left=10');
      }
     else
      {
          window.open(htmlFile+".htm",'','width='+htmlWidth+',height='+htmlHeight+',top=10,left=10');
      }
 
     }
}