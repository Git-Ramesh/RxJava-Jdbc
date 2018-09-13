package com.rs.rxjdbc.app;

import java.util.List;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromUrl;
import com.github.davidmoten.rx.jdbc.Database;

import rx.Observable;


public class RxJdbcApp2 {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/spring_boot?useSSL=false";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "root";

	public static void main(String[] args) {
		ConnectionProvider connectionProvider = new ConnectionProviderFromUrl(DB_URL, DB_USERNAME, DB_PASSWORD);
		Database database = Database.from(connectionProvider);
		
		//Get the number of records
		Observable<Integer> count = database.select("select pid, name, address from PERSON where pid < ?").parameter(4)
				.count();
		count.subscribe(System.out::println);
		Observable<Integer> count1 = database.select("select pid, name, address from PERSON")
					.fetchSize(1).count();
		count1.subscribe(System.out::println);
		Observable<List<String>> source =database.select("select name from PERSON")
				.getAs(String.class)
				.toList();
		Observable<String> names = source.flatMap(Observable::from);
		names.subscribe(System.out::println);
//				.toBlocking()
//				.single();
//		names.parallelStream().forEachOrdered(System.out::println);
		
	}
}
