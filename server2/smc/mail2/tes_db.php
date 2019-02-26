<?php
include "db/db.php";

define( 'DB_HOST', 'localhost' ); // set database host
define( 'DB_USER', 'semutsof_truk' ); // set database user
define( 'DB_PASS', 'K[BniN04p[Io' ); // set database password
define( 'DB_NAME', 'semutsof_truk' ); // set database name
define( 'SEND_ERRORS_TO', 'nanang.programmer@gmail.com' ); //set email notification email address
define( 'DISPLAY_DEBUG', true ); //display db errors?


$database = new DB();
 
$database = DB::getInstance();
 

$query = "SELECT * FROM job_email WHERE statusemail = 'Prepare'";
$results = $database->get_results( $query );
foreach( $results as $row )
{
    echo $row['jo'] . ', ID = '. $row['id'] .'<br />';
}
?>