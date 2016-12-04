<?php
header('Content-Type: json');
//read json body
$post = file_get_contents('php://input');
//echo("dumping post");
//var_dump($post);
// var_dump($_POST);
//echo("dumping data");
$data = json_decode($post);
//var_dump($data);
$lbUser = $data->user;//user
$lbPass = $data->password;//password
//echo("user: " . $lbUser);
//echo("password: " . $lbPass);
// SQL server declerations
$host = "76.94.123.147";//server IP
$port = 4910;//mySQL port
$socket = "";//not used
$user = "491user";//server username
$password = "password1";//server password
$dbname = "longbox";//removes need to have database.tableName

// connect to database
$con = new mysqli ( $host, $user, $password, $dbname, $port, $socket ) or 
	die ( json_encode('Could not connect to the database server, ' . mysqli_connect_error () ));

$query = "SELECT COUNT(*) FROM `longbox`.`login` WHERE `userName` = '$lbUser' AND `password` = '$lbPass';";

$results = mysqli_query($con, $query);

if(!$results){//on fail
	die(json_encode('failed on id query, ' . $con->error));
}

//save results
$row = mysqli_fetch_array($results, MYSQLI_BOTH);
//var_dump($row);
if($row[0] == 0){
	echo json_encode(false);
} else echo json_encode(true);
?>