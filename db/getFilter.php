<?php


require_once('connection.php');
require_once('function.php');
if (isset($_GET)) {
    if (isset($_GET['filter'])) {
        $songId = filterSongName($_GET['filter'] ,$dbCon);
        $artistID = filterArtistName($_GET['filter'] ,$dbCon);
        $song = array_merge($songId,$artistID);
        if (count($song) > 0)
        $data = getListSongByID($song,99,$dbCon);
        else
        $data = array();
        echo json_encode(array('status' => true, 'data' => $data, 'message' => 'Load success'), JSON_UNESCAPED_UNICODE );
    } else
        die(json_encode(array('status' => false, 'message' => 'invalid params')));
}
