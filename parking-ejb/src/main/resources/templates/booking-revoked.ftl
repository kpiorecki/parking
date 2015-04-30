<#import "lib/macro.ftl" as macro> 

<@macro.mail>
unfortunately your parking place reservation on
<br/><br/>
<b>${date.toString('EEEE, dd.MM.yyyy')}</b>
<br/><br/>
in ${parking.address.city}, ${parking.address.street} ${parking.address.number}
<br/><br/>
has been canceled. We're sorry for any inconvenience caused.
</@macro.mail> 
