<?php 

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
    }
include "../../php2/db.class.php";



function json_stores_list($sql) {
global $lang;

	$db = db_connect();

	$stores = $db->get_rows($sql);
	$json = array();

	if(!empty($stores)) {

		$json['success'] = 1;
		$json['query'] = $sql;

		$json['stores'] = array();

		foreach($stores as $k=>$v) {

			

			
			$json['stores'][] = array(
				'name'=>$v['name'],
				'logox'=>$v['logox'],
				'address'=>$v['address'],
				'email'=>$v['email'],
				'telephone'=>$v['telephone'],
				'description'=>$v['description'],
				'lat'=>$v['latitude'],
				'lng'=>$v['longitude'],
				'cat_name'=>$cat_name,
				'cat_img'=>$cat_img,
				'img'=>$img,
				'kabupaten'=>$kabupaten
			);

		}
	} else {

		$json['stores'][] = array(
				'name'=>'',
				'logox'=>'',
				'address'=>'',
				'email'=>'',
				'telephone'=>'',
				'description'=>'',
				'lat'=>'',
				'lng'=>'',
				'cat_name'=>'',
				'cat_img'=>'',
				'img'=>'',
				'kabupaten'=>''
			);
	}

return json_encode($json);
}

if(isset($_POST['addressx'])) {
	
	if(isset($_POST['action']) && $_POST['action']=='get_nearby_things') {
		
		
		//insert aksesor
		$SQLi = "INSERT INTO aksesor SET
			id = '',
			user_id = '0',
			lat = '".$_POST['lat']."',
			lang = '".$_POST['lng']."'
		";
		$hasili = mysql_query($SQLi) or die(mysql_error());
		
		if(!isset($_POST['lat']) || !isset($_POST['lng'])) {
			echo json_encode(array('success'=>0,'msg'=>'Coordinate not found'));
		exit;
	}
		
		// support unicode
		//mysql_query("SET NAMES utf8");

	
		
		$sql = "SELECT *, ( 3959 * ACOS( COS( RADIANS(".$_POST['lat'].") ) * COS( RADIANS( latitude ) ) * COS( RADIANS( longitude ) - RADIANS(".$_POST['lng'].") ) + SIN( RADIANS(".$_POST['lat'].") ) * SIN( RADIANS( latitude ) ) ) ) AS distance FROM things WHERE approved=1 AND pool = '". $_POST["addressx"] ." ' AND waktu >= DATE(DATE_SUB(NOW(), INTERVAL 3 DAY)) ".$category_filter." HAVING distance <= ".$_POST['distance']." ORDER BY distance ASC LIMIT 0,60";
		//die($sql); kayaknya bukan disini ya
		
		
		echo json_stores_list($sql);
	}
exit;
}
?>