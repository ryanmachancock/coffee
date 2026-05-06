package com.projects.coffee.service;

import com.projects.coffee.dto.RegistrationDTO;
import com.projects.coffee.entity.Login;
import com.projects.coffee.entity.Person;
import com.projects.coffee.repository.LoginRepository;
import com.projects.coffee.repository.PersonRepository;
import com.projects.coffee.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginRepository loginRepository;
    private final PersonRepository personRepository;

    public AuthService(PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       LoginRepository loginRepository,
                       PersonRepository personRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.loginRepository = loginRepository;
        this.personRepository = personRepository;
    }

    public boolean isLoginSuccessful(Login login) {
        Login dbLogin = loginRepository.findByUsername(login.getUsername());
        return dbLogin != null && passwordEncoder.matches(login.getPassword(), dbLogin.getPassword()) && login.getUsername().equals(dbLogin.getUsername());
    }

    public boolean isRegisterSuccessful(RegistrationDTO registrationDTO) {
        if (loginRepository.findByUsername(registrationDTO.getUsername()) != null) {
            return false;
        }
        Person person = new Person();
        person.setFirstName(registrationDTO.getFirstName());
        person.setLastName(registrationDTO.getLastName());
        person.setUsername(registrationDTO.getUsername());
        person = personRepository.save(person);

        Login login = new Login();
        login.setUsername(registrationDTO.getUsername());
        login.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        login.setPerson(person);
        loginRepository.save(login);

        return true;
    }

    public String authenticateUser(Login loginRequest) {
        Login login = loginRepository.findByUsername(loginRequest.getUsername());
        if (login != null && passwordEncoder.matches(loginRequest.getPassword(), login.getPassword())) {
            return jwtUtil.generateToken(login.getUsername());
        }
        return "Invalid credentials";
    }
}
