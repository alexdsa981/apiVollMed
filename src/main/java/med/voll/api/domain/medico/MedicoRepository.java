package med.voll.api.domain.medico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Page<Medico> findByActivoTrue(Pageable paginacion);

    //selecciona medico random con especialidad especificada y fecha libre
    @Query("""
            SELECT m FROM Medico m
            WHERE m.activo = true and
            m.especialidad=:especialidad and
            m.id not in(
            select c.medico.id from Consulta c
            WHERE c.fecha=:fecha
            )
            order by rand()
            limit 1
            """)
    Medico seleccionarMedicoConEspecialidadEnFecha(Especialidad especialidad, LocalDateTime fecha);

    @Query("""
            SELECT m.activo
            FROM Medico m
            WHERE m.id=:idMedico
            """)
    Boolean findActivoById(Long idMedico);
}
