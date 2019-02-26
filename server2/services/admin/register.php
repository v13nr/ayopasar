<?php   

    include "../../php/mysql.db.php";
	
    $db = new koneksi();

    include "../../php/DB_init.php";
    $conn = $db->connect(true);
        
    $email = isset($_POST['email']) ? base64_decode($_POST['email']) : "";
    $nama = isset($_POST['nama']) ? base64_decode($_POST['nama'])  : ""; 
    $hp = isset($_POST['hp']) ? base64_decode($_POST['hp'])  : ""; 
    $password = isset($_POST['password']) ? base64_decode($_POST['password'])  : ""; 

	$SQL = "SELECT iduser FROM user WHERE iduser = '$email'";
	$db->query($SQL, $rec_num, $user);
    if($rec_num>0) {
        die("{success: false, message: \"user id sudah dipakai.\"}");
    }
	 
    $sql = "INSERT INTO user SET
		iduser = '".$email."',
		nama = '".$nama."',
		pass = '".MD5($password)."',
		hp = '".$hp."',
		status = 'U'";
	$db->query($sql, $rec_num, $user);
	
	
	//simpan data order
		$SQL = "INSERT INTO orders SET 
		user_id = '".$email."',
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
	

?>