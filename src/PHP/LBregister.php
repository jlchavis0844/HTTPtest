<?php

//read json body
$post = file_get_contents('php://input');
//read variables from body
$data = json_decode($post);
$lbUser = $data->user;//user
$lbPass = $data->password;//password

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

$query = "SELECT COUNT(*) FROM `longbox`.`login` WHERE `userName` = '$lbUser';";

$results = mysqli_query($con, $query);

if(!$results){//on fail
	die(json_encode('failed on id query, ' . $con->error));
}

//save results
$row = mysqli_fetch_array($results, MYSQLI_BOTH);

if($row[0] == 0){//if count is zero, user name does not exist
	/**
		Generate time stamp for the log in table.
	*/
	$now = new DateTime ();
	$now->setTimezone ( new DateTimeZone ( 'America/Los_Angeles' ) );
	$tStamp = $now->format ( 'Y-m-d H:i:s' );

	//query string to insert a new user
	$query = "INSERT INTO `longbox`.`login` (`userName`, `password`) VALUES ('$lbUser', '$lbPass');
			";
	
	//print $query . "<br>";
	$results = mysqli_query($con, $query);
	
	if(!$results){//check for a failed connection
		die(json_encode('registration failed, ' . $con->error));//insert failed, true error
	} else echo json_encode('registration worked');//it worked
} else {//user exists, not a true error, just not a sucess.
	echo json_encode("registration failed, user name $lbUser exists");
}	





?>