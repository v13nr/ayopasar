<?php 
session_start();
error_reporting(1);
?>
<!DOCTYPE html>
<html>
<head>
	<title>Inventaris Aset Desa :: iAD</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
</head>
<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#">LBS</a>
    </div>
    <ul class="nav navbar-nav">
      <li class=""><a href="index.php?mn=aset">Aset Desa</a></li>
      <li class=""><a href="index.php?mn=sekolah">Aset Sekolah</a></li>
      <li><a href="#"  data-toggle="modal" data-target="#myModal">Login</a></li>
    </ul>
  </div>
</nav>
<?php
	
	include "config_sistem.php";
	
		$SQLlogin = "SELECT * FROM user WHERE user = '".$_POST["user"]."' AND pass=md5('".$_POST["pass"]."')";
		$querylogin = mysql_query($SQLlogin ) or die('1.'. mysql_error());	
		$barislogin = mysql_fetch_array($querylogin);
		if(mysql_num_rows($querylogin) > 0){
			
			$_SESSION["is_login_web"] = "Yes";
		
		}
		
	
	$SQL = "SELECT A.*, B.jeneng, C.* FROM event A LEFT JOIN user B ON A.user_id = B.id LEFT JOIN things C ON A.image = C.logox WHERE 1 ";
	if($_SESSION["is_login_web"] = "Yes" && $barislogin["id"]<>""){
		//$SQL .= " AND B.id = 37"; //'". $barislogin["id"] . "'";
	} elseif($_GET["mn"]=="sekolah") {
		$SQL .= " AND C.software= 'sekolah'";
	} elseif($_GET["mn"]=="aset") {
		$SQL .= " AND C.software= 'iad'";
	}
	$SQL .= " AND B.id = 37"; 
	$SQL .= " ORDER BY A.waktu DESC LIMIT 10";
	//echo $SQL;
	$hasil = mysql_query($SQL) or die('2.'.mysql_error());
?>
  <div class="table-responsive">          
  <table class="table">
		<tr>
			<td>No.</td>
			<td>User</td>
			<td>Tanggal Pengambilan Foto</td>
			<td>Judul</td>
			<td>IB</td>
			<td>AU</td>
			<td>Image</td>
			<td>Koordinat</td>
		</tr>
		<?php while($baris=mysql_fetch_array($hasil)) { ?>
		<tr>
			<td><?php echo ++$no; ?></td>
			<td><?php echo $baris["jeneng"];?></td>
			
			<td><?php echo $baris["waktu"]; ?></td>
			<td><?php echo $baris["namabukubarang"]; ?></td>
			<td><?php echo $baris["identitasbarang"]; ?></td>
			<td><?php echo $baris["asalusul"]; ?></td>
			<td><img src="file/<?php echo $baris["image"]; ?>" width = "200px"?></td>
			<td><a href="https://www.google.co.id/maps/@<?php echo $baris["latlong"]; ?>,15z?hl=en"><?php echo $baris["latlong"]; ?></a></td>
		</tr>
		<?php } ?>
	</table>
</div>
<!-- Modal -->
  <div class="modal fade" id="myModal" role="dialog">
    <div class="modal-dialog">
    
      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">Login</h4>
        </div>
        <div class="modal-body">
        <form method="post" action="">
          <p>
          	<input type="text" name="user" placeholder="fill username">
          	<br>
          	<input type="password" name="pass" placeholder="fill password">
          	<br>
          	<input type="submit" class="btn btn-default" name="submit" value="Login">
          </p>
         </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
      </div>
      
    </div>
</body>
</html>