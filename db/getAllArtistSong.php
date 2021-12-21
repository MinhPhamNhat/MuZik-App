<?php


require_once ('connection.php');
require_once ('function.php');

    $data = getAllListSong($dbCon);

    echo json_encode(array('status' => true, 'data' => $data, 'message' => 'Load success'), JSON_UNESCAPED_UNICODE );


?>