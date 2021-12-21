<?php
require_once('connection.php');

    function getArtistName($artistID, $dbCon){
        $query = "SELECT TenCaSi FROM CaSi WHERE CaSiID='".$artistID."'";
        try{
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        }
        catch(PDOException $ex){
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $name = $stmt->fetch(PDO::FETCH_ASSOC);
        
        return $name['TenCaSi'];
    }

    
    function getArtistBySongID($songID, $dbCon){
        $query = "SELECT CaSiID FROM CaSiHatBaiHat WHERE BaiHatID='".$songID."'";
        try{
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        }
        catch(PDOException $ex){
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $data = array();
        while($row = $stmt->fetch(PDO::FETCH_ASSOC)){
            $data[] = $row['CaSiID'];
        }
        return $data;
    }

    function getSongByID($songID, $dbCon){
        $query = "SELECT * FROM BaiHat WHERE BaiHatID='".$songID."'";
        try{
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        }
        catch(PDOException $ex){
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $song = $stmt->fetch(PDO::FETCH_ASSOC);
        
        return $song;
    }

    function getArtistByID($artistID, $dbCon){
        $query = "SELECT * FROM CaSi WHERE CaSiID='".$artistID."'";
        try{
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        }
        catch(PDOException $ex){
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $artist = $stmt->fetch(PDO::FETCH_ASSOC);
        
        return $artist;
    }

    function getAllListSong($dbCon){
        $queryBaiHat = "SELECT * FROM BaiHat";

        try{
            $stmt = $dbCon->prepare($queryBaiHat);
            $stmt->execute();
        }
        catch(PDOException $ex){
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
    
        $data = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC))
    
        {
            $artists = getArtistBySongID($row['BaiHatID'], $dbCon);
            foreach ($artists as $artist){
                $artistName = getArtistName($artist, $dbCon);
                $row['CaSi'][] = array('CaSiID' => $artist, 'TenCaSi' => $artistName);
            }
            $data[] =  $row ;
        }
    
        return $data;
    }

    function getListSongByID($ID, $top, $dbCon){
        $listOfID = implode(",",$ID);
        $queryBaiHat = "SELECT * FROM BaiHat WHERE BaiHatID  IN (".$listOfID.") ORDER BY LuotNghe DESC LIMIT ".$top."";

    try{
        $stmt = $dbCon->prepare($queryBaiHat);
        $stmt->execute();
    }
    catch(PDOException $ex){
        die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
    }

    $data = array();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC))

    {
        $artists = getArtistBySongID($row['BaiHatID'], $dbCon);
        foreach ($artists as $artist){
            $artistName = getArtistName($artist, $dbCon);
            $row['CaSi'][] = array('CaSiID' => $artist, 'TenCaSi' => $artistName);
        }
        $data[] =  $row ;
    }

    return $data;
    }

    function checkUsername($username, $dbCon){
        $query = "SELECT UserID from KhachHang WHERE UserID = '".$username."'";
        try{
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        }catch(PDOException $ex){
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $userId =  $stmt->fetch(PDO::FETCH_ASSOC);
        if (is_array($userId)){
            return "True";
        }
        return "False";
    }

    function checkEmail($email, $dbCon){
        $query = "SELECT Email from KhachHang WHERE Email = '".$email."'";
        try{
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        }catch(PDOException $ex){
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $email =  $stmt->fetch(PDO::FETCH_ASSOC);
        if (is_array($email)){
            return "True";
        }
        return "False";
    }

    function insertToLogin($username, $password, $email, $tenuser, $dbCon){
        $dataFrom = array(
            'UserID' => $username,
            'Pass' => $password,
            'TenUser' => $tenuser,
            'Email' => $email
        );
        $query = "INSERT INTO KhachHang (UserID, Pass, TenUser, Email) VALUES (:UserID, :Pass, :TenUser, :Email)";
        $stmt= $dbCon->prepare($query);
        $stmt->execute($dataFrom);
    }

    function login($username, $password, $dbCon){
        $query = "SELECT UserID, Email, TenUser from KhachHang WHERE UserID='".$username."' AND Pass='".$password."'";
        try{
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        }catch(PDOException $ex){
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $userId =  $stmt->fetch(PDO::FETCH_ASSOC);
        if (is_array($userId)){
            echo json_encode(array('status' => true, 'data'=> $userId, 'message' => 'successed'), JSON_UNESCAPED_UNICODE);
            return;
        }
        echo json_encode(array('status' => false, 'message' => 'failed'));
    }

    function filterSongName($songName, $dbCon){
        $query = 'SELECT BaiHatID FROM BaiHat WHERE TenBaiHat like \'%' . $songName . '%\'';
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $listID = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $listID[] =  "'".$row['BaiHatID']."'";
        }
        return $listID;
    }

    function filterArtistName($artistName, $dbCon){
        $query = 'SELECT CaSiID FROM CaSi WHERE TenCaSi like \'%' . $artistName . '%\'';
        
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }

        $listID = array();
        while ($casiid = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $query2 = 'SELECT BaiHatID FROM CaSiHatBaiHat WHERE CaSiID=\''.$casiid['CaSiID'].'\'';
            try {
                $stmt = $dbCon->prepare($query2);
                $stmt->execute();
            } catch (PDOException $ex) {
                die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
            }
            while ($baihatid = $stmt->fetch(PDO::FETCH_ASSOC)) {
                $listID[] =  "'".$baihatid['BaiHatID']."'";
            }
        }
        return $listID;
    }
    
    function getPlaylistById($PlaylistId, $num ,$dbCon){
        $query = "SELECT BaiHatID FROM PlaylistBaiHat WHERE PlaylistID='".$PlaylistId."' LIMIT ".$num."";
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $baihat = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $baihat[] =  "'".$row['BaiHatID']."'";
        }
        return $baihat;
    }
    
    function getPublicPlaylist($dbCon){
        $query = "SELECT * FROM Playlist WHERE PlaylistID like 'PUBPL%'";
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $playlist = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $playlist[] =  $row;
        }
        return $playlist;
    }
    
    function getPlaylistByUserId($UserId, $dbCon){
        $query = "SELECT PlaylistID FROM UserPlaylist WHERE UserID='".$UserId."'";
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $playlist = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $playlist[] =  $row['PlaylistID'];
        }
        return $playlist;
    }
    
    function getPlaylistName($PlaylistID, $dbCon){
        $query = "SELECT PlaylistName FROM Playlist WHERE PlaylistID='".$PlaylistID."'";
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        return $row['PlaylistName'];
    }
    
    function insertToPlaylist($UserID, $PlaylistName, $dbCon ){
        $query = "SELECT CAST(SUBSTRING((SELECT PlaylistID FROM Playlist WHERE PlaylistID like 'PL0%' ORDER BY PlaylistID DESC LIMIT 1), 3,6) AS INT)+1 AS ID";
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $PlaylistID =  "PL000".$row['ID'];
        
        $dataFrom = array(
            'PlaylistID' => $PlaylistID,
            'PlaylistName' => $PlaylistName
        );
        $query = "INSERT INTO Playlist (PlaylistID, PlaylistName) VALUES (:PlaylistID, :PlaylistName)";
        $stmt= $dbCon->prepare($query);
        $stmt->execute($dataFrom);
        
        $dataFrom = array(
            'UserID' => $UserID,
            'PlaylistID' => $PlaylistID
        );
        $query = "INSERT INTO UserPlaylist (UserID, PlaylistID) VALUES (:UserID, :PlaylistID)";
        $stmt= $dbCon->prepare($query);
        $stmt->execute($dataFrom);
    }
    
    
    function editPlaylist($PlaylistID, $PlaylistName, $dbCon){
        $query = "UPDATE Playlist SET PlaylistName='".$PlaylistName."' WHERE PlaylistID='".$PlaylistID."'";
         try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        echo json_encode(array('status' => true, 'message' => "success"));
    }
    
    function deletePlaylist($PlaylistID, $dbCon){
        $query = "DELETE FROM PlaylistBaiHat WHERE PlaylistID='".$PlaylistID."'";
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $query = "DELETE FROM UserPlaylist WHERE PlaylistID='".$PlaylistID."'";
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        $query = "DELETE FROM Playlist WHERE PlaylistID='".$PlaylistID."'";
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        echo json_encode(array('status' => true, 'message' => "success"));
    }
    
    function addSongToPlaylist($PlaylistID, $songID, $dbCon){
        $query = "INSERT INTO PlaylistBaiHat(PlaylistID, BaiHatID) VALUES ('".$PlaylistID."', '".$songID."')";
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        echo json_encode(array('status' => true, 'message' => "success"));
    }
    
    function deleteSongFromPlaylist($PlaylistID, $songID, $dbCon){
        $query = "DELETE FROM PlaylistBaiHat WHERE BaiHatID='".$songID."' AND PlaylistID='".$PlaylistID."'";
        try {
            $stmt = $dbCon->prepare($query);
            $stmt->execute();
        } catch (PDOException $ex) {
            die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
        }
        echo json_encode(array('status' => true, 'message' => "success"));
    }
?>