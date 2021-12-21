<?php 
require_once ('connection.php');
require_once ('function.php');
if (isset($_GET)){
    if(isset($_GET['UserID'])){
        if(file_exists("user_ava/".$_GET['UserID'].".png")){
            header("Location: /Muzik/user_ava/".$_GET['UserID'].".png");
        }else
            header("Location: /Muzik/user_ava/default.png");
    }
    die(json_encode(array('status' => false, 'message' => 'invalid params')));
}
?>