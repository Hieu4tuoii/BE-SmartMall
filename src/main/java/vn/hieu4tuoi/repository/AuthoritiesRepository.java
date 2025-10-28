package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hieu4tuoi.model.Authorities;

@Repository
public interface AuthoritiesRepository extends JpaRepository<Authorities, String> {
}

