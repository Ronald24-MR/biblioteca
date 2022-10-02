/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entitdades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ronald
 */
@Entity
@Table(name = "registro_regreso")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegistroRegreso.findAll", query = "SELECT r FROM RegistroRegreso r")
    , @NamedQuery(name = "RegistroRegreso.findByCodigo", query = "SELECT r FROM RegistroRegreso r WHERE r.codigo = :codigo")
    , @NamedQuery(name = "RegistroRegreso.findByFecharegreso", query = "SELECT r FROM RegistroRegreso r WHERE r.fecharegreso = :fecharegreso")
    , @NamedQuery(name = "RegistroRegreso.findByObservaciones", query = "SELECT r FROM RegistroRegreso r WHERE r.observaciones = :observaciones")})
public class RegistroRegreso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "Fecha_regreso")
    @Temporal(TemporalType.DATE)
    private Date fecharegreso;
    @Basic(optional = false)
    @Column(name = "Observaciones")
    private String observaciones;
    @JoinColumn(name = "Registro_prestamo_Codigo", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private RegistroPrestamo registroprestamoCodigo;

    public RegistroRegreso() {
    }

    public RegistroRegreso(Integer codigo) {
        this.codigo = codigo;
    }

    public RegistroRegreso(Integer codigo, Date fecharegreso, String observaciones) {
        this.codigo = codigo;
        this.fecharegreso = fecharegreso;
        this.observaciones = observaciones;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Date getFecharegreso() {
        return fecharegreso;
    }

    public void setFecharegreso(Date fecharegreso) {
        this.fecharegreso = fecharegreso;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public RegistroPrestamo getRegistroprestamoCodigo() {
        return registroprestamoCodigo;
    }

    public void setRegistroprestamoCodigo(RegistroPrestamo registroprestamoCodigo) {
        this.registroprestamoCodigo = registroprestamoCodigo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RegistroRegreso)) {
            return false;
        }
        RegistroRegreso other = (RegistroRegreso) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entitdades.RegistroRegreso[ codigo=" + codigo + " ]";
    }
    
}
