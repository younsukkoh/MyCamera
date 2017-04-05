<?php
	$db_name = "mycamera";
	$mysql_user_name = "root";
	$mysql_password = "";
	$server_name = "localhost";

	$connection = mysqli_connect($server_name, $mysql_user_name, $mysql_password, $db_name);
	if (!$connection) {
		echo "Connection Error ".mysqli_connect_error();
	}
	else {
		echo "<h3> Database Connection Success <h3>";
	}
?>