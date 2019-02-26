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

	$SQL = "UPDATE event SET
		judul = '".$_POST["value1"]."',
		keterangan= '".$_POST["value2"]."',
		lokasi= '".$_POST["lokasi"]."'
		WHERE 
		image= '".$_POST["gambar"]."'
		";
	$hasil = mysql_query($SQL) or die(mysql_error());
	
	
	$split = explode(',', $_GET["latlong"]);
	$SQL = "UPDATE things SET
		software = 'sekolah',
		tanggal_asset = '". date('Y-m-d') ."',
		namabukubarang= '".$_POST["namabukubarang"]."',
		pengarangpembuat= '".$_POST["pengarangpembuat"]."',
		jumlah_barang= '".$_POST["kuantitas"]."',
		harga= '".$_POST["harga"]."',
		sumber= '".$_POST["sumber"]."',
		referensi= '".$_POST["referensi"]."',
		tanggal_asset= '".$_POST["tanggalpenerimaan"]."',
		penerimaaan= '".$_POST["penerimaaan"]."',
		keluar= '".$_POST["keluar"]."',
		keterangan= '".$_POST["keterangan"]."'
		WHERE 
		id= '".$_GET["id"]."'
		";
	$hasil = mysql_query($SQL) or die(mysql_error());

echo "Data telah Terupdate. Terimakasih.";
?>
