<#import "lib/macro.ftl" as macro> 

<@macro.mail>
you have requested to reset your password.
<br/><br/>
Please click on the following link to change your password:
<br/><br/>
<a href="${resetPasswordURL}">${resetPasswordURL}</a>
<br/><br/>
<#if validityHours == 1>
The link is valid within next hour.
<#else>
The link is valid within next ${validityHours} hours.
</#if>
</@macro.mail> 
