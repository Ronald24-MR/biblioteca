/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entitdades;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ronald
 */
@Entity
@Table(name = "registro")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Registro.findAll", query = "SELECT r FROM Registro r")
    , @NamedQuery(name = "Registro.findByCedula", query = "SELECT r FROM Registro r WHERE r.cedula = :cedula")
    , @NamedQuery(name = "Registro.findByNombre", query = "SELECT r FROM Registro r WHERE r.nombre = :nombre")
    , @NamedQuery(name = "Registro.findByDireccion", query = "SELECT r FROM Registro r WHERE r.direccion = :direccion")
    , @NamedQuery(name = "Registro.findByTelefono", query = "SELECT r FROM Registro r WHERE r.telefono = :telefono")
    , @NamedQuery(name = "Registro.findByCorreo", query = "SELECT r FROM Registro r WHERE r.correo = :correo")
    , @NamedQuery(name = "Registro.findByCelular", query = "SELECT r FROM Registro r WHERE r.celular = :celular")})
public class Registro implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Cedula")
    private Integer cedula;
    @Basic(optional = false)
    @Column(name = "Nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "Direccion")
    private String direccion;
    @Basic(optional = false)
    @Column(name = "Telefono")
    private int telefono;
    @Basic(optional = false)
    @Column(name = "Correo")
    private String correo;
    @Basic(optional = false)
    @Column(name = "Celular")
    private int celular;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "registroCedula")
    private List<RegistroPrestamo> registroPrestamoList;
    @JoinColumn(name = "Ciudad_Codigo", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Ciudad ciudadCodigo;
    @JoinColumn(name = "Departamento_Codigo", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Departamento departamentoCodigo;
    @JoinColumn(name = "Municipio_Codigo", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Municipio municipioCodigo;

    public Registro() {
    }

    public Registro(Integer cedula) {
        this.cedula = cedula;
    }

    public Registro(Integer cedula, String nombre, String direccion, int telefono, String correo, int celular) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.celular = celular;
    }

    public Integer getCedula() {
        return cedula;
    }

    public void setCedula(Integer cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getCelular() {
        return celular;
    }

    public void setCelular(int celular) {
        this.celular = celular;
    }

    @XmlTransient
    public List<RegistroPrestamo> getRegistroPrestamoList() {
        return registroPrestamoList;
    }

    public void setRegistroPrestamoList(List<RegistroPrestamo> registroPrestamoList) {
        this.registroPrestamoList = registroPrestamoList;
    }

    public Ciudad getCiudadCodigo() {
        return ciudadCodigo;
    }

    public void setCiudadCodigo(Ciudad ciudadCodigo) {
        this.ciudadCodigo = ciudadCodigo;
    }

    public Departamento getDepartamentoCodigo() {
        return departamentoCodigo;
    }

    public void setDepartamentoCodigo(Departamento departamentoCodigo) {
        this.departamentoCodigo = departamentoCodigo;
    }

    public Municipio getMunicipioCodigo() {
        return municipioCodigo;
    }

    public void setMunicipioCodigo(Municipio municipioCodigo) {
        this.municipioCodigo = municipioCodigo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cedula != null ? cedula.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Registro)) {
            return false;
        }
        Registro other = (Registro) object;
        if ((this.cedula == null && other.cedula != null) || (this.cedula != null && !this.cedula.equals(other.cedula))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entitdades.Registro[ cedula=" + cedula + " ]";
    }
    
}
