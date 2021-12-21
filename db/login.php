<?php

require_once ('connection.php');
require_once ('function.php');
if (isset($_POST)){
    if(isset($_POST['username']) and isset($_POST['password']))
        login($_POST['username'], $_POST['password'], $dbCon);
    else
    die(json_encode(array('status' => false, 'message' => 'Invalid params')));
}else{
    die(json_encode(array('status' => false, 'message' => 'Invalid method')));
}

?>