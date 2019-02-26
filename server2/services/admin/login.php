<?php   

    include "../../php/mysql.db.php";
	
    $db = new koneksi();

    include "../../php/DB_init.php";
    $conn = $db->connect(true);
        
    $iduser = isset($_POST['username']) ? base64_decode($_POST['username']) : "";
    $passwd = isset($_POST['password']) ? base64_decode($_POST['password'])  : ""; 

    //USER
    $user = array();
    $sql = "SELECT A.iduser AS id,A.nama,A.status AS tipe, pass, A.idBackground AS idbackground FROM user A WHERE A.iduser='".$iduser."'";
	$db->query($sql, $rec_num, $user);
	$nama = $user[0]["nama"];
		
		
    if($rec_num<=0) {
        die('{success: false, message: "User login dan password tidak sesuai."}');
    } else {
		if(MD5($passwd)!=$user[0]["pass"]) {
			die('{success: false, message: "User login dan password tidak sesuai. "}');
		}
		
		//simpan data order
		$SQL = "INSERT INTO orders SET 
		user_id = '".$iduser."',
		nama = '".$nama."',
		waktu_order = '". date('Y-m-d H:i:s') ."',
		alamat_jemputan = '".$_POST["from"]."',
		detail_jemputan = '".$_POST["detail"]."',
		alamat_tujuan = '".$_POST["to"]."',
		tipe_taksi = '".$_POST["tipe"]."',
		status = 'Pesan'";
		$db->query($SQL, $rec_num, $user);
		$id = mysql_insert_id();
		
		$db->close($conn);
		
		die('{success:true, message: "Selamat Datang di Member Area. \nSilakan menunggu konfirmasi penjemputan.", token:'.$id.'}');
    
	}
	

?>