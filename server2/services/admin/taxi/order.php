<?php   

    include "../../../php/mysql.db.php";
	
    $db = new koneksi();

    include "../../../php/DB_init.php";
    $conn = $db->connect(true);
        
    $iduser = isset($_POST['username']) ? base64_decode($_POST['username']) : "";
    $passwd = isset($_POST['password']) ? base64_decode($_POST['password'])  : ""; 

    
	$data = array();
    $sql = "SELECT * FROM orders";
	$db->query($sql, $rec_num, $data);
	
		$db->close($conn);
	
    if($rec_num<=0) {
        die('{success: false, message: "Tidak Ada Order Taksi."}');
    } else {

		include "../../../php/Json.php";
		$json = new Json();

		$result = array(
			"topics" => $data
		);

		die($json->encode($result));
    
	}
	

?>