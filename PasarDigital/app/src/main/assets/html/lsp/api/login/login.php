<?php


include "../class/init.php";

 function acakpassword($psw) {

            $AcakPass = $psw;

            for ($i=1; $i<=88; $i++)

            {

               $AcakPass = md5($AcakPass);

            }

            return $AcakPass;

         }

if(!isset($_POST["username"])){
	die("No Direct Access.");
}


$query = "SELECT COUNT(A.id) as sukses, B.token, A.id, A.idgrup FROM admin_users A
LEFT JOIN v_token B ON A.token_id = B.id 
WHERE A.aktif = 1 AND 
(A.userid = '". $_POST["username"] ."' OR A.email='". $_POST["username"] ."' ) 
AND A.password = '".acakpassword($_POST["password"])."'
AND (A.idgrup = 8 OR A.idgrup = 9) 
AND B.token IS NOT NULL AND B.active = 1
";


//die($query);

$results = mysqli_query($link,$query);


foreach( $results as $row )
{
    $data[] = $row;
}


echo json_encode($data);