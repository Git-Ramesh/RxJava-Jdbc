package com.rs.rxjdbc.app;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromUrl;
import com.github.davidmoten.rx.jdbc.Database;

import rx.Observable;

public class RxJavaApp3 {
	private static final Logger LOGGER = LoggerFactory.getLogger(RxJavaApp3.class);
	private static final String URL = "jdbc:mysql://localhost:3306/rxjava?useSSL=false";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	public static void main(String[] args) throws URISyntaxException, IOException {

		Database database = null;
		ConnectionProvider connectionProvider = new ConnectionProviderFromUrl(URL, USERNAME, PASSWORD);

		database = Database.from(connectionProvider);
		// Insert CLOB data
		// insertCLOB(database);
		// Read the CLOB data
		readCLOB(database);

	}

	public static void insertCLOB(Database database) throws URISyntaxException, IOException {
		String create_table_query = "create table if not exists document(id integer(10) primary key auto_increment, document LONGTEXT)";
		String insert_query = "insert into document(id, document) values (?, ?)";
		Observable<Integer> createObservable = database.update(create_table_query).count();
		createObservable.subscribe(status -> System.out.println("Status: " + status));
		URL fileUrl = Thread.currentThread().getContextClassLoader().getResource("document.txt");
		File targetFile = new File(fileUrl.toURI());

		System.out.println(targetFile.exists());
		Path path = Paths.get(targetFile.toURI());
		String fileContent = new String(Files.readAllBytes(path));
		database.update(insert_query).parameter(1).parameter(Database.toSentinelIfNull(fileContent)).count()
				.subscribe(status -> System.out.println("Insert Status: " + status));
	}

	public static void readCLOB(Database database) {
		List<String> list = database.select("select document from document where id =? ").parameter(1)
				.getAs(String.class).toList().toBlocking().single();
		System.out.println(list.get(0));
	}
}
