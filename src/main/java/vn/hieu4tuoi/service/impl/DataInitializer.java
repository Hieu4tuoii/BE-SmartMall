package vn.hieu4tuoi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import vn.hieu4tuoi.common.StepActive;
import vn.hieu4tuoi.common.UserStatus;
import vn.hieu4tuoi.model.Authorities;
import vn.hieu4tuoi.model.User;
import vn.hieu4tuoi.repository.AuthoritiesRepository;
import vn.hieu4tuoi.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer {

    private final AuthoritiesRepository authoritiesRepository;
    private final UserRepository userRepository;
    private  final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(AuthoritiesRepository authoritiesRepository, UserRepository userRepository,  BCryptPasswordEncoder passwordEncoder) {
        this.authoritiesRepository = authoritiesRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        // Create authority if it doesn't exist
        Authorities adminAuthority;
        Optional<Authorities> existingAuthority = authoritiesRepository.findById("ROLE_ADMIN");

        if (existingAuthority.isEmpty()) {
            adminAuthority = new Authorities("ROLE_ADMIN", new ArrayList<>());
            authoritiesRepository.save(adminAuthority);
        } else {
            adminAuthority = existingAuthority.get();
        }


        //tạo role customer
        Authorities customerAuthority;
        Optional<Authorities> existingCustomerAuthority = authoritiesRepository.findById("ROLE_CUSTOMER");
        if (existingCustomerAuthority.isEmpty()) {
             customerAuthority = new Authorities("ROLE_CUSTOMER", new ArrayList<>());
            authoritiesRepository.save(customerAuthority);
        }else {
             customerAuthority = existingCustomerAuthority.get();
        }

        //tao role employee
        Authorities employeeAuthority;
        Optional<Authorities> existingEmployeeAuthority = authoritiesRepository.findById("ROLE_EMPLOYEE");
        if (existingEmployeeAuthority.isEmpty()) {
            employeeAuthority = new Authorities("ROLE_EMPLOYEE", new ArrayList<>());
            authoritiesRepository.save(employeeAuthority);
        }else {
            employeeAuthority = existingEmployeeAuthority.get();
        }

        // Create admin user if it doesn't exist
        if (userRepository.findByEmailAndIsDeletedFalse("admin@gmail.com") == null) {
            List<Authorities> authoritiesList = new ArrayList<>();
            authoritiesList.add(adminAuthority);

            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setAuthorities(authoritiesList);
            admin.setStepActive(StepActive.ACTIVE);
            admin.setStatus(UserStatus.ACTIVE);

            userRepository.save(admin);
        }
    }
}
