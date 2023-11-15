package com.example.myapp.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_role")
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(max = 50)
	@Column(length = 50)
	private String name;

	@Size(max = 20)
	@Column(length = 20)
	private String code;

	@ManyToMany(mappedBy = "roles")
	private List<User> users = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Role)) {
			return false;
		}
		return Objects.equals(name, ((Role) o).name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "Authority{" + "name='" + name + '\'' + "}";
	}
}
