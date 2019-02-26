<?php @session_start();
if($_SESSION["is_login"] != "Yes"){
	die(" sesi yang Anda gunakan tidak bisa diupload. Silakan LOGIN terlebih dahulu. TIDAK");
}
include "config_sistem.php";
//print_r($_FILES);
foreach($_FILES as $key=>$value)
{
 // echo $value;
}
function random_string($length) {
    $key = '';
    $keys = array_merge(range(0, 9), range('a', 'z'));

    for ($i = 0; $i < $length; $i++) {
        $key .= $keys[array_rand($keys)];
    }

    return $key;
}

$new_image_name = random_string(8).".jpg";
if($_FILES){
	
	
	$gambarnya = "Sesi : ".session_id(). '_::_' .$new_image_name;
	move_uploaded_file($_FILES["file"]["tmp_name"], "./file/".$gambarnya);
	
	$SQL = "INSERT INTO event SET
		id = '',
		user_id = '".$_SESSION["user_id"]."',
		image = '$gambarnya',
		latlong = '".$_GET["latlong"]."',
		judul = '".$_REQUEST["value1"]."',
		keterangan= '".$_REQUEST["value2"]."'
		";
	$hasil = mysql_query($SQL) or die(mysql_error());
	
	$split = explode(',', $_GET["latlong"]);
	$SQL = "INSERT into things SET
		id = '',
		latitude = '".$split[0]."',
		longitude = '".$split[1]."',
		logox = '".$gambarnya ."',
		approved = '1',
		pool = 'Makassar',
		judul = '".$_REQUEST["value1"]."',
		keterangan= '".$_REQUEST["value2"]."'
		";
	$hasil = mysql_query($SQL) or die(mysql_error());
}

	echo $gambarnya;

?>