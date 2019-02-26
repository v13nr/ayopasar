<?php 
	session_start();
    include "GCM.php";
    
    $gcm = new GCM();
    
    $gcmRegIds = array($_GET["reg_id"]);
    $result = $gcm->send_notification($gcmRegIds, array("message" => $_GET["sending_text"]));

die($result);
?>