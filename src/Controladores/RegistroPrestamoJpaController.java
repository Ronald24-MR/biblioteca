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
import Entitdades.Libros;
import Entitdades.Mes;
import Entitdades.Registro;
import Entitdades.RegistroPrestamo;
import Entitdades.TipoPago;
import Entitdades.Vigencia;
import Entitdades.RegistroRegreso;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class RegistroPrestamoJpaController implements Serializable {

    public RegistroPrestamoJpaController() {
        this.emf = Persistence.createEntityManagerFactory("bibliotecPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RegistroPrestamo registroPrestamo) throws PreexistingEntityException, Exception {
        if (registroPrestamo.getRegistroRegresoList() == null) {
            registroPrestamo.setRegistroRegresoList(new ArrayList<RegistroRegreso>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Libros librosCodigo = registroPrestamo.getLibrosCodigo();
            if (librosCodigo != null) {
                librosCodigo = em.getReference(librosCodigo.getClass(), librosCodigo.getCodigo());
                registroPrestamo.setLibrosCodigo(librosCodigo);
            }
            Mes mesCodigo = registroPrestamo.getMesCodigo();
            if (mesCodigo != null) {
                mesCodigo = em.getReference(mesCodigo.getClass(), mesCodigo.getCodigo());
                registroPrestamo.setMesCodigo(mesCodigo);
            }
            Registro registroCedula = registroPrestamo.getRegistroCedula();
            if (registroCedula != null) {
                registroCedula = em.getReference(registroCedula.getClass(), registroCedula.getCedula());
                registroPrestamo.setRegistroCedula(registroCedula);
            }
            TipoPago tipopagoCodigo = registroPrestamo.getTipopagoCodigo();
            if (tipopagoCodigo != null) {
                tipopagoCodigo = em.getReference(tipopagoCodigo.getClass(), tipopagoCodigo.getCodigo());
                registroPrestamo.setTipopagoCodigo(tipopagoCodigo);
            }
            Vigencia vigenciaCodigo = registroPrestamo.getVigenciaCodigo();
            if (vigenciaCodigo != null) {
                vigenciaCodigo = em.getReference(vigenciaCodigo.getClass(), vigenciaCodigo.getCodigo());
                registroPrestamo.setVigenciaCodigo(vigenciaCodigo);
            }
            List<RegistroRegreso> attachedRegistroRegresoList = new ArrayList<RegistroRegreso>();
            for (RegistroRegreso registroRegresoListRegistroRegresoToAttach : registroPrestamo.getRegistroRegresoList()) {
                registroRegresoListRegistroRegresoToAttach = em.getReference(registroRegresoListRegistroRegresoToAttach.getClass(), registroRegresoListRegistroRegresoToAttach.getCodigo());
                attachedRegistroRegresoList.add(registroRegresoListRegistroRegresoToAttach);
            }
            registroPrestamo.setRegistroRegresoList(attachedRegistroRegresoList);
            em.persist(registroPrestamo);
            if (librosCodigo != null) {
                librosCodigo.getRegistroPrestamoList().add(registroPrestamo);
                librosCodigo = em.merge(librosCodigo);
            }
            if (mesCodigo != null) {
                mesCodigo.getRegistroPrestamoList().add(registroPrestamo);
                mesCodigo = em.merge(mesCodigo);
            }
            if (registroCedula != null) {
                registroCedula.getRegistroPrestamoList().add(registroPrestamo);
                registroCedula = em.merge(registroCedula);
            }
            if (tipopagoCodigo != null) {
                tipopagoCodigo.getRegistroPrestamoList().add(registroPrestamo);
                tipopagoCodigo = em.merge(tipopagoCodigo);
            }
            if (vigenciaCodigo != null) {
                vigenciaCodigo.getRegistroPrestamoList().add(registroPrestamo);
                vigenciaCodigo = em.merge(vigenciaCodigo);
            }
            for (RegistroRegreso registroRegresoListRegistroRegreso : registroPrestamo.getRegistroRegresoList()) {
                RegistroPrestamo oldRegistroprestamoCodigoOfRegistroRegresoListRegistroRegreso = registroRegresoListRegistroRegreso.getRegistroprestamoCodigo();
                registroRegresoListRegistroRegreso.setRegistroprestamoCodigo(registroPrestamo);
                registroRegresoListRegistroRegreso = em.merge(registroRegresoListRegistroRegreso);
                if (oldRegistroprestamoCodigoOfRegistroRegresoListRegistroRegreso != null) {
                    oldRegistroprestamoCodigoOfRegistroRegresoListRegistroRegreso.getRegistroRegresoList().remove(registroRegresoListRegistroRegreso);
                    oldRegistroprestamoCodigoOfRegistroRegresoListRegistroRegreso = em.merge(oldRegistroprestamoCodigoOfRegistroRegresoListRegistroRegreso);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRegistroPrestamo(registroPrestamo.getCodigo()) != null) {
                throw new PreexistingEntityException("RegistroPrestamo " + registroPrestamo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RegistroPrestamo registroPrestamo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RegistroPrestamo persistentRegistroPrestamo = em.find(RegistroPrestamo.class, registroPrestamo.getCodigo());
            Libros librosCodigoOld = persistentRegistroPrestamo.getLibrosCodigo();
            Libros librosCodigoNew = registroPrestamo.getLibrosCodigo();
            Mes mesCodigoOld = persistentRegistroPrestamo.getMesCodigo();
            Mes mesCodigoNew = registroPrestamo.getMesCodigo();
            Registro registroCedulaOld = persistentRegistroPrestamo.getRegistroCedula();
            Registro registroCedulaNew = registroPrestamo.getRegistroCedula();
            TipoPago tipopagoCodigoOld = persistentRegistroPrestamo.getTipopagoCodigo();
            TipoPago tipopagoCodigoNew = registroPrestamo.getTipopagoCodigo();
            Vigencia vigenciaCodigoOld = persistentRegistroPrestamo.getVigenciaCodigo();
            Vigencia vigenciaCodigoNew = registroPrestamo.getVigenciaCodigo();
            List<RegistroRegreso> registroRegresoListOld = persistentRegistroPrestamo.getRegistroRegresoList();
            List<RegistroRegreso> registroRegresoListNew = registroPrestamo.getRegistroRegresoList();
            List<String> illegalOrphanMessages = null;
            for (RegistroRegreso registroRegresoListOldRegistroRegreso : registroRegresoListOld) {
                if (!registroRegresoListNew.contains(registroRegresoListOldRegistroRegreso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RegistroRegreso " + registroRegresoListOldRegistroRegreso + " since its registroprestamoCodigo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (librosCodigoNew != null) {
                librosCodigoNew = em.getReference(librosCodigoNew.getClass(), librosCodigoNew.getCodigo());
                registroPrestamo.setLibrosCodigo(librosCodigoNew);
            }
            if (mesCodigoNew != null) {
                mesCodigoNew = em.getReference(mesCodigoNew.getClass(), mesCodigoNew.getCodigo());
                registroPrestamo.setMesCodigo(mesCodigoNew);
            }
            if (registroCedulaNew != null) {
                registroCedulaNew = em.getReference(registroCedulaNew.getClass(), registroCedulaNew.getCedula());
                registroPrestamo.setRegistroCedula(registroCedulaNew);
            }
            if (tipopagoCodigoNew != null) {
                tipopagoCodigoNew = em.getReference(tipopagoCodigoNew.getClass(), tipopagoCodigoNew.getCodigo());
                registroPrestamo.setTipopagoCodigo(tipopagoCodigoNew);
            }
            if (vigenciaCodigoNew != null) {
                vigenciaCodigoNew = em.getReference(vigenciaCodigoNew.getClass(), vigenciaCodigoNew.getCodigo());
                registroPrestamo.setVigenciaCodigo(vigenciaCodigoNew);
            }
            List<RegistroRegreso> attachedRegistroRegresoListNew = new ArrayList<RegistroRegreso>();
            for (RegistroRegreso registroRegresoListNewRegistroRegresoToAttach : registroRegresoListNew) {
                registroRegresoListNewRegistroRegresoToAttach = em.getReference(registroRegresoListNewRegistroRegresoToAttach.getClass(), registroRegresoListNewRegistroRegresoToAttach.getCodigo());
                attachedRegistroRegresoListNew.add(registroRegresoListNewRegistroRegresoToAttach);
            }
            registroRegresoListNew = attachedRegistroRegresoListNew;
            registroPrestamo.setRegistroRegresoList(registroRegresoListNew);
            registroPrestamo = em.merge(registroPrestamo);
            if (librosCodigoOld != null && !librosCodigoOld.equals(librosCodigoNew)) {
                librosCodigoOld.getRegistroPrestamoList().remove(registroPrestamo);
                librosCodigoOld = em.merge(librosCodigoOld);
            }
            if (librosCodigoNew != null && !librosCodigoNew.equals(librosCodigoOld)) {
                librosCodigoNew.getRegistroPrestamoList().add(registroPrestamo);
                librosCodigoNew = em.merge(librosCodigoNew);
            }
            if (mesCodigoOld != null && !mesCodigoOld.equals(mesCodigoNew)) {
                mesCodigoOld.getRegistroPrestamoList().remove(registroPrestamo);
                mesCodigoOld = em.merge(mesCodigoOld);
            }
            if (mesCodigoNew != null && !mesCodigoNew.equals(mesCodigoOld)) {
                mesCodigoNew.getRegistroPrestamoList().add(registroPrestamo);
                mesCodigoNew = em.merge(mesCodigoNew);
            }
            if (registroCedulaOld != null && !registroCedulaOld.equals(registroCedulaNew)) {
                registroCedulaOld.getRegistroPrestamoList().remove(registroPrestamo);
                registroCedulaOld = em.merge(registroCedulaOld);
            }
            if (registroCedulaNew != null && !registroCedulaNew.equals(registroCedulaOld)) {
                registroCedulaNew.getRegistroPrestamoList().add(registroPrestamo);
                registroCedulaNew = em.merge(registroCedulaNew);
            }
            if (tipopagoCodigoOld != null && !tipopagoCodigoOld.equals(tipopagoCodigoNew)) {
                tipopagoCodigoOld.getRegistroPrestamoList().remove(registroPrestamo);
                tipopagoCodigoOld = em.merge(tipopagoCodigoOld);
            }
            if (tipopagoCodigoNew != null && !tipopagoCodigoNew.equals(tipopagoCodigoOld)) {
                tipopagoCodigoNew.getRegistroPrestamoList().add(registroPrestamo);
                tipopagoCodigoNew = em.merge(tipopagoCodigoNew);
            }
            if (vigenciaCodigoOld != null && !vigenciaCodigoOld.equals(vigenciaCodigoNew)) {
                vigenciaCodigoOld.getRegistroPrestamoList().remove(registroPrestamo);
                vigenciaCodigoOld = em.merge(vigenciaCodigoOld);
            }
            if (vigenciaCodigoNew != null && !vigenciaCodigoNew.equals(vigenciaCodigoOld)) {
                vigenciaCodigoNew.getRegistroPrestamoList().add(registroPrestamo);
                vigenciaCodigoNew = em.merge(vigenciaCodigoNew);
            }
            for (RegistroRegreso registroRegresoListNewRegistroRegreso : registroRegresoListNew) {
                if (!registroRegresoListOld.contains(registroRegresoListNewRegistroRegreso)) {
                    RegistroPrestamo oldRegistroprestamoCodigoOfRegistroRegresoListNewRegistroRegreso = registroRegresoListNewRegistroRegreso.getRegistroprestamoCodigo();
                    registroRegresoListNewRegistroRegreso.setRegistroprestamoCodigo(registroPrestamo);
                    registroRegresoListNewRegistroRegreso = em.merge(registroRegresoListNewRegistroRegreso);
                    if (oldRegistroprestamoCodigoOfRegistroRegresoListNewRegistroRegreso != null && !oldRegistroprestamoCodigoOfRegistroRegresoListNewRegistroRegreso.equals(registroPrestamo)) {
                        oldRegistroprestamoCodigoOfRegistroRegresoListNewRegistroRegreso.getRegistroRegresoList().remove(registroRegresoListNewRegistroRegreso);
                        oldRegistroprestamoCodigoOfRegistroRegresoListNewRegistroRegreso = em.merge(oldRegistroprestamoCodigoOfRegistroRegresoListNewRegistroRegreso);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = registroPrestamo.getCodigo();
                if (findRegistroPrestamo(id) == null) {
                    throw new NonexistentEntityException("The registroPrestamo with id " + id + " no longer exists.");
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
            RegistroPrestamo registroPrestamo;
            try {
                registroPrestamo = em.getReference(RegistroPrestamo.class, id);
                registroPrestamo.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The registroPrestamo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RegistroRegreso> registroRegresoListOrphanCheck = registroPrestamo.getRegistroRegresoList();
            for (RegistroRegreso registroRegresoListOrphanCheckRegistroRegreso : registroRegresoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RegistroPrestamo (" + registroPrestamo + ") cannot be destroyed since the RegistroRegreso " + registroRegresoListOrphanCheckRegistroRegreso + " in its registroRegresoList field has a non-nullable registroprestamoCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Libros librosCodigo = registroPrestamo.getLibrosCodigo();
            if (librosCodigo != null) {
                librosCodigo.getRegistroPrestamoList().remove(registroPrestamo);
                librosCodigo = em.merge(librosCodigo);
            }
            Mes mesCodigo = registroPrestamo.getMesCodigo();
            if (mesCodigo != null) {
                mesCodigo.getRegistroPrestamoList().remove(registroPrestamo);
                mesCodigo = em.merge(mesCodigo);
            }
            Registro registroCedula = registroPrestamo.getRegistroCedula();
            if (registroCedula != null) {
                registroCedula.getRegistroPrestamoList().remove(registroPrestamo);
                registroCedula = em.merge(registroCedula);
            }
            TipoPago tipopagoCodigo = registroPrestamo.getTipopagoCodigo();
            if (tipopagoCodigo != null) {
                tipopagoCodigo.getRegistroPrestamoList().remove(registroPrestamo);
                tipopagoCodigo = em.merge(tipopagoCodigo);
            }
            Vigencia vigenciaCodigo = registroPrestamo.getVigenciaCodigo();
            if (vigenciaCodigo != null) {
                vigenciaCodigo.getRegistroPrestamoList().remove(registroPrestamo);
                vigenciaCodigo = em.merge(vigenciaCodigo);
            }
            em.remove(registroPrestamo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RegistroPrestamo> findRegistroPrestamoEntities() {
        return findRegistroPrestamoEntities(true, -1, -1);
    }

    public List<RegistroPrestamo> findRegistroPrestamoEntities(int maxResults, int firstResult) {
        return findRegistroPrestamoEntities(false, maxResults, firstResult);
    }

    private List<RegistroPrestamo> findRegistroPrestamoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RegistroPrestamo.class));
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

    public RegistroPrestamo findRegistroPrestamo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RegistroPrestamo.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegistroPrestamoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RegistroPrestamo> rt = cq.from(RegistroPrestamo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
