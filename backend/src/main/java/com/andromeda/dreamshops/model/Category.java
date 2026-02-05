package com.andromeda.dreamshops.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Category parentCategory;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Product> products;


    public Category(String name) {
        this.name = name;
    }

    public Category(String name, Category parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
    }
}
