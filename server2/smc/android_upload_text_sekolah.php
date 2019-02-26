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
    }?>
    
    <?php
    
     include "config_sistem.php";
    
    $result = array("success" => false, "message" => "Data GAGAL terkirim.");
    
    $split = explode(',', $_POST["latlong"]);
    $SQL = "INSERT INTO anggota SET
		nama_lengkap = '".$_POST["nama_anggota"]."',
		nik = '".$_POST["nik"]."',
		hp = '".$_POST["hp"]."',
		email = '".$_POST["email"]."',
		pass = '".$_POST["password"]."',
		jenis_lapak = '".$_POST["jenis_lapak"]."',
		nama_bank = '".$_POST["nama_bank"]."',
		norek = '".$_POST["norek"]."',
		namapasar = '".$_POST["namapasar"]."',
		namalapak = '".$_POST["namalapak"]."',
		alamat = '".$_POST["alamat"]."',
		npwp = '".$_POST["npwp"]."'";
	$hasil = mysql_query($SQL) or die(json_encode($result));
	
    include "mail2/mail_tes.php";
    
        
        
    $SQL = "UPDATE anggota SET
		kode_aktivasi = '".$Kode."'
		WHERE email = '". $_POST["email"] ."'
		";
	$hasil = mysql_query($SQL) or die(json_encode($result));
	
    if($hasil){
        $result = array("success" => true, "message" => "Data telah terkirim. Menunggu Validasi kebenaran Data Dari ASPARINDO.".$pesanTambahan);
        die(json_encode($result));
        
    } else {
        
        $result = array("success" => true, "message" => "Data GAGAL terkirim. LSP Komputer");
        die(json_encode($result));
    }

?>
