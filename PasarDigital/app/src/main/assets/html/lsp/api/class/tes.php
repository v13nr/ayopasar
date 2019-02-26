<?php
/**
 * example.php
 * Displays some examples of class.db.php usage
 *
 * @author Bennett Stone
 * @version 1.0
 * @date 18-Feb-2013
 * @package class.db.php
 **/
error_reporting(1);

include "init.php";


echo "TES 4";


$results = mysqli_query($link,"SELECT nama FROM admin_users");




foreach( $results as $row )
{
    //echo $row['nama'] .'<br />';
}

mysqli_close($link);