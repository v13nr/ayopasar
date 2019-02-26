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
    }?><?php
    //print_r($_POST);
    //die();
    /*
    
    Array
 (
     [user_id] => 1
     [gambar] => 1549774301819(4).jpg
     [jenisbarang] => tjenisbarang
     [kodebarang] => tkodebarang
     [identitasbarang] => tidentitasbrg
     [jumlah_barang] => tkuantitas
     [apbdesa] => cash
     [lain] => 
     [kekayaan] => 
     [tanggal_asset] => 2019-02-10
     [keterangan] => tketerangan
 )
 
    
    */
     include "config_sistem.php";
    
    $result = array("success" => true, "message" => "Data GAGAL terkirim.");
    
    $SQL = "INSERT INTO barang SET
		user_id = '".$_POST["user_id"]."',
		gambar = '".$_POST["gambar"]."',
		jenis_barang = '".$_POST["jenisbarang"]."',
		kodebarang = '".$_POST["kodebarang"]."',
		identitasbarang = '".$_POST["identitasbarang"]."',
		jumlah_barang = '".$_POST["jumlah_barang"]."',
		cash = '".$_POST["apbdesa"]."',
		kredit = '".$_POST["lain"]."',
		hibah = '".$_POST["kekayaan"]."',
		tanggal_asset = '".$_POST["tanggal_asset"]."',
		keterangan = '".$_POST["keterangan"]."'";
	$hasil = mysql_query($SQL) or die(json_encode($SQL));
	
    
	
    if($hasil){
        $result = array("success" => true, "message" => "Data telah tersimpan.");
        die(json_encode($result));
        
    } else {
        
        $result = array("success" => true, "message" => "Data GAGAL terkirim. LSP Komputer");
        die(json_encode($result));
    }

?>
