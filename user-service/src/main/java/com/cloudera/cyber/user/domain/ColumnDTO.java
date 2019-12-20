package com.cloudera.cyber.user.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.cloudera.cyber.user.Column;
import com.cloudera.cyber.user.ColumnType;

import lombok.Data;

@Data
@Entity
@Table(name="columns")
public class ColumnDTO implements Column {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	private String name;
	private ColumnType type;
	private String label;
}
