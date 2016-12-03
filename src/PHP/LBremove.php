<?php
header('Content-Type: json');
$post = file_get_contents('php://input');
$data = json_decode($post);
$lbUser = $data->user;//user
$lbPass = $data->password;//password
$host = "76.94.123.147";//server IP
$port = 4910;//mySQL port
$socket = "";//not used
$user = "491user";//server username
$password = "password1";//server password
$dbname = "longbox";//removes need to have database.tableName
$issues = $data->issues;

// connect to database
$con = new mysqli ( $host, $user, $password, $dbname, $port, $socket ) or 
die ( json_encode('Could not connect to the database server, ' . mysqli_connect_error () ));

//start the process of checking the username and password
$query = "SELECT COUNT(*) FROM `longbox`.`login` WHERE `userName` = '$lbUser' AND `password` = '$lbPass';";
//echo($query . '<br>');

//send query
$results = mysqli_query($con, $query);

if(!$results){//on fail
	die(json_encode('failed on id query, ' . $con->error));
}

//save results
$row = mysqli_fetch_array($results, MYSQLI_BOTH);
//var_dump($row);
if($row[0] == 0){
	die (json_encode("Incorrect username/password"));
}

$updated = 0;//track updated rows

//read in each issue id and remove it from the DB
foreach($issues as $currID){
	$query = "DELETE FROM issues WHERE userName = '$lbUser' AND id = '$currID';";
//	echo($query . '<br>');
	if (mysqli_query($con, $query) === TRUE){
		$updated = $updated + mysqli_affected_rows($con);
	}
}
//Return the number of updated rows
$retVal = array('updated' => $updated);
echo json_encode($updated);
?>