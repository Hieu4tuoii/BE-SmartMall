package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category extends AbstractEntity {

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "slug", length = 255)
    private String slug;

    @Column(name = "image_url")
    private String imageUrl;
}
