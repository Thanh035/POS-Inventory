package com.example.myapp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.example.myapp.constant.Constants;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class UserDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 100)
    private String fullname;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    private boolean activated = false;

    @Size(min = 9, max = 11)
    private String phoneNumber;

    @Size(max = 256)
    private String imageUrl;

    private List<String> roles;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.email = user.getEmail();
        this.fullname = user.getFullname();
        this.imageUrl = user.getImageUrl();
        this.activated = user.isActivated();
        this.phoneNumber = user.getPhoneNumber();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.roles = user.getRoles().stream().map(Role::getCode).collect(Collectors.toList());
    }

}
