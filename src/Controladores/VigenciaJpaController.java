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
import Entitdades.RegistroPrestamo;
import Entitdades.Vigencia;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class VigenciaJpaController implements Serializable {

    public VigenciaJpaController() {
        this.emf = Persistence.createEntityManagerFactory("bibliotecPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Vigencia vigencia) throws PreexistingEntityException, Exception {
        if (vigencia.getRegistroPrestamoList() == null) {
            vigencia.setRegistroPrestamoList(new ArrayList<RegistroPrestamo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RegistroPrestamo> attachedRegistroPrestamoList = new ArrayList<RegistroPrestamo>();
            for (RegistroPrestamo registroPrestamoListRegistroPrestamoToAttach : vigencia.getRegistroPrestamoList()) {
                registroPrestamoListRegistroPrestamoToAttach = em.getReference(registroPrestamoListRegistroPrestamoToAttach.getClass(), registroPrestamoListRegistroPrestamoToAttach.getCodigo());
                attachedRegistroPrestamoList.add(registroPrestamoListRegistroPrestamoToAttach);
            }
            vigencia.setRegistroPrestamoList(attachedRegistroPrestamoList);
            em.persist(vigencia);
            for (RegistroPrestamo registroPrestamoListRegistroPrestamo : vigencia.getRegistroPrestamoList()) {
                Vigencia oldVigenciaCodigoOfRegistroPrestamoListRegistroPrestamo = registroPrestamoListRegistroPrestamo.getVigenciaCodigo();
                registroPrestamoListRegistroPrestamo.setVigenciaCodigo(vigencia);
                registroPrestamoListRegistroPrestamo = em.merge(registroPrestamoListRegistroPrestamo);
                if (oldVigenciaCodigoOfRegistroPrestamoListRegistroPrestamo != null) {
                    oldVigenciaCodigoOfRegistroPrestamoListRegistroPrestamo.getRegistroPrestamoList().remove(registroPrestamoListRegistroPrestamo);
                    oldVigenciaCodigoOfRegistroPrestamoListRegistroPrestamo = em.merge(oldVigenciaCodigoOfRegistroPrestamoListRegistroPrestamo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVigencia(vigencia.getCodigo()) != null) {
                throw new PreexistingEntityException("Vigencia " + vigencia + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Vigencia vigencia) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Vigencia persistentVigencia = em.find(Vigencia.class, vigencia.getCodigo());
            List<RegistroPrestamo> registroPrestamoListOld = persistentVigencia.getRegistroPrestamoList();
            List<RegistroPrestamo> registroPrestamoListNew = vigencia.getRegistroPrestamoList();
            List<String> illegalOrphanMessages = null;
            for (RegistroPrestamo registroPrestamoListOldRegistroPrestamo : registroPrestamoListOld) {
                if (!registroPrestamoListNew.contains(registroPrestamoListOldRegistroPrestamo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RegistroPrestamo " + registroPrestamoListOldRegistroPrestamo + " since its vigenciaCodigo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<RegistroPrestamo> attachedRegistroPrestamoListNew = new ArrayList<RegistroPrestamo>();
            for (RegistroPrestamo registroPrestamoListNewRegistroPrestamoToAttach : registroPrestamoListNew) {
                registroPrestamoListNewRegistroPrestamoToAttach = em.getReference(registroPrestamoListNewRegistroPrestamoToAttach.getClass(), registroPrestamoListNewRegistroPrestamoToAttach.getCodigo());
                attachedRegistroPrestamoListNew.add(registroPrestamoListNewRegistroPrestamoToAttach);
            }
            registroPrestamoListNew = attachedRegistroPrestamoListNew;
            vigencia.setRegistroPrestamoList(registroPrestamoListNew);
            vigencia = em.merge(vigencia);
            for (RegistroPrestamo registroPrestamoListNewRegistroPrestamo : registroPrestamoListNew) {
                if (!registroPrestamoListOld.contains(registroPrestamoListNewRegistroPrestamo)) {
                    Vigencia oldVigenciaCodigoOfRegistroPrestamoListNewRegistroPrestamo = registroPrestamoListNewRegistroPrestamo.getVigenciaCodigo();
                    registroPrestamoListNewRegistroPrestamo.setVigenciaCodigo(vigencia);
                    registroPrestamoListNewRegistroPrestamo = em.merge(registroPrestamoListNewRegistroPrestamo);
                    if (oldVigenciaCodigoOfRegistroPrestamoListNewRegistroPrestamo != null && !oldVigenciaCodigoOfRegistroPrestamoListNewRegistroPrestamo.equals(vigencia)) {
                        oldVigenciaCodigoOfRegistroPrestamoListNewRegistroPrestamo.getRegistroPrestamoList().remove(registroPrestamoListNewRegistroPrestamo);
                        oldVigenciaCodigoOfRegistroPrestamoListNewRegistroPrestamo = em.merge(oldVigenciaCodigoOfRegistroPrestamoListNewRegistroPrestamo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = vigencia.getCodigo();
                if (findVigencia(id) == null) {
                    throw new NonexistentEntityException("The vigencia with id " + id + " no longer exists.");
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
            Vigencia vigencia;
            try {
                vigencia = em.getReference(Vigencia.class, id);
                vigencia.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vigencia with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RegistroPrestamo> registroPrestamoListOrphanCheck = vigencia.getRegistroPrestamoList();
            for (RegistroPrestamo registroPrestamoListOrphanCheckRegistroPrestamo : registroPrestamoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Vigencia (" + vigencia + ") cannot be destroyed since the RegistroPrestamo " + registroPrestamoListOrphanCheckRegistroPrestamo + " in its registroPrestamoList field has a non-nullable vigenciaCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(vigencia);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Vigencia> findVigenciaEntities() {
        return findVigenciaEntities(true, -1, -1);
    }

    public List<Vigencia> findVigenciaEntities(int maxResults, int firstResult) {
        return findVigenciaEntities(false, maxResults, firstResult);
    }

    private List<Vigencia> findVigenciaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vigencia.class));
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

    public Vigencia findVigencia(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vigencia.class, id);
        } finally {
            em.close();
        }
    }

    public int getVigenciaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vigencia> rt = cq.from(Vigencia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
