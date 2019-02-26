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
    }
   

$cari = $_POST['cari'];

function grab($link){

     $curl = curl_init();

     curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);

     curl_setopt($curl, CURLOPT_URL, $link);

     $grab = curl_exec($curl);

     curl_close($curl);

     return $grab;

}

$is_negeri = $_POST["is_negeri"];
$k_jenjang = $_POST["k_jenjang"];
$limit = $_POST["limit"];
$page = $_POST["page"];
					
echo grab('http://siap-online.com/bau-bau/sekolah/index?limit='.$limit.'&page='.$page.'&k_jenjang='.$k_jenjang.'&is_negeri='.$is_negeri);

?>