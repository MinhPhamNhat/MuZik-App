<?php
require_once('connection.php');
require_once('function.php');
if (isset($_GET)){
    if(isset($_GET['playlistid'])){
        echo deletePlaylist($_GET['playlistid'], $dbCon);
    }else
    die(json_encode(array('status' => false, 'message' => 'invalid params')));
} else
    die(json_encode(array('status' => false, 'message' => 'invalid method')));
    
?>