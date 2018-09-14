package com.rs.rxjdbc.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromUrl;
import com.github.davidmoten.rx.jdbc.Database;

public class RxJavaApp4 {
	private static final Logger LOGGER = LoggerFactory.getLogger(RxJavaApp4.class);
	private static final String URL = "jdbc:mysql://localhost:3306/rxjava?useSSL=false";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	public static void main(String[] args) throws URISyntaxException, IOException {

		Database database = null;
		ConnectionProvider connectionProvider = new ConnectionProviderFromUrl(URL, USERNAME, PASSWORD);

		database = Database.from(connectionProvider);
		// insertBLOB(database);
		readBLOB(database);
	}

	private static void insertBLOB(Database database) throws URISyntaxException, IOException {
		String createQuery = "create table if not exists images(id int(10), image MEDIUMBLOB) ";
		String insertQuery = "insert into images(id, image) values (?, ?)";
		database.update(createQuery).count().subscribe(System.out::println);
		URL url = Thread.currentThread().getContextClassLoader().getResource("221297.png");
		Path path = Paths.get(url.toURI());
		database.update(insertQuery).parameter(1).parameter(Database.toSentinelIfNull(Files.readAllBytes(path))).count()
				.subscribe(System.out::println);

	}

	private static void readBLOB(Database database) throws IOException {
		String selectQuery = "select image from images where id = ?";
		InputStream is = database.select(selectQuery).parameter(1).get(RxJavaApp4::resultSetMapper).toList()
				.toBlocking().single().get(0);
		File imageFile = new File("src/main/resources/abc.png");
		Files.copy(is, Paths.get(imageFile.toURI()));
	}

	private static InputStream resultSetMapper(ResultSet rs) throws SQLException {
		Blob blob = rs.getBlob(1);
		return blob.getBinaryStream();
	}
}
