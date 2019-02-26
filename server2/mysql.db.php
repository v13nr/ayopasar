<?php
session_start();

class koneksi{

    var $db_host;
    var $db_user;
    var $db_pass;
    var $field;
    var $db_name;

    public $tabelDB ;
    public $fieldArr ;

    function session_validation() {
        global $_SESSION;

        if(!isset($_SESSION['sis'])) {
            die("{success: false, message: \"You are not login.\"}");
        }
    }

    public function koneksi($db_hostparam="",$db_uidparam="",$db_pwdparam="",$db_nameparam="") {
        $this->db_host = $db_hostparam;
        $this->db_user = $db_uidparam;
        $this->db_pass = $db_pwdparam;
        $this->db_name = $db_nameparam;
    }
		
    public function connect($bypass_session=false) {

        if(!$bypass_session) $this->session_validation();
        $this->conn = @mysql_connect($this->db_host, $this->db_user, $this->db_pass);
        if($this->conn) {
            if(@mysql_select_db($this->db_name, $this->conn)) {
				$rs = $this->getFieldValue("rekening_header", array("DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s') AS sekarang", "IF(DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT(lastregister, '%Y-%m-%d %H:%i:%s'), 'ILEGAL', 'LEGAL') AS validasi", "((TIME_TO_SEC(TIMEDIFF(DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'), DATE_FORMAT(lastregister, '%Y-%m-%d %H:%i:%s')))/60)/60)/24 AS totallife"), array("aktif" => "Y"));
                if(count($rs)>0 AND $rs[0]["validasi"]=="LEGAL") {
                    $sql = "UPDATE rekening_header SET lastregister='".$rs[0]["sekarang"]."', lifetime=lifetime+".$rs[0]["totallife"]." WHERE aktif='Y'";
                    $this->execute($this->conn, $sql);
//                } else {
//                    $this->close($this->conn);
//                    die("{success: false, message:\"Ilegal Operation.\"}");
                }
				return $this->conn;
			}
		}
        die('Error Connection: ' . mysql_error());
    }

    public function close($conn) {
        $return = @mysql_close($conn);
        unset($conn);
        return $return;
    }

    public function array_flatten($array) {
        if (!is_array($array)) {
            return FALSE;
        }
        $result = array();
        foreach ($array as $key => $value) {
            if (is_array($value)) {
              $result = array_merge($result, $this->array_flatten($value));
            }
            else {
              $result[$key] = $value;
            }
        }
        return $result;
    }

    public function query($sql, &$rec_count, &$data){
	$result = @mysql_query($sql);

        if ($result ) {
            $rec_count = @mysql_num_rows($result);
            $data = array();

            if ($rec_count > 0)
                while ($row = @mysql_fetch_array($result, MYSQL_ASSOC)) {
                    foreach ($row as $key => $value)
                        if($value=="true" OR $value=="false") $row[$key] = $value=="true";
                    
                    array_push($data, $row);
                }
            @mysql_free_result($result);
            unset($result);

            return true;
        }

        die('Invalid SQL command : ' . $sql . '<br><br>' . mysql_error());
    }
		
     public function execute($conn, $sql) {
        $result = @mysql_query($sql, $conn);

        if($result == 0) {
            if(strpos(mysql_error(), "command denied to user") !== false)
                die('{success:false, message:"Data dalam keadaan arsip!"}');
            die('Invalid SQL command : ' . $sql . '<br><br>' . mysql_error());
        }else
            return mysql_affected_rows();
    }
        
    public function SQLupdateData($dataArr,$cozArr, $escapequote='') {

    	$query = "UPDATE ".$this->tabelDB." SET " ;
    	foreach($dataArr as $item => $value )
    		$query .= "".$item."=" . ($item==$escapequote?"$value":"'$value'") . ", " ;
    	
    	$query = rtrim($query,', ') ;
    	$query .= " WHERE ";
    	
    	for($i=0;$i< count($cozArr) ;$i++)
            $query .= "".$cozArr[$i]['fieldTbl']."='".$cozArr[$i]['content']."' ".$cozArr[$i]['operator']." ";

    	return $query ;	
    }	
    
    public function SQLinsertData($escapequote='') {
    	$query = "INSERT INTO ".$this->tabelDB." SET ";
    	$fieldArr = $this->fieldArr ;
    	foreach($fieldArr as $item => $value )
    		$query .= "".$item."=" . ($item==$escapequote?"$value":"'$value'") . ", " ;
    	
    	$query = rtrim($query,', ') ;
    	return $query ;
    }

     public function getLastNumber($table, $field, $length=0, $prefix='', $suffix='', $seleksi=array(), $kecuali=array(), $escapequote=array()) {

        $sql = "SELECT MAX(CONVERT(SUBSTRING($field, LENGTH('$prefix')+1, LENGTH($field)-LENGTH('$prefix')-LENGTH('$suffix')), SIGNED))+1
                AS LAST FROM $table WHERE 1 AND LEFT($field, LENGTH('$prefix'))='$prefix' AND RIGHT($field, LENGTH('$suffix'))='$suffix'";
        foreach($seleksi as $item => $value) {
            $sql .= " AND $item";
            if(is_array($value)) {
                $sql .= "IN (";
                foreach($value as $items => $values) {
                    $sql .= (array_search($item, $escapequote)!==false?"$values":"'$values'").",";
                }
                $sql = rtrim($sql, ",").")";

            } else {
                $sql .="=".(array_search($item, $escapequote)!==false?"$value":"'$value'");
            }
        }

        foreach($kecuali as $item => $value) {
            $sql .= " AND $item";
            if(is_array($value)) {
                $sql .= "NOT IN (";
                foreach($value as $items => $values) {
                    $sql .= (array_search($item, $escapequote)!==false?"$values":"'$values'").",";
                }
                $sql = rtrim($sql, ",").")";

            } else {
                $sql .="<>".(array_search($item, $escapequote)!==false?"$value":"'$value'");
            }
        }
        $this->query($sql, $rs_num, $rs);

        $last = isset( $rs[0]["LAST"])?$rs[0]["LAST"]:1;
        $length-=strlen($last);
        for($i=0; $i<$length; $i++) $last="0".$last;

        return $prefix.$last.$suffix;
    }

    public function isDuplicateId($cekId, $tabel, $kolomCekId, $exeptId="", $kolomExeptId="") {
        $kolomExeptId=strlen($kolomExeptId)==0?$kolomCekId:$kolomExeptId;
        $sql = "SELECT COUNT($kolomCekId) AS TOTAL FROM $tabel WHERE $kolomCekId='$cekId' AND $kolomExeptId<>'$exeptId'" ;
        $this->query($sql, $rs_num, $rs);

        return $rs[0]["TOTAL"]>0;
    }

    public function getFieldValue($tabel, $kolom=array(), $seleksi=array(), $seleksi_escapequote=array(), $escapequote=array()) {

        $sql = "SELECT ";
        foreach ($kolom as $key => $value) {
            $sql.="$value,";
        }
        $sql=rtrim($sql,","). " FROM $tabel WHERE 1";

        foreach($seleksi as $item => $value) {
            $sql .= " AND $item";
            if(is_array($value)) {
                $sql .= "IN (";
                foreach($value as $items => $values) {
                    $sql .= (array_search($item, $escapequote)!==false?"$values":"'$values'").",";
                }
                $sql = rtrim($sql, ",").")";

            } else {
                $sql .="=".(array_search($item, $escapequote)!==false?"$value":"'$value'");
            }
        }

        $this->query($sql, $rs_num, $result);

        return $result;
    }
	
	public function konversiTanggal($str) {

        if (strlen($str)==0) return "";

        $temp = explode("-",$str);
        return $temp[2] . "-" . $temp[1] . "-" . $temp[0];

    }

}

function currencyFormat($str) {
//    return number_format($str, 0, '.', ',');

    $number = explode(".",$str);

    return number_format($number[0]) . ($number[1]?("." . $number[1]):"");
}

function diskonFormat($str) {

    $number = explode("+",$str);

    $result="";
    for($i=0; $i<count($number); $i++)
        $result .= ($i>0?"+":"") . currencyFormat(str_replace("%", "", $number[$i])) .
            (strpos($number[$i], "%")?"%":"");

    return $result;
}

function konversiTanggal($str) {

    if (strlen($str)==0) return "";

    $temp = explode("-",$str);
    return $temp[2] . "-" . $temp[1] . "-" . $temp[0];

}

?>