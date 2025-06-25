package cl.rac.gesprub.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rac.gesprub.Entidad.Autenticacion;
import cl.rac.gesprub.Entidad.Usuario;

@Repository
public interface AutenticacionRepository extends JpaRepository<Autenticacion, Long>{

	
    // Permite buscar la autenticación asociada a un objeto Usuario.
    // Esto funciona si en tu entidad Autenticacion tienes una relación @OneToOne a Usuario.
    Optional<Autenticacion> findByUsuario(Usuario usuario);
}
