package com.rs.rxjdbc.app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromUrl;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.QuerySelect;
import com.github.davidmoten.rx.jdbc.QuerySelect.Builder;

import rx.Observable;

public class RxJdbcApp {
	private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/spring_boot?useSSL=false";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "root";

	public static void main(String[] args) {

		ConnectionProvider connectionProvider = new ConnectionProviderFromUrl(DB_CONNECTION, DB_USER, DB_PASSWORD);
		Database db = Database.from(connectionProvider);
		System.out.println(db);
		Observable<Boolean> tx = db.beginTransaction();
		System.out.println(tx);

		QuerySelect.Builder bu = db.select("select username from users where email = 'sowmya@gmail.com'");
		System.out.println(bu.count().subscribe(System.out::println));

		Builder builder = db.select("select * from users");
		Observable<List<Users>> users = builder.get(RxJdbcApp::mapResultSet);
		// users.flatMap(usersList ->
		// Observable.just(usersList.get(0))).forEach(System.out::println);
		// users.forEach(System.out::println);
		// Action1
		// users.forEach(RxJdbcApp::callAction1);
		Observable<Users> source = users.flatMap(Observable::from);
		System.out.println("source: " + source);

		source.map(Users::getEmail).forEach(System.out::println);

		System.out.println("main...");
	}

	private static List<Users> mapResultSet(ResultSet rs) throws SQLException {
		System.out.println("mapResultSet: " + Thread.currentThread());
		final List<Users> usersList = new ArrayList<>();
		while (rs.next()) {
			Users user = new Users();
			user.setUsername(rs.getString("username"));
			user.setCountry(rs.getString("country"));
			user.setEmail(rs.getString("email"));
			user.setEnabled(rs.getInt("enabled"));
			user.setPassword(rs.getString("password"));
			user.setRole(rs.getString("role"));
			usersList.add(user);
		}
		return usersList;
	}

	private static void callAction1(Object obj) {
		System.out.println(obj.getClass());
		System.out.println(obj);
	}

}
