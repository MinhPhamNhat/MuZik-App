<?php 
    require_once('connection.php');
    require_once('function.php');
    
   $data = getPublicPlaylist($dbCon);
    echo json_encode(array('status' => true, 'data' => $data, 'message' => 'Success'), JSON_UNESCAPED_UNICODE );


?>