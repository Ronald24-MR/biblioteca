/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.IllegalOrphanException;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entitdades.Ciudad;
import Entitdades.Departamento;
import Entitdades.Municipio;
import Entitdades.Registro;
import Entitdades.RegistroPrestamo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class RegistroJpaController implements Serializable {

    public RegistroJpaController() {
        this.emf = Persistence.createEntityManagerFactory("bibliotecPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Registro registro) throws PreexistingEntityException, Exception {
        if (registro.getRegistroPrestamoList() == null) {
            registro.setRegistroPrestamoList(new ArrayList<RegistroPrestamo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ciudad ciudadCodigo = registro.getCiudadCodigo();
            if (ciudadCodigo != null) {
                ciudadCodigo = em.getReference(ciudadCodigo.getClass(), ciudadCodigo.getCodigo());
                registro.setCiudadCodigo(ciudadCodigo);
            }
            Departamento departamentoCodigo = registro.getDepartamentoCodigo();
            if (departamentoCodigo != null) {
                departamentoCodigo = em.getReference(departamentoCodigo.getClass(), departamentoCodigo.getCodigo());
                registro.setDepartamentoCodigo(departamentoCodigo);
            }
            Municipio municipioCodigo = registro.getMunicipioCodigo();
            if (municipioCodigo != null) {
                municipioCodigo = em.getReference(municipioCodigo.getClass(), municipioCodigo.getCodigo());
                registro.setMunicipioCodigo(municipioCodigo);
            }
            List<RegistroPrestamo> attachedRegistroPrestamoList = new ArrayList<RegistroPrestamo>();
            for (RegistroPrestamo registroPrestamoListRegistroPrestamoToAttach : registro.getRegistroPrestamoList()) {
                registroPrestamoListRegistroPrestamoToAttach = em.getReference(registroPrestamoListRegistroPrestamoToAttach.getClass(), registroPrestamoListRegistroPrestamoToAttach.getCodigo());
                attachedRegistroPrestamoList.add(registroPrestamoListRegistroPrestamoToAttach);
            }
            registro.setRegistroPrestamoList(attachedRegistroPrestamoList);
            em.persist(registro);
            if (ciudadCodigo != null) {
                ciudadCodigo.getRegistroList().add(registro);
                ciudadCodigo = em.merge(ciudadCodigo);
            }
            if (departamentoCodigo != null) {
                departamentoCodigo.getRegistroList().add(registro);
                departamentoCodigo = em.merge(departamentoCodigo);
            }
            if (municipioCodigo != null) {
                municipioCodigo.getRegistroList().add(registro);
                municipioCodigo = em.merge(municipioCodigo);
            }
            for (RegistroPrestamo registroPrestamoListRegistroPrestamo : registro.getRegistroPrestamoList()) {
                Registro oldRegistroCedulaOfRegistroPrestamoListRegistroPrestamo = registroPrestamoListRegistroPrestamo.getRegistroCedula();
                registroPrestamoListRegistroPrestamo.setRegistroCedula(registro);
                registroPrestamoListRegistroPrestamo = em.merge(registroPrestamoListRegistroPrestamo);
                if (oldRegistroCedulaOfRegistroPrestamoListRegistroPrestamo != null) {
                    oldRegistroCedulaOfRegistroPrestamoListRegistroPrestamo.getRegistroPrestamoList().remove(registroPrestamoListRegistroPrestamo);
                    oldRegistroCedulaOfRegistroPrestamoListRegistroPrestamo = em.merge(oldRegistroCedulaOfRegistroPrestamoListRegistroPrestamo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRegistro(registro.getCedula()) != null) {
                throw new PreexistingEntityException("Registro " + registro + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Registro registro) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Registro persistentRegistro = em.find(Registro.class, registro.getCedula());
            Ciudad ciudadCodigoOld = persistentRegistro.getCiudadCodigo();
            Ciudad ciudadCodigoNew = registro.getCiudadCodigo();
            Departamento departamentoCodigoOld = persistentRegistro.getDepartamentoCodigo();
            Departamento departamentoCodigoNew = registro.getDepartamentoCodigo();
            Municipio municipioCodigoOld = persistentRegistro.getMunicipioCodigo();
            Municipio municipioCodigoNew = registro.getMunicipioCodigo();
            List<RegistroPrestamo> registroPrestamoListOld = persistentRegistro.getRegistroPrestamoList();
            List<RegistroPrestamo> registroPrestamoListNew = registro.getRegistroPrestamoList();
            List<String> illegalOrphanMessages = null;
            for (RegistroPrestamo registroPrestamoListOldRegistroPrestamo : registroPrestamoListOld) {
                if (!registroPrestamoListNew.contains(registroPrestamoListOldRegistroPrestamo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RegistroPrestamo " + registroPrestamoListOldRegistroPrestamo + " since its registroCedula field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (ciudadCodigoNew != null) {
                ciudadCodigoNew = em.getReference(ciudadCodigoNew.getClass(), ciudadCodigoNew.getCodigo());
                registro.setCiudadCodigo(ciudadCodigoNew);
            }
            if (departamentoCodigoNew != null) {
                departamentoCodigoNew = em.getReference(departamentoCodigoNew.getClass(), departamentoCodigoNew.getCodigo());
                registro.setDepartamentoCodigo(departamentoCodigoNew);
            }
            if (municipioCodigoNew != null) {
                municipioCodigoNew = em.getReference(municipioCodigoNew.getClass(), municipioCodigoNew.getCodigo());
                registro.setMunicipioCodigo(municipioCodigoNew);
            }
            List<RegistroPrestamo> attachedRegistroPrestamoListNew = new ArrayList<RegistroPrestamo>();
            for (RegistroPrestamo registroPrestamoListNewRegistroPrestamoToAttach : registroPrestamoListNew) {
                registroPrestamoListNewRegistroPrestamoToAttach = em.getReference(registroPrestamoListNewRegistroPrestamoToAttach.getClass(), registroPrestamoListNewRegistroPrestamoToAttach.getCodigo());
                attachedRegistroPrestamoListNew.add(registroPrestamoListNewRegistroPrestamoToAttach);
            }
            registroPrestamoListNew = attachedRegistroPrestamoListNew;
            registro.setRegistroPrestamoList(registroPrestamoListNew);
            registro = em.merge(registro);
            if (ciudadCodigoOld != null && !ciudadCodigoOld.equals(ciudadCodigoNew)) {
                ciudadCodigoOld.getRegistroList().remove(registro);
                ciudadCodigoOld = em.merge(ciudadCodigoOld);
            }
            if (ciudadCodigoNew != null && !ciudadCodigoNew.equals(ciudadCodigoOld)) {
                ciudadCodigoNew.getRegistroList().add(registro);
                ciudadCodigoNew = em.merge(ciudadCodigoNew);
            }
            if (departamentoCodigoOld != null && !departamentoCodigoOld.equals(departamentoCodigoNew)) {
                departamentoCodigoOld.getRegistroList().remove(registro);
                departamentoCodigoOld = em.merge(departamentoCodigoOld);
            }
            if (departamentoCodigoNew != null && !departamentoCodigoNew.equals(departamentoCodigoOld)) {
                departamentoCodigoNew.getRegistroList().add(registro);
                departamentoCodigoNew = em.merge(departamentoCodigoNew);
            }
            if (municipioCodigoOld != null && !municipioCodigoOld.equals(municipioCodigoNew)) {
                municipioCodigoOld.getRegistroList().remove(registro);
                municipioCodigoOld = em.merge(municipioCodigoOld);
            }
            if (municipioCodigoNew != null && !municipioCodigoNew.equals(municipioCodigoOld)) {
                municipioCodigoNew.getRegistroList().add(registro);
                municipioCodigoNew = em.merge(municipioCodigoNew);
            }
            for (RegistroPrestamo registroPrestamoListNewRegistroPrestamo : registroPrestamoListNew) {
                if (!registroPrestamoListOld.contains(registroPrestamoListNewRegistroPrestamo)) {
                    Registro oldRegistroCedulaOfRegistroPrestamoListNewRegistroPrestamo = registroPrestamoListNewRegistroPrestamo.getRegistroCedula();
                    registroPrestamoListNewRegistroPrestamo.setRegistroCedula(registro);
                    registroPrestamoListNewRegistroPrestamo = em.merge(registroPrestamoListNewRegistroPrestamo);
                    if (oldRegistroCedulaOfRegistroPrestamoListNewRegistroPrestamo != null && !oldRegistroCedulaOfRegistroPrestamoListNewRegistroPrestamo.equals(registro)) {
                        oldRegistroCedulaOfRegistroPrestamoListNewRegistroPrestamo.getRegistroPrestamoList().remove(registroPrestamoListNewRegistroPrestamo);
                        oldRegistroCedulaOfRegistroPrestamoListNewRegistroPrestamo = em.merge(oldRegistroCedulaOfRegistroPrestamoListNewRegistroPrestamo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = registro.getCedula();
                if (findRegistro(id) == null) {
                    throw new NonexistentEntityException("The registro with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Registro registro;
            try {
                registro = em.getReference(Registro.class, id);
                registro.getCedula();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The registro with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RegistroPrestamo> registroPrestamoListOrphanCheck = registro.getRegistroPrestamoList();
            for (RegistroPrestamo registroPrestamoListOrphanCheckRegistroPrestamo : registroPrestamoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Registro (" + registro + ") cannot be destroyed since the RegistroPrestamo " + registroPrestamoListOrphanCheckRegistroPrestamo + " in its registroPrestamoList field has a non-nullable registroCedula field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Ciudad ciudadCodigo = registro.getCiudadCodigo();
            if (ciudadCodigo != null) {
                ciudadCodigo.getRegistroList().remove(registro);
                ciudadCodigo = em.merge(ciudadCodigo);
            }
            Departamento departamentoCodigo = registro.getDepartamentoCodigo();
            if (departamentoCodigo != null) {
                departamentoCodigo.getRegistroList().remove(registro);
                departamentoCodigo = em.merge(departamentoCodigo);
            }
            Municipio municipioCodigo = registro.getMunicipioCodigo();
            if (municipioCodigo != null) {
                municipioCodigo.getRegistroList().remove(registro);
                municipioCodigo = em.merge(municipioCodigo);
            }
            em.remove(registro);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Registro> findRegistroEntities() {
        return findRegistroEntities(true, -1, -1);
    }

    public List<Registro> findRegistroEntities(int maxResults, int firstResult) {
        return findRegistroEntities(false, maxResults, firstResult);
    }

    private List<Registro> findRegistroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Registro.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Registro findRegistro(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Registro.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegistroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Registro> rt = cq.from(Registro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
