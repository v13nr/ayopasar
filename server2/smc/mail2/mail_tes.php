<?php
 require 'phpmailer/PHPMailerAutoload.php';
 include "ak.php";
 include "config.db.php";
 
 $jeneng = "customer.care.ayopasar@gmail.com";
 $kunci = "AyoPasar";
 $to = $_POST["email"];
 
	//Create a new PHPMailer instance
    $mail = new PHPMailer;   
    $mail->isSMTP();
	// change this to 0 if the site is going live
    $mail->SMTPDebug = 2;
    $mail->Debugoutput = 'html';
    $mail->Host = 'ssl://smtp.gmail.com';
    $mail->Port = 465; // 25; //366; //587;
    $mail->SMTPSecure = 'tls';

	//use SMTP authentication
    $mail->SMTPAuth = true;
	//Username to use for SMTP authentication
    $mail->Username = $jeneng;
    $mail->Password = $kunci;
    $mail->setFrom('customer.care.ayopasar@ayopasar.com', 'Ayo Pasar');
    $mail->addReplyTo($jeneng, 'Customer Care AyoPasar');
	$mail->addAddress($to, 'Somebody');
    $mail->Subject = 'New contact from AyoPasar';
    // $message is gotten from the form
	
	$mail->SMTPDebug = 0; 
	
	//$query = "SELECT A.*, B.kode_voucher FROM member A 
	       // LEFT JOIN voucher B ON B.email = A.email
	       // WHERE A.email_voucher_regis = 0 AND B.terpakai = 0";
	        
	        
	//email optional
	//$query .= "  AND A.email = '".$to."'";
	        
	        
	//$query .= "        LIMIT 1";
	
	//$results = $database->get_results( $query );
	
	      
function RandUnik($panjang) { 
	$unik = "";
   $pstring = "0123456789"; 
   $plen = strlen($pstring); 
      for ($i = 1; $i <= $panjang; $i++) { 
          $start = rand(0,$plen); 
          $unik.= substr($pstring, $start, 1); 
      } 
 
    return $unik; 
} 

$bulan = array(
                '01' => 'JANUARI',
                '02' => 'FEBRUARI',
                '03' => 'MARET',
                '04' => 'APRIL',
                '05' => 'MEI',
                '06' => 'JUNI',
                '07' => 'JULI',
                '08' => 'AGUSTUS',
                '09' => 'SEPTEMBER',
                '10' => 'OKTOBER',
                '11' => 'NOVEMBER',
                '12' => 'DESEMBER',
        );


$Kode =  "REG-".substr($bulan[date('m')],0,3).date('d').RandUnik(5);

	$isEksis =1; // $database->num_rows( $query );
	if($isEksis < 1){ die("Tidak ada data baru."); }
//	foreach( $results as $row )
//	{



		//echo $row["first_name"]. ' =>'. $row['phone'] . ', ID = '. $row['email'] .'<br />';
		//$message = "Faktur Anda Telah Terbit dengan Nomor ".$row['id'];
		$message .= "<br>Selamat anda berhasil registrasi dan telah bergabung menjadi keluarga AyoPasar. <br><br>
		Email ini terkirim otomatis sebagai pemberitahuan atas registrasi melalui aplikasi AyoPasar.<BR><BR>
		
		kode Aktivasi ini di App Anda ".$Kode."<br><br>
		Password Login Anda adalah ".$_POST["password"];
		
	
		$mail->msgHTML($message);
		$mail->AltBody = $filteredmessage;
		if (!$mail->send()) {
			$dataEmailPesan = "Pendaftaran Anda Belum Berhasil Mengirim Email, Harap coba kembali.";
			$result = array("success" => true, "message" => $dataEmailPesan);
            die(json_encode($result));
			//die("END HERE.");
		} else {
			//echo "Your message was successfully delivered,you would be contacted shortly.";
			$pesanTambahan = ". Email Verifikasi telah terkirim.";
		}
	//}
	
	/**
	 * Updating data
	 */
	//Fields and values to update
	$update = array(
		'email_voucher_regis' => 1
	);
	//Add the WHERE clauses
	$where_clause = array(
		'member.id' => $row['id']
	);
    //$updated = $database->update( 'member', $update, $where_clause, 1 );
	if( $updated )
	{
		echo '<p>Successfully updated '.$where_clause['id']. ' to '. $update['email_voucher_regis'].'</p>';
	}
	
	
?> 