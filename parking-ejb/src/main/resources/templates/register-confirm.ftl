<#import "lib/macro.ftl" as macro> 

<@macro.mail>
thank you for signing up to our application!
<br/><br/>
To complete the registration process and activate your account, please click on the following link:
<br/><br/>
<a href="${activationURL}">${activationURL}</a>
<br/><br/>
no later than ${activationDeadline.toString('EEEE, dd.MM.yyyy')}.  
</@macro.mail> 
