<?php 
	session_start();
    include "GCM.php";
    
    $gcm = new GCM();
    
    $gcmRegIds = array("fJopZJjFA_8:APA91bFZh5YUVmRf52iSzZ4RC6uJ0qVS2OLCbZWRH9L7p6tww60TjhqBB1OXkvMxHWtFjgmP2gxj-QlK5qfvtvFSw15oznkcnwXJ7aoHOaxYA_Y5st3thYDRtYh7pHRaIi4ABCRpHRCk");
    $result = $gcm->send_notification($gcmRegIds, array("message" => "To All Selamat datang di AyoPasar!"));

die($result);
?>