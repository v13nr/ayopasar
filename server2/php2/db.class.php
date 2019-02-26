<?php
 mysql_connect('localhost', 'sogionli_kredit', 'H?#L^)6e06Fc') or die('Tidak terkoneksi dengan server.');
 mysql_select_db('sogionli_kredit') or die('Tidak terkoneksi dengan DB');
 
class DB { 
	var $hostname 	= 'localhost';
	var $username 	= 'sogionli_kredit';
	var $password 	= 'H?#L^)6e06Fc';
	var $db_name	= 'sogionli_kredit';
	var $connection;
	var $errors		= array();
	var $queries	= array();
	var $query		= '';
	var $insert_id	= 0;
	var $debug		= 1;
	
	
	function DB($params=array()) {
		if(!empty($params)) {
			foreach($params as $k=>$v) {
				if(isset($this->{$k})) {
					$this->{$k} = $v;
				}
			}
		}
		
		if($this->hostname != '' && $this->username != '' && $this->db_name != '') {
			$this->connection = mysql_connect('localhost','sogionli_kredit','H?#L^)6e06Fc');
			if(!$this->connection) {
				$this->errors[] = 'Could not connect: '.mysql_error();
				return FALSE;
			}
			if(!mysql_select_db($this->db_name,$this->connection)) {
				$this->errors[] = 'Database error: '.mysql_error();
				return FALSE;
			}
		} else {
			$this->errors[] = 'Wrong database information';
			return FALSE;
		}
		
		if($this->debug && !empty($this->errors)) {
			$this->print_debug($this->errors);
		}
		
	return TRUE;
	}
	
	
	function query($sql) {
		$this->queries[] = $sql;
		$result = mysql_query($sql);
		
		if(!$result) {
			$this->errors[] = 'Invalid query: '.mysql_error();
		}
		if($result===TRUE) {
			$this->insert_id = mysql_insert_id();
		}
		
		if($this->debug && !empty($this->errors)) {
			$this->print_debug($this->errors);
			$this->print_debug($sql);
		}
		
	return $result;
	}
	
	
	function get_rows($sql) {
		$result = $this->query($sql);
		
		$rows = array();
		if($result !== FALSE) {
			while($row = mysql_fetch_array($result,MYSQL_ASSOC)) {
				$rows[] = $row;
			}
		}
	return $rows;
	}
	
	
	function get_row($sql) {
		$result = $this->query($sql);
		$row = array();
		if($result !== FALSE) {
			$row = mysql_fetch_array($result,MYSQL_ASSOC);
		}
	return $row;
	}


	function escape($str) {
		return $str;
	}
	
	
	function insert($table, $data) {

		
		foreach($data as $k=>$v) {
			$data[$k] = $this->escape($v);
		}


		if(!isset($data['created'])) {
			$data['created'] = date("Y-m-d H:i:s");
		}
		

		$fields = $this->get_sql_field_names($table);
		
		foreach($data as $k=>$v) {
			if(!in_array($k,$fields)) {
				unset($data[$k]);
			}
		}


		$keys 	= array_keys($data);
		$values = array_values($data);

		$sql = "INSERT INTO ".$this->escape($table)." (".implode(',',$keys).") VALUES('".implode("','",$values)."')";
		
	return $this->query($sql);
	}
	
	
	function update($table, $data, $id) {

		if(!isset($data['modified'])) {
			$data['modified'] = date("Y-m-d H:i:s");
		}

		$fields = $this->get_sql_field_names($table);
		$update = '';
		foreach($data as $k=>$v) {
			if(in_array($k,$fields)) {
				$update .= "`$k`='".$this->escape($v)."', ";
			}
		}

		$update = substr($update, 0, strrpos($update, ','));

		$sql = "UPDATE ".$this->escape($table)." SET ".$update." WHERE id=".$this->escape($id);

	return $this->query($sql);
	}
	
	function delete($table, $id) {
		

		$sql = "DELETE FROM ".$this->escape($table)." WHERE id=".$this->escape($id);

	return $this->query($sql);
	}


	function get_insert_id() {
		return $this->insert_id;
	}


	function get_sql_field_names($table) {
		$columns = array();
		$rows = $this->get_rows("SHOW COLUMNS FROM ".$table);
		
		foreach($rows as $k=>$v) {
			if(!in_array($v['Field'], $columns)) {
				$columns[] = $v['Field'];
			}
		}
	return $columns;
	}


	function print_debug($debug) {
		echo '<pre>';
		if(is_string($debug)) {
			echo $debug;
		} else {
			print_r($debug);
		}
		echo '</pre>';
	}
}


function db_connect() {

	$db = new DB(array(
		'hostname'=>'localhost',
		'username'=>'sogionli_kredit',
		'password'=>'H?#L^)6e06Fc',
		'db_name'=>'sogionli_kredit'
	));


	if($db===FALSE) {
		print_debug($db->errors);
		exit;
	}

return $db;
}

function cors() {

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
}
//cors();
?>