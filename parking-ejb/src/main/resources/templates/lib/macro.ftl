<#macro mail>
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Parking Email</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
		</head>
		<body style="margin: 0; padding: 0;">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">	
				<tr>
					<td style="padding: 10px 0 30px 0;">
						<table align="center" border="0" cellpadding="0" cellspacing="0" width="600" style="border: 1px solid #cccccc; border-collapse: collapse;">
							<tr>
								<td align="center" style="padding: 40px 0 30px 0; color: #153643; font-size: 28px; font-weight: bold; font-family: Arial, sans-serif;">
									<img src="cid:headerImage" alt="" width="${headerImageW}" height="${headerImageH}" style="display: block;" />
								</td>
							</tr>
							<tr>
								<td bgcolor="#ffffff" style="padding: 40px 30px 40px 30px;">
									<table border="0" cellpadding="0" cellspacing="0" width="100%">
										<tr>
											<td style="color: #153643; font-family: Arial, sans-serif; font-size: 24px;">
												<b>Dear ${titleUser.firstName},</b>
											</td>
										</tr>
										<tr>
											<td style="padding: 20px 0 30px 0; color: #153643; font-family: Arial, sans-serif; font-size: 16px; line-height: 20px;">
												<table border="0" cellpadding="0" cellspacing="0" width="100%">
													<tr>
														<td width="${600 - 30 - 30 - 20 - contentImageW}" valign="top" style="padding: 0 0 0 0; color: #153643; font-family: Arial, sans-serif; font-size: 16px; line-height: 20px;">
															<#nested>
														</td>
														<td style="font-size: 0; line-height: 0;" width="20">
   															&nbsp;
  														</td>
														<td width="${contentImageW}" valign="top">
															<img src="cid:contentImage" alt="" width="${contentImageW}" height="${contentImageH}" style="display: block;" />
														</td>
													</tr>
												</table>
											</td>
										</tr>								
									</table>
								</td>
							</tr>
							<tr>
								<td bgcolor="#8c8c8c" style="padding: 30px 30px 30px 30px;">
									<table border="0" cellpadding="0" cellspacing="0" width="100%">
										<tr>
											<td style="color: #ffffff; font-family: Arial, sans-serif; font-size: 14px;" width="75%">
												Parking 2015<br/>
												Source code can be found <a href="https://github.com/kpiorecki/parking" style="color: #ffffff;"><font color="#ffffff">here</font></a>
											</td>									
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</body>
	</html>
</#macro>