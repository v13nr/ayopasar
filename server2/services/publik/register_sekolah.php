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
	
	function action_getReg()
	{
		$response = array(); // siapkan respon yang nanti akan di convert menjadi JSON
		$sql = "INSERT INTO user SET
			user = '".$_POST["user"]."',
			pass = '".md5($_POST[pass])."',
			software= 'sekolah',
			namasekolah= '".$_POST["namasekolah"]."',
			namasekolah2= '".$_POST["namasekolah2"]."',
			namakepsek = '".$_POST[namakepsek]."',
			statusnegeri= '".$_POST[statusnegeri]."',
			k_jenjang= '".$_POST[k_jenjang]."',
			alamatsekolah= '".$_POST[alamatsekolah]."',
			desakecamatan= '".$_POST[desakecamatan]."',
			kabupaten= '".$_POST[kabupaten]."'
		
		";
		$query = mysql_query($sql);	
		$id = mysql_insert_id();	
		if($query){
				$response["pesan"] = "Register Berhasil."; 
				$_SESSION["user_id"] = $id;
				$_SESSION["is_login"] = "Yes";
				
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
