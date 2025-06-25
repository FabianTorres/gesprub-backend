package cl.rac.gesprub.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rac.gesprub.Entidad.Jira;

@Repository
public interface JiraRepository extends JpaRepository<Jira, Long>{

}
