<?php
require_once ('connection.php');
require_once ('function.php');
if (isset($_GET)){
    if((isset($_GET['userid']) and isset($_GET['playlistname']))){
        insertToPlaylist($_GET['userid'], $_GET['playlistname'], $dbCon);
        echo json_encode(array('status' => true, 'message' => 'success'));
    }else
    die(json_encode(array('status' => false, 'message' => 'Invalid params')));
}else{
    die(json_encode(array('status' => false, 'message' => 'Invalid method')));
}
?>