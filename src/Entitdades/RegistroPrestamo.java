/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entitdades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ronald
 */
@Entity
@Table(name = "registro_prestamo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegistroPrestamo.findAll", query = "SELECT r FROM RegistroPrestamo r")
    , @NamedQuery(name = "RegistroPrestamo.findByCodigo", query = "SELECT r FROM RegistroPrestamo r WHERE r.codigo = :codigo")
    , @NamedQuery(name = "RegistroPrestamo.findByTarjetacredito", query = "SELECT r FROM RegistroPrestamo r WHERE r.tarjetacredito = :tarjetacredito")
    , @NamedQuery(name = "RegistroPrestamo.findByCvv", query = "SELECT r FROM RegistroPrestamo r WHERE r.cvv = :cvv")
    , @NamedQuery(name = "RegistroPrestamo.findByFechaprestamo", query = "SELECT r FROM RegistroPrestamo r WHERE r.fechaprestamo = :fechaprestamo")
    , @NamedQuery(name = "RegistroPrestamo.findByFechaentrega", query = "SELECT r FROM RegistroPrestamo r WHERE r.fechaentrega = :fechaentrega")})
public class RegistroPrestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "Tarjeta_credito")
    private int tarjetacredito;
    @Basic(optional = false)
    @Column(name = "Cvv")
    private int cvv;
    @Basic(optional = false)
    @Column(name = "Fecha_prestamo")
    @Temporal(TemporalType.DATE)
    private Date fechaprestamo;
    @Basic(optional = false)
    @Column(name = "Fecha_entrega")
    @Temporal(TemporalType.DATE)
    private Date fechaentrega;
    @JoinColumn(name = "Libros_Codigo", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Libros librosCodigo;
    @JoinColumn(name = "Mes_Codigo", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Mes mesCodigo;
    @JoinColumn(name = "Registro_Cedula", referencedColumnName = "Cedula")
    @ManyToOne(optional = false)
    private Registro registroCedula;
    @JoinColumn(name = "Tipo_pago_Codigo", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private TipoPago tipopagoCodigo;
    @JoinColumn(name = "Vigencia_Codigo", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Vigencia vigenciaCodigo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "registroprestamoCodigo")
    private List<RegistroRegreso> registroRegresoList;

    public RegistroPrestamo() {
    }

    public RegistroPrestamo(Integer codigo) {
        this.codigo = codigo;
    }

    public RegistroPrestamo(Integer codigo, int tarjetacredito, int cvv, Date fechaprestamo, Date fechaentrega) {
        this.codigo = codigo;
        this.tarjetacredito = tarjetacredito;
        this.cvv = cvv;
        this.fechaprestamo = fechaprestamo;
        this.fechaentrega = fechaentrega;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public int getTarjetacredito() {
        return tarjetacredito;
    }

    public void setTarjetacredito(int tarjetacredito) {
        this.tarjetacredito = tarjetacredito;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public Date getFechaprestamo() {
        return fechaprestamo;
    }

    public void setFechaprestamo(Date fechaprestamo) {
        this.fechaprestamo = fechaprestamo;
    }

    public Date getFechaentrega() {
        return fechaentrega;
    }

    public void setFechaentrega(Date fechaentrega) {
        this.fechaentrega = fechaentrega;
    }

    public Libros getLibrosCodigo() {
        return librosCodigo;
    }

    public void setLibrosCodigo(Libros librosCodigo) {
        this.librosCodigo = librosCodigo;
    }

    public Mes getMesCodigo() {
        return mesCodigo;
    }

    public void setMesCodigo(Mes mesCodigo) {
        this.mesCodigo = mesCodigo;
    }

    public Registro getRegistroCedula() {
        return registroCedula;
    }

    public void setRegistroCedula(Registro registroCedula) {
        this.registroCedula = registroCedula;
    }

    public TipoPago getTipopagoCodigo() {
        return tipopagoCodigo;
    }

    public void setTipopagoCodigo(TipoPago tipopagoCodigo) {
        this.tipopagoCodigo = tipopagoCodigo;
    }

    public Vigencia getVigenciaCodigo() {
        return vigenciaCodigo;
    }

    public void setVigenciaCodigo(Vigencia vigenciaCodigo) {
        this.vigenciaCodigo = vigenciaCodigo;
    }

    @XmlTransient
    public List<RegistroRegreso> getRegistroRegresoList() {
        return registroRegresoList;
    }

    public void setRegistroRegresoList(List<RegistroRegreso> registroRegresoList) {
        this.registroRegresoList = registroRegresoList;
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
        if (!(object instanceof RegistroPrestamo)) {
            return false;
        }
        RegistroPrestamo other = (RegistroPrestamo) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entitdades.RegistroPrestamo[ codigo=" + codigo + " ]";
    }
    
}
