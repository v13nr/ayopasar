<?php 
	session_start();
    // Allow from any origin
    if (isset($_SERVER['HTTP_ORIGIN'])) {
        header("Access-Control-Allow-Origin: {$_SERVER['HTTP_ORIGIN']}");
        header('Access-Control-Allow-Credentials: true');
        header('Access-Control-Max-Age: 86400');    // cache for 1 day
    }

    // Access-Control headers are received during OPTIONS requests
    if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {

        if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_METHOD']))
            header("Access-Control-Allow-Methods: GET, POST, OPTIONS");         

        if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']))
            header("Access-Control-Allow-Headers: {$_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']}");

        exit(0);
    }?><?php
    
     include "config_sistem.php";
    
    $result = array("success" => true, "message" => "Data GAGAL terkirim.");
    
    $SQL = "INSERT INTO collecting SET
		user_id = '".$_POST["user_id"]."',
		paket = '".$_POST["paket"]."',
		cicilan = '".$_POST["cicilan"]."',
		email = '".$_POST["email"]."'";
	$hasil = mysql_query($SQL) or die(json_encode($result));
	
    
	
    if($hasil){
        $result = array("success" => true, "message" => "Data telah terkirim. Menunggu Proses Dari ASPARINDO.");
        die(json_encode($result));
        
    } else {
        
        $result = array("success" => true, "message" => "Data GAGAL terkirim. LSP Komputer");
        die(json_encode($result));
    }

?>
