<?php

$post = file_get_contents('php://input');
#print_r($post);
$data = json_decode($post);
$lbUser = $data->user;
$lbPass = $data->password;
#print_r($data->id_list);

$idArr = [];

foreach ($data->id_list as $name) {
    $idArr[] = $name->id;

}

#print($lbUser);
#print($lbPass);
#print_r($idArr);

// SQL server declerations
$host = "76.94.123.147";//server IP
$port = 4910;//mySQL port
$socket = "";//not used
$user = "491user";//server username
$password = "password1";//server password
$dbname = "longbox";//removes need to have database.tableName

// connect to database
$con = new mysqli ( $host, $user, $password, $dbname, $port, $socket ) or die ( json_encode('Could not connect to the database server' . mysqli_connect_error () ));

/*
	Verify user name and password
*/
// declare password check statement
$query = "SELECT password from `longbox`.`login` WHERE userName = '$lbUser'";
#print($query);
$result = "reset";//clearing status results, 1 good, 0 bad
$passwordResult = "this should not be here";//debug purpose
if ($stmt = $con->prepare ( $query )) {//build query
	$result = $stmt->execute ();//send query, record swucess/fail in $result
	if(!$result){//if query failed
		die(json_encode('failed getting password' . $con->error)); 
	}
	$stmt->bind_result ( $passwordResult );//assign returned value
	$stmt->fetch ();//store password into above line
	$stmt->close ();//close statement
}

// if password given isn't same query returns, kill
if ($passwordResult != $lbPass) {
	#print('given password: ' . $lbPass . '- pass result: ' . $passwordResult);
	die ( json_encode('User password is incorrect' . $con->error));
}

$now = new DateTime ();
$now->setTimezone ( new DateTimeZone ( 'America/Los_Angeles' ) );
$tStamp = $now->format ( 'Y-m-d H:i:s' );

// update login timestamp
$query = "UPDATE login SET lastLogin ='$tStamp' WHERE userName ='$lbUser';";

//send the login time stamp
$result = "reset";//clear query results
// send statement using the same connection, don't check response
if ($stmt = $con->prepare ( $query )) {
	$result = $stmt->execute (); // send
	$stmt->close (); // close
	if(!$result){//on fail
		die(json_encode('failed on timestamp' . $con->error));
	}
}

$assocArr = array();
#$query = "";
#print("starting loop<br>");
foreach ($idArr as $arrId) {
	$query = "INSERT INTO `longbox`.`issues` (`userName`, `id`) VALUES ('$lbUser', '$arrId');";
	#print($query . "<br>");
	$results = mysqli_query($con, $query);
	#$query = "";
	if(!$results){
		$assocArr[$arrId] = "insert error, $con->error";
	} else {
		$assocArr[$arrId] = "$tStamp";
	}
}

#print_r($assocArr);

echo json_encode($assocArr);

?>