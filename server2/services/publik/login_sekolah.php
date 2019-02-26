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

	include "../../php2/db.class.php";
	
	function action_getAuth()
	{
		$response = array(); // siapkan respon yang nanti akan di convert menjadi JSON
		$sql = "SELECT * FROM user WHERE user = '".$_POST["user"]."' AND user <> '' AND software = 'sekolah' AND pass=md5('".$_POST["pass"]."')";
		$query = mysql_query($sql);		
		if($query){
			if(mysql_num_rows($query) > 0){
				$response["pesan"] = "Login Berhasil."; 
				while($row = mysql_fetch_object($query)){
					// masukan setiap baris data ke variable respon
					$response[] = $row; 
					$id = $row->id;
				}
				$_SESSION["is_login"] = "Yes";
				$_SESSION["user_id"] = $id;
			}else{
				$response["pesan"] = "Login Gagal. Code:0"; 
				$response['error'] = 'Data kosong'; // memberi respon ketika data kosong
			}
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
