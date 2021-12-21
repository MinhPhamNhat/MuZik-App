<?php

require_once ('connection.php');
require_once ('function.php');
if (isset($_POST)){
    if(isset($_POST['username']) and isset($_POST['password']) and isset($_POST['email']) and isset($_POST['name'])){
        if(checkUsername($_POST['username'], $dbCon) == "True"){
            die(json_encode(array('status' => false, 'message' => 'Invalid params', 'type' => 'username', 'code' => '1')));
        }else if(checkEmail($_POST['email'], $dbCon) == "True"){
            die(json_encode(array('status' => false, 'message   ' => 'Invalid params', 'type' => 'email', 'code' => '1')));
        }else{
            insertToLogin($_POST['username'],$_POST['password'], $_POST['email'], $_POST['name'], $dbCon);
            echo json_encode(array('status' => true, 'message' => 'Success'));
        }
    }else
    die(json_encode(array('status' => false, 'message' => 'Invalid params')));
}else{
    die(json_encode(array('status' => false, 'message' => 'Invalid method')));
}

?>