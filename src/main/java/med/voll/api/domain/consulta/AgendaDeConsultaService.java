package med.voll.api.domain.consulta;

import med.voll.api.domain.consulta.validaciones.HorarioDeAnticipacion;
import med.voll.api.domain.consulta.validaciones.ValidadorDeConsultas;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.domain.paciente.PacienteRepository;
import med.voll.api.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaDeConsultaService {
    //si no ponemos autowired el valor será nullo, además spring ya sabe donde inyectar
    @Autowired
    private ConsultaRepository consultaRepository;
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private MedicoRepository medicoRepository;

    //lista que recibe interfaz, al colocar autowired spring sabe que todos los elementos que apliquen
    //la interfaz serán inyectadas, por lo que si se agregan o eliminan inyecciones no intervendriamos en esta clase,
    //solo las clases validadoras
    @Autowired
    List<ValidadorDeConsultas> validadores;

    public DatosDetalleConsulta agendar(DatosAgendarConsulta datos){
        if (!pacienteRepository.findById(datos.idPaciente()).isPresent()){
            throw new ValidacionDeIntegridad("Este id para el paciente no fue encontrado");
        }
        if (datos.idMedico()!=null && !medicoRepository.existsById(datos.idMedico())){
            throw new ValidacionDeIntegridad("Este id para el medico no fue encontrado");

        }

        //validaciones
        validadores.forEach(v->v.validar(datos));



        //.get() para que sea del tipo Paciente y no un Optional<Paciente>
        var paciente = pacienteRepository.findById(datos.idPaciente()).get();

        var medico = seleccionarMedico(datos);
        if (medico==null){
            throw new ValidacionDeIntegridad("No existen medicos disponibles para este horario y especialdiad");
        }

        var consulta = new Consulta(null, medico, paciente, datos.fecha());

        consultaRepository.save(consulta);
        return new DatosDetalleConsulta(consulta);
    }

    private Medico seleccionarMedico(DatosAgendarConsulta datos) {
        if (datos.idMedico() != null){
            return medicoRepository.getReferenceById(datos.idMedico());
        }
        if (datos.especialidad() == null){
            throw new ValidacionDeIntegridad("Debe seleccionarse una especialidad para el medico");
        }
        return medicoRepository.seleccionarMedicoConEspecialidadEnFecha(datos.especialidad(),datos.fecha());
    }
}
