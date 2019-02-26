<?php   

    include "../../../php/mysql.db.php";
	
    $db = new koneksi();

    include "../../../php/DB_init.php";
    $conn = $db->connect(true);
    
	$data = array();
    $sql = "SELECT  A.id AS id, (SELECT name FROM unit, pengemudi WHERE unit.id = pengemudi.unit) AS nama_unit, A.latlang_bid AS latlang_bid, B.nama as nama_pengemudi, A.status AS status, CONCAT('+62 ', B.hp) AS hp, A.status_bid AS status_bid, A.status_drop AS status_drop FROM unit C, orders A LEFT JOIN pengemudi B ON A.bid_pengemudi = B.iduser WHERE B.unit = C.id";
	if(isset($_POST["id"]) && $_POST["id"]<>""){
		$sql .= " AND A.id = '".$_POST["id"]."'";
	}
	$sql .= " AND A.status = 'Pesan' AND (status_bid = 'Bid') AND status_drop = ''";
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