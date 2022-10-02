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
@Table(name = "vigencia")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vigencia.findAll", query = "SELECT v FROM Vigencia v")
    , @NamedQuery(name = "Vigencia.findByCodigo", query = "SELECT v FROM Vigencia v WHERE v.codigo = :codigo")
    , @NamedQuery(name = "Vigencia.findByNombre", query = "SELECT v FROM Vigencia v WHERE v.nombre = :nombre")})
public class Vigencia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "Nombre")
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vigenciaCodigo")
    private List<RegistroPrestamo> registroPrestamoList;

    public Vigencia() {
    }

    public Vigencia(Integer codigo) {
        this.codigo = codigo;
    }

    public Vigencia(Integer codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public List<RegistroPrestamo> getRegistroPrestamoList() {
        return registroPrestamoList;
    }

    public void setRegistroPrestamoList(List<RegistroPrestamo> registroPrestamoList) {
        this.registroPrestamoList = registroPrestamoList;
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
        if (!(object instanceof Vigencia)) {
            return false;
        }
        Vigencia other = (Vigencia) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entitdades.Vigencia[ codigo=" + codigo + " ]";
    }
    
}
