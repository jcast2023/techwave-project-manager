package com.cibertec;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TechwaveProjectManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechwaveProjectManagerApplication.class, args);
	}
	
	@Bean // Define un bean de ModelMapper para inyección de dependencias
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
	
	//@Bean
	//public PasswordEncoder passwordEncoder() {
	//    return new BCryptPasswordEncoder(); // O cualquier otro PasswordEncoder que prefieras
	//}

}
