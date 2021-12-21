<?php
require_once('connection.php');
require_once('function.php');
if (isset($_GET)){
    if(isset($_GET['playlistid']) and isset($_GET['songid'])){
        echo deleteSongFromPlaylist($_GET['playlistid'], $_GET['songid'], $dbCon);
    }else
    die(json_encode(array('status' => false, 'message' => 'invalid params')));
} else
    die(json_encode(array('status' => false, 'message' => 'invalid method')));
    
?>