<?php
include "db/db.php";

define( 'DB_HOST', 'localhost' ); // set database host
define( 'DB_USER', 'sogionli_root' ); // set database user
define( 'DB_PASS', '100riburupiah' ); // set database password
define( 'DB_NAME', 'sogionli_sogi_online_kosong' ); // set database name
define( 'SEND_ERRORS_TO', 'nanang.programmer@gmail.com' ); //set email notification email address
define( 'DISPLAY_DEBUG', true ); //display db errors?
require_once( 'db/db.php' );


$database = new DB();
 
$database = DB::getInstance();
 


?>