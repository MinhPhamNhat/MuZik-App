<?php 
    require_once('connection.php');
    require_once('function.php');
    
    if(isset($_GET)){
       if (isset($_GET['id']) and isset($_GET['num'])) {
        $songs = getPlaylistByID($_GET['id'], $_GET['num'], $dbCon);
        $data = getListSongByID($songs, $_GET['num'], $dbCon);
        echo json_encode(array('status' => true, 'data' => $data, 'message' => 'Success'), JSON_UNESCAPED_UNICODE );
    } else
        die(json_encode(array('status' => false, 'message' => 'invalid params')));
}
?>