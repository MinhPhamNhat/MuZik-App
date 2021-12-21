<?php 
    require_once('connection.php');
    require_once('function.php');
    
    if(isset($_GET)){
       if (isset($_GET['id'])) {
            $playlist = getPlaylistByUserId($_GET['id'], $dbCon);
            $data = array();
            foreach($playlist as $p){
                $songid = getPlaylistByID($p, 999, $dbCon);
                $song = array();
                if(count($songid)>0)
                $song = getListSongByID($songid, 999, $dbCon);
                $data[] = array('PlaylistID' => $p, 'PlaylistName'=> getPlaylistName($p, $dbCon),'items'=>$song);
            }
        echo json_encode(array('status' => true, 'data' => $data, 'message' => 'Success'), JSON_UNESCAPED_UNICODE );
       }
        die(json_encode(array('status' => false, 'message' => 'invalid params')));

    } else
        die(json_encode(array('status' => false, 'message' => 'invalid params')));

?>