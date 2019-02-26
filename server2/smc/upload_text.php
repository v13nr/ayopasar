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
		keterangan= '".$_POST["keterangan"]."',
		jenisbarang= '".$_POST["jenisbarang"]."',
		kodebarang= '".$_POST["kodebarang"]."',
		identitasbarang= '".$_POST["identitasbarang"]."',
		asalusul= '".$_POST["asalusul"]."',
		tanggal_asset= '".$_POST["tanggal_asset"]."'
		WHERE 
		logox= '".$_GET["gambar"]."'
		";
	$hasil = mysql_query($SQL) or die(mysql_error());

echo "Data telah terkirim. Terimakasih.";
?>
