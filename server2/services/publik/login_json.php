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

       //exit(0);
    }
    ?><?php
	//include "../../php2/db.class.php";
	if($_POST["username"]=="adminsogi2019"){
    	$data = array(
    	    'username'  => 'admin',
    	    'nama'      => 'Administrator',
    	    'email'     => 'admin@sogi-online.com'
    	    );  
    	
    	$jsondata = array(
    	    'result'    => $data,
    	    'error'     => false,
    	    'username'  => 'admin',
    	    'nama'      => 'Administrator',
    	    'email'     => 'admin@sogi-online.com'
    	    ); 
	    
	}   else {
	    
	    
    	$data = array(
    	    'username'  => 'Kontak Administrator Daerah Anda',
    	    'nama'      => 'Kontak Administrator Daerah Anda',
    	    'email'     => 'Kontak Administrator Daerah Anda'
    	    );  
    	    
	    $jsondata = array(
    	    'result'    => $data,
    	    'error'     => true,
    	    'username'  => 'Kontak Administrator Daerah Anda',
    	    'message'      => 'Kontak Administrator Daerah Anda',
    	    'email'     => 'Kontak Administrator Daerah Anda'
    	    ); 
	}
	die(json_encode($jsondata));
?>
