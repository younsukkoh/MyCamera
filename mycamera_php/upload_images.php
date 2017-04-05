<?php
	require "init.php";
	
	$first_name = "Younsuk";
	$last_name = "Koh";
	$user_name = "younsukkoh";
	$user_password = "hellophp";

	$sql_query = "insert into item values('$first_name', '$last_name', '$user_name', '$user_password');";

	if (mysqli_query($connection, $sql_query)) {
		echo "<h3> Data Insertion Success </h3>";
	}
	else {
		echo "<h3> Data Insertion Error </h3>".mysqli_error($connection);
	}

?>	
