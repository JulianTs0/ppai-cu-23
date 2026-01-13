package com.mycompany.ppai_cu_23.refactor;

import com.mycompany.ppai_cu_23.models.CambioDeEstado;
import com.mycompany.ppai_cu_23.models.EventoSismico;
import com.mycompany.ppai_cu_23.models.Usuario;
import com.mycompany.ppai_cu_23.persistance.DataBaseService;
import com.mycompany.ppai_cu_23.utils.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "estados")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String nombre;

    @Column(nullable = false)
    public String ambito;

    public Estado(String ambito, String nombre) {
        this.ambito = ambito;
        this.nombre = nombre;
    }

    // Transiciones

    public void adquirirDatos(){
        System.out.println("Metodo no implementado");
    }

    public void cerrar(){
        System.out.println("Metodo no implementado");
    }

    public void confirmar(){
        System.out.println("Metodo no implementado");
    }

    public void rechazar(Usuario usuario, LocalDateTime fechaHoraActual, EventoSismico evento, List<CambioDeEstado> cambiosDeEstado){

        // buscar CAMBIO-ACTUAL
        CambioDeEstado cambioDeEstadoActual = this.buscarCambioDeEstadoActual(cambiosDeEstado);
        // fechahorafin al CAMBIO-ACTUAL
        cambioDeEstadoActual.setFechaHoraFin(fechaHoraActual);
        // Crear estado
        Estado estadoRechazado = new Rechazado(Enums.nombresAmbito.Evento_Sismico.name(), Enums.nombresEstado.Rechazado.name());
        // crear CAMBIO-NUEVO
        CambioDeEstado cambioRechazado = this.crearCambioDeEstado(estadoRechazado, usuario, fechaHoraActual);
        // actualizar cambio de estado actual
        evento.getCambioDeEstados().add(cambioRechazado);
        // actualizar estado actual
        evento.setEstadoActual(estadoRechazado);
        // actualizar la BDD
        DataBaseService.actualizarEventoSismico(evento);

    }

    public void derivar(){
        System.out.println("Metodo no implementado");
    }

    public void controlarTiempo(){
        System.out.println("Metodo no implementado");
    }

    public void revisar(Usuario usuario, LocalDateTime fechaHoraActual, EventoSismico evento, List<CambioDeEstado> cambiosDeEstado){
        // buscar CAMBIO-ACTUAL
        CambioDeEstado cambioDeEstadoActual = this.buscarCambioDeEstadoActual(cambiosDeEstado);
        // fechahorafin al CAMBIO-ACTUAL
        cambioDeEstadoActual.setFechaHoraFin(fechaHoraActual);
        // Crear estado
        Estado estadoBloqueadoEnRevision = new BloqueadoEnRevision(Enums.nombresAmbito.Evento_Sismico.name(), Enums.nombresEstado.Bloqueado_En_Revision.name());
        // crear CAMBIO-NUEVO
        CambioDeEstado cambioBloqueadoEnRevision = this.crearCambioDeEstado(estadoBloqueadoEnRevision, usuario, fechaHoraActual);
        // actualizar cambio de estado actual
        evento.getCambioDeEstados().add(cambioBloqueadoEnRevision);
        // actualizar estado actual
        evento.setEstadoActual(estadoBloqueadoEnRevision);
        // actualizar la BDD
        DataBaseService.actualizarEventoSismico(evento);
    }

    public void anular(){
        System.out.println("Metodo no implementado");
    }

    // Verificaciones

    public boolean esPendienteDeRevision(){
        return (this.nombre.equals(Enums.nombresEstado.Pendiente_De_Revision.name()));
    }

    public boolean esAutoDetectado(){
        return (this.nombre.equals(Enums.nombresEstado.Auto_Detectado.name()));
    }

    public boolean esBloqueadoARevisar(){
        return (this.nombre.equals(Enums.nombresEstado.Bloqueado_En_Revision.name()));
    }

    public boolean esRechazado(){
        return (this.nombre.equals(Enums.nombresEstado.Rechazado.name()));
    }

    // Ambito

    public boolean esAmbitoEventoSismico(){
        return (this.ambito.equals(Enums.nombresAmbito.Evento_Sismico.name()));
    }

    public CambioDeEstado buscarCambioDeEstadoActual(List<CambioDeEstado> cambiosDeEstado){
        for (CambioDeEstado cambio : cambiosDeEstado) {
            if (cambio.esEstadoActual()) {
                return cambio;
            }
        }
        return null;
    }

    public CambioDeEstado crearCambioDeEstado(Estado nuevoEstado, Usuario usuario, LocalDateTime fechaHoraActual){
        CambioDeEstado nuevo = new CambioDeEstado();

        nuevo.setFechaHoraInicio(fechaHoraActual);
        nuevo.setFechaHoraFin(null);
        nuevo.setEstado(nuevoEstado);
        nuevo.setEmpleado(usuario.getEmpleado());

        return nuevo;
    }

}
