<?php   
    include "../../php/mysql.db.php";
	
    $db = new koneksi();

    include "../../php/DB_init.php";
    $conn = $db->connect(true);
    
	$data = array();
    $sql = "SELECT id, nama, alamat_jemputan, detail_jemputan, alamat_tujuan FROM orders WHERE 1 ";
	if(isset($_POST["id"]) && $_POST["id"]<>""){
		$sql .= " AND id = '".$_POST["id"]."'";
	}
	$sql .= " AND status = 'Pesan' AND (status_bid = '' OR status_bid = 'Bid') AND status_drop = ''";
	//echo $sql;
	$db->query($sql, $rec_num, $data);
	
		$db->close($conn);
	
    if($rec_num<=0) {
        die('{success: false, message: "Tidak Ada Order Taksi."}');
    } else {

		include "../../php/Json.php";
		$json = new Json();

		$result = array(
			"topics" => $data
		);

		die($json->encode($result));
    
	}
	

?>