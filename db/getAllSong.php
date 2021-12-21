<?php


require_once ('connection.php');
require_once ('function.php');
    $queryBaiHat = 'SELECT * FROM BaiHat';

    try{
        $stmt = $dbCon->prepare($queryBaiHat);
        $stmt->execute();
    }
    catch(PDOException $ex){
        die(json_encode(array('status' => false, 'message' => $ex->getMessage())));
    }

    $data = array();
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
        $data[] =  $row ;
    }

    echo json_encode(array('status' => true, 'data' => $data, 'message' => 'Load success'), JSON_UNESCAPED_UNICODE );


?>