<?php
define( 'DB_HOST', 'localhost' ); // set database host
define( 'DB_USER', 'sogionli_root' ); // set database user
define( 'DB_PASS', 'qwe123sogionline' ); // set database password
define( 'DB_NAME', 'sogionli_sogi_online_kosong' ); // set database name
define( 'SEND_ERRORS_TO', 'nanang.programmer@gmail.com' ); //set email notification email address
define( 'DISPLAY_DEBUG', true ); //display db errors?


$link = mysql_connect(DB_HOST, DB_USER, DB_PASS) or die("tidak terkoneksi dengan server");
mysql_select_db(DB_NAME) or die("tidak terhubung ke database");
header("Access-Control-Allow-Origin: *");