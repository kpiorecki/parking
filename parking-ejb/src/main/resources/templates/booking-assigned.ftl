<#import "lib/macro.ftl" as macro> 

<@macro.mail>
you have been assigned a parking place on
<br/><br/>
<b>${date.toString('EEEE, dd.MM.yyyy')}</b>
<br/><br/>
in ${parking.address.city}, ${parking.address.street} ${parking.address.number}.  
</@macro.mail> 
