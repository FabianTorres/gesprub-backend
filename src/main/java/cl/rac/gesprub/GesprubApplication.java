package cl.rac.gesprub;

import java.sql.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.rac.gesprub.Entidad.Autenticacion;
import cl.rac.gesprub.Entidad.Usuario;
import cl.rac.gesprub.Repositorio.AutenticacionRepository;
import cl.rac.gesprub.Repositorio.UsuarioRepository;

@SpringBootApplication
public class GesprubApplication {

	public static void main(String[] args) {
		SpringApplication.run(GesprubApplication.class, args);
		
	}
	
	
	@Bean
	@Profile("dev")
    public CommandLineRunner createTestUser(
            UsuarioRepository usuarioRepo,
            AutenticacionRepository authRepo,
            PasswordEncoder passwordEncoder // Inyectamos el codificador que creamos en SecurityConfig
    ) {
        return args -> {
            // Revisa si el usuario de prueba ya existe para no crearlo cada vez
            if (usuarioRepo.findByNombreUsuario("testuser").isEmpty()) {
                // 1. Crear la entidad Usuario
                Usuario testUser = new Usuario();
                testUser.setNombreUsuario("testuser");
                testUser.setCorreo("test@test.com");
                testUser.setRolUsuario("ADMIN"); // O el rol que corresponda
                testUser.setActivo(1);
                testUser.setFechaCreacion(new Date(System.currentTimeMillis())); // Fecha actual
                
                // Primero guardamos el usuario para que JPA le asigne un ID
                usuarioRepo.save(testUser);

                // 2. Crear la entidad Autenticacion
                Autenticacion auth = new Autenticacion();
                auth.setUsuario(testUser); // Establecemos la relación con el usuario ya guardado
                
                // ¡EL PASO CLAVE! Hasheamos la contraseña antes de guardarla
                auth.setPassword(passwordEncoder.encode("prueba123"));
                
                authRepo.save(auth);
                
                System.out.println("====================================================================");
                System.out.println(">>> Usuario de prueba 'testuser' creado con contraseña 'prueba123' <<<");
                System.out.println("====================================================================");
            }
        };
    }

}
