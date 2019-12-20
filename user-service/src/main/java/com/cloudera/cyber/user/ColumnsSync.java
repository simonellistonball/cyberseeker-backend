package com.cloudera.cyber.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cloudera.cyber.user.domain.ColumnDTO;
import com.cloudera.cyber.user.repos.ColumnsRepository;

@SpringBootApplication
public class ColumnsSync implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(ColumnsSync.class);

	@Autowired
	ColumnsRepository repo;

	@Override
	public void run(String... args) throws Exception {
		Iterable<ColumnDTO> allColumns = repo.findAll();
		
		// query the metastore to get the view columns
		
		// compare the two lists and insert any columns not represented in our metastore
		
		
	}

	public static void main(String[] args) {
		SpringApplication.run(ColumnsSync.class, args);
	}

}
