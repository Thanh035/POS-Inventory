package com.example.myapp.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.example.myapp.constant.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_user")
public class User extends AbstractAuditingEntity<Long> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 50)
	@Column(length = 50, unique = true, nullable = false)
	private String login;

	@JsonIgnore
	@NotNull
	@Size(min = 60, max = 60)
	@Column(name = "password_hash", length = 60, nullable = false)
	private String password;

	@Size(max = 100)
	@Column(name = "fullname", length = 100)
	private String fullname;

	@Email
	@Size(min = 5, max = 254)
	@Column(length = 254, unique = true, nullable = false)
	private String email;

	@NotNull
	@Column(nullable = false)
	private boolean activated = false;

	@Size(min = 9, max = 11)
	@Column(name = "phone_number", length = 11)
	private String phoneNumber;

	@Size(max = 256)
	@Column(name = "image_url", length = 256)
	private String imageUrl;

	@Size(max = 20)
	@Column(name = "activation_key", length = 20)
	@JsonIgnore
	private String activationKey;

	@Size(max = 20)
	@Column(name = "reset_key", length = 20)
	@JsonIgnore
	private String resetKey;

	@Column(name = "reset_date")
	private Instant resetDate = null;

	@JsonIgnore
	@ManyToMany
	@JoinTable(
			name = "tbl_user_role", 
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), 
			inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id") }
	)
	@BatchSize(size = 20)
	private List<Role> roles = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof User)) {
			return false;
		}
		return id != null && id.equals(((User) o).id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public String toString() {
		return "User{" + "login='" + login + '\'' + ", fullname='" + fullname + '\'' + ", email='" + email + '\''
				+ ", imageUrl='" + imageUrl + '\'' + ", activated='" + activated + '\'' + ", activationKey='"
				+ activationKey + '\'' + "}";
	}

}
