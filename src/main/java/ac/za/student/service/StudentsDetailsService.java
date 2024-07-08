package ac.za.student.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ac.za.student.entity.Students;
import ac.za.student.repository.StudentsRepo;

@Service
public class StudentsDetailsService implements UserDetailsService {

    @Autowired
    private StudentsRepo studentsRepo;
    @Override
    public UserDetails loadUserByUsername(String studentNumber) throws UsernameNotFoundException {
       Students student = studentsRepo.findByStudentNumber(studentNumber)
        .orElseThrow(() -> new UsernameNotFoundException("Student not found"));

        return new User(
            student.getStudentNumber(),
            student.getPassword(),
            getAuthorities(student)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Students student) {
        return Collections.singletonList(new SimpleGrantedAuthority(student.getRole()));
    }
    
}
