<?php

define('EMAIL_FOR_REPORTS', 'atulkekan1994@gmail.com');
define('RECAPTCHA_PRIVATE_KEY', '@privatekey@');
define('FINISH_URI', 'http://');
define('FINISH_ACTION', 'message');
define('FINISH_MESSAGE', 'Thanks for filling out my form!');
define('UPLOAD_ALLOWED_FILE_TYPES', 'doc, docx, xls, csv, txt, rtf, html, zip, jpg, jpeg, png, gif');

define('_DIR_', str_replace('\\', '/', dirname(__FILE__)) . '/');
require_once _DIR_ . '/handler.php';

?>

<?php if (frmd_message()): ?>
<link rel="stylesheet" href="<?php echo dirname($form_path); ?>/formoid-default-skyblue.css" type="text/css" />
<span class="alert alert-success"><?php echo FINISH_MESSAGE; ?></span>
<?php else: ?>
<!-- Start Formoid form-->
<link rel="stylesheet" href="<?php echo dirname($form_path); ?>/formoid-default-skyblue.css" type="text/css" />
<script type="text/javascript" src="<?php echo dirname($form_path); ?>/jquery.min.js"></script>
<form class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px" method="post"><div class="title"><h2></h2></div>
	<div class="element-input<?php frmd_add_class("input"); ?>"><label class="title">Modifier Code</label><input class="large" type="text" name="input" /></div>
	<div class="element-input<?php frmd_add_class("input1"); ?>"><label class="title">Modifier Name</label><input class="large" type="text" name="input1" /></div>
	<div class="element-select<?php frmd_add_class("select"); ?>"><label class="title">Modifier Group</label><div class="large"><span><select name="select" >

		<option value="option 1">option 1</option>
		<option value="option 2">option 2</option>
		<option value="option 3">option 3</option></select><i></i></span></div></div>
	<div class="element-textarea<?php frmd_add_class("textarea"); ?>"><label class="title">Modifier Description</label><textarea class="medium" name="textarea" cols="20" rows="5" ></textarea></div>
	<div class="element-input<?php frmd_add_class("input2"); ?>"><label class="title">Rate</label><input class="large" type="text" name="input2" /></div>
	<div class="element-radio<?php frmd_add_class("radio"); ?>"><label class="title"></label>		<div class="column column1"><label><input type="radio" name="radio" value="Applicable" /><span>Applicable</span></label><label><input type="radio" name="radio" value="Chargeable" /><span>Chargeable</span></label></div><span class="clearfix"></span>
</div>
<div class="submit"><input type="submit" value="Submit"/></div></form><script type="text/javascript" src="<?php echo dirname($form_path); ?>/formoid-default-skyblue.js"></script>

<!-- Stop Formoid form-->
<?php endif; ?>

<?php frmd_end_form(); ?>