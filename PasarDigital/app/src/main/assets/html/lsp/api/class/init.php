<?php
define( 'DB_HOST', 'localhost' ); // set database host
define( 'DB_USER', 'sogionli_kredit' ); // set database user
define( 'DB_PASS', 'H?#L^)6e06Fc' ); // set database password
define( 'DB_NAME', 'sogionli_kredit' ); // set database name
define( 'SEND_ERRORS_TO', 'nanang.programmer@gmail.com' ); //set email notification email address
define( 'DISPLAY_DEBUG', true ); //display db errors?


$link = mysqli_connect(DB_HOST, DB_USER, DB_PASS, DB_NAME);

header("Access-Control-Allow-Origin: *");