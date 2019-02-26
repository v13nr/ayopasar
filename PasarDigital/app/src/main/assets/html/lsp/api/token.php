<?php

if(!isset($_POST["token"])){
	die("No Direct Access.");
}

include "class/init.php";

$query = "SELECT COUNT(B.id) AS ada FROM v_token B WHERE  B.token = '".$_POST["token"]."' AND B.active = 1 LIMIT 1";
//die($query);
$results = mysqli_query($link,$query);

foreach( $results as $row )
{
    $tokenValid = $row['ada'];
}

?>