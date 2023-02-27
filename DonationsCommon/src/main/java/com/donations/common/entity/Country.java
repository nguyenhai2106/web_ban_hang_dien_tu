package com.donations.common.entity;

import java.util.Set;

import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Nationalized;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "countries")
public class Country {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Nationalized
	@Column(nullable = false, unique = true, length = 64)
	private String name;

	@Column(nullable = false, unique = true, length = 8)
	private String code;

	@OneToMany(mappedBy = "country")
	private Set<State> stateas;

	public Country() {
	}

	public Country(Integer id, String name, String code) {
		this.id = id;
		this.name = name;
		this.code = code;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Set<State> getStateas() {
		return stateas;
	}

	public void setStateas(Set<State> stateas) {
		this.stateas = stateas;
	}

	@Override
	public String toString() {
		return "Country [id = " + id + ", name = " + name + ", code = " + code + "]";
	}

}
