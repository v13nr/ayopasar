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
    }
    ?>
    <?php
	include "../../php2/db.class.php";
	
	function action_setpass()
	{
		$response = array(); // siapkan respon yang nanti akan di convert menjadi JSON
		$sql = "SELECT pass FROM anggota 
			WHERE email = '".$_POST["user"]."' 
			AND email <> ''
		";
		$query = mysql_query($sql) or die(mysql_error());	
		$baris = mysql_fetch_array($query);
		if($baris["pass"]!=MD5($_POST["pass0"])){
			$response["pesan"] = "Pengisian Password Lama salah."; 
			die("[".json_encode($response)."]");
		}
		$sql = "UPDATE anggota SET 
			pass = '".md5($_POST[pass1])."'
			WHERE email = '".$_POST["user"]."' 
		";
		$query = mysql_query($sql);	
		if($query){
				$response["pesan"] = "Password Berhasil diubah."; 
				
		}else{
			$response["pesan"] = "Register Gagal. Code : -1."; 
			$response['error'] = mysql_error(); // memberi respon ketika query salah
		}
		echo "[".json_encode($response)."]"; // convert variable respon menjadi JSON, lalu tampilkan 
	}
	
	if (isset($_POST["action"])) {
		$f = "action_".$_POST['action'];
		if (function_exists ($f)) {
			$f();
		}
	}
?>
