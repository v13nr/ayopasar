<?php 
	@session_start();
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
	if($_SESSION["is_login"] != "Yes"){
		//die("access denied.");
	}
    ?>
    <?php
	include "../../php2/db.class.php";
	
	function action_delete()
	{
		$response = array(); // siapkan respon yang nanti akan di convert menjadi JSON
		$sql = "UPDATE things SET status = 0 WHERE id= '".$_POST["id"]."' AND software = 'sekolah'";
		$query = mysql_query($sql) or die(mysql_error());		
		if($query){
			
			die("Data Berhasil di hapus.");
		}else{
			$response["pesan"] = "Login Gagal. Code: -1."; 
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
