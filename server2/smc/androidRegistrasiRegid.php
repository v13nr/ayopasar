<?php 
	session_start();
    mysql_connect('localhost', 'sogionli_kredit', 'H?#L^)6e06Fc') or die('Tidak terkoneksi dengan server.');
 mysql_select_db('sogionli_kredit') or die('Tidak terkoneksi dengan DB');
    
    /*
    
    berarti di androidRegistrasiRegid insert ya bukan update
[13:53, 2/15/2019] Rifkhi: iya mas.
[13:53, 2/15/2019] Rifkhi: kalo reg_idnya sama
[13:53, 2/15/2019] Rifkhi: tinggal update
[13:53, 2/15/2019] Rifkhi: tapi kalo tidak ada
[13:53, 2/15/2019] Rifkhi: di insert
    
    */
    $result = array("success" => false, "message" => "Quesry Error.");
		
	$hasil = mysql_query("UPDATE gcm_reg_id SET reg_id = '".$_POST["reg_id"]."'") or die(json_encode($result));
	
    if($hasil){
        $result = array("success" => true, "message" => "Register GCM Reg ID Berhasil.");
        die(json_encode($result));
        
    } else {
        
        $result = array("success" => false, "message" => "GAGAL Register GCM Reg ID.");
        die(json_encode($result));
    }

?>