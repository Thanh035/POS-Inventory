package com.example.myapp.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_category")
public class Category extends AbstractAuditingEntity<Integer> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String name;

    @Size(max = 20)
    @Column(length = 20)
    private String code;

    @Size(max = 255)
    @Column(length = 255)
    private String Description;

    @OneToMany(mappedBy = "category" ,targetEntity = SubCategory.class)
    private List<SubCategory> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category" ,targetEntity = Product.class)
    private List<Product> products = new ArrayList<>();

}
