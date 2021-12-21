<?php
require_once ('connection.php');
require_once ('function.php');
if (isset($_GET)){
    if (isset($_GET['id']))
        $data = getArtistBySongID($_GET['id'],$dbCon);
    else{
        die(json_encode(array('status' => false, 'message' => 'invalid params')));
    }
    if ($data == false)
    die(json_encode(array('status' => false, 'message' => 'invalid params')));
    echo json_encode(array('status' => true, 'data' => $data, 'message' => 'Load success'), JSON_UNESCAPED_UNICODE );
}
?>