package com.rs.rxjdbc.app;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromUrl;
import com.github.davidmoten.rx.jdbc.Database;

import rx.Observable;
import rx.Observer;

public class RxJdbcApp1 {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/spring_boot?useSSL=false";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "root";

	public static void main(String[] args) {
		final String CREATE_QUERY = "CREATE TABLE IF NOT EXISTS PERSON (PID INTEGER(10) primary key auto_increment, NAME VARCHAR(20), ADDRESS VARCHAR(50))";
		final String INSERT_QUERY = "INSERT INTO PERSON VALUES (:pid,:name,:address)";
		final String UPDATE_QUERY = "UPDATE PERSON SET ADDRESS=? WHERE PID=?";
		final String DELETE_QUERY = "DELETE FROM PERSON WHERE pid = ?";
		ConnectionProvider connectionProvider = new ConnectionProviderFromUrl(DB_URL, DB_USERNAME, DB_PASSWORD);
		Database database = Database.from(connectionProvider);
		// Observable<Integer> create = database.update(CREATE_QUERY).count();
		// Observable<Integer> insert = database.update(INSERT_QUERY)
		// .parameter("pid", 2)
		// .parameter("name", "Ramesh")
		// .parameter("address", "Hyd")
		// .dependsOn(create)
		// .count();
		// Observable<Integer> update = database.update(UPDATE_QUERY)
		// .parameter("Delhi")
		// .parameter("2")
		// .count();
		Observable<Integer> delete = database.update(DELETE_QUERY).parameter(2).count();
		// Observable<Integer> source = database.update(UPDATE_QUERY).count();
		delete.subscribe(new Observer<Integer>() {

			@Override
			public void onCompleted() {
				System.out.println("onComplete..");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("onError..");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onNext(Integer t) {
				System.out.println("onNext: " + t);

			}
		});

	}

}
