<?php
/**
This PHP will return a json with the field id_list that has an array of off id's
of id's of comics that were entered after the passed timeStamp;

json body should contain the following:
{
	"password" : "password",
	"user" : "userName",
	"timeStamp" : "yyyy-mm-dd hh:mm:ss"
}

the json will be returned as :

{
	"id_list":
	["5855","5856","5857","5858","9999"]
}

*/
//read json body
$post = file_get_contents('php://input');
//read variables from body
$data = json_decode($post);
$lbUser = $data->user;//user
$lbPass = $data->password;//password
$lbTime = $data->timeStamp;//min time stamp

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

/*
	Verify user name and password
*/
// declare password check statement
$query = "SELECT password from `longbox`.`login` WHERE userName = '$lbUser'";

$result = "reset";//clearing status results, 1 good, 0 bad
$passwordResult = "this should not be here";//debug purpose
if ($stmt = $con->prepare ( $query )) {//build query
	$result = $stmt->execute ();//send query, record swucess/fail in $result
	if(!$result){//if query failed
		die('failed getting password,' . $con->error); 
	}
	$stmt->bind_result ( $passwordResult );//assign returned value
	$stmt->fetch ();//store password into above line
	$stmt->close ();//close statement
}

// if password given isn't same query returns, kill
if ($passwordResult != $lbPass) {
	die ( json_encode('User password is incorrect, ' . $con->error));
}

$now = new DateTime ();//get current time
$now->setTimezone ( new DateTimeZone ( 'America/Los_Angeles' ) );//format to region
$tStamp = $now->format ( 'Y-m-d H:i:s' );//readable format

// update login timestamp
$query = "UPDATE login SET lastLogin ='$tStamp' WHERE userName ='$lbUser';";

//send the login time stamp
$result = "reset";//clear query results
// send statement using the same connection, don't check response
if ($stmt = $con->prepare ( $query )) {
	$result = $stmt->execute (); // send
	$stmt->close (); // close
	if(!$result){//on fail
		die(json_encode('failed on timestamp, ' . $con->error));
	}
}

$arr = array();//make a temporary array to hold id's

//build a query to get the id's to sync
$query = "SELECT `id` FROM `longbox`.`issues` WHERE `userName` = '$lbUser' AND `timestamp` > '$lbTime';";

//store the results
$results = mysqli_query($con, $query);

if(!$results){//on fail
	die(json_encode('failed on id query, ' . $con->error));
}

//fetch and write results to the temp array
while($row = mysqli_fetch_assoc($results)){
	$arr[] = $row['id'];
}

//write temp array to associate array
$assocArr['id_list'] = $arr;
echo json_encode($assocArr);//format assoc array and return as json

?>