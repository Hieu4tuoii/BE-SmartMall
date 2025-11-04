package vn.hieu4tuoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "banner")
public class Banner extends AbstractEntity {

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "link")
    private String link;
}


