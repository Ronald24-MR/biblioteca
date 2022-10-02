/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entitdades.RegistroPrestamo;
import Entitdades.RegistroRegreso;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class RegistroRegresoJpaController implements Serializable {

    public RegistroRegresoJpaController() {
        this.emf = Persistence.createEntityManagerFactory("bibliotecPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RegistroRegreso registroRegreso) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RegistroPrestamo registroprestamoCodigo = registroRegreso.getRegistroprestamoCodigo();
            if (registroprestamoCodigo != null) {
                registroprestamoCodigo = em.getReference(registroprestamoCodigo.getClass(), registroprestamoCodigo.getCodigo());
                registroRegreso.setRegistroprestamoCodigo(registroprestamoCodigo);
            }
            em.persist(registroRegreso);
            if (registroprestamoCodigo != null) {
                registroprestamoCodigo.getRegistroRegresoList().add(registroRegreso);
                registroprestamoCodigo = em.merge(registroprestamoCodigo);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRegistroRegreso(registroRegreso.getCodigo()) != null) {
                throw new PreexistingEntityException("RegistroRegreso " + registroRegreso + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RegistroRegreso registroRegreso) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RegistroRegreso persistentRegistroRegreso = em.find(RegistroRegreso.class, registroRegreso.getCodigo());
            RegistroPrestamo registroprestamoCodigoOld = persistentRegistroRegreso.getRegistroprestamoCodigo();
            RegistroPrestamo registroprestamoCodigoNew = registroRegreso.getRegistroprestamoCodigo();
            if (registroprestamoCodigoNew != null) {
                registroprestamoCodigoNew = em.getReference(registroprestamoCodigoNew.getClass(), registroprestamoCodigoNew.getCodigo());
                registroRegreso.setRegistroprestamoCodigo(registroprestamoCodigoNew);
            }
            registroRegreso = em.merge(registroRegreso);
            if (registroprestamoCodigoOld != null && !registroprestamoCodigoOld.equals(registroprestamoCodigoNew)) {
                registroprestamoCodigoOld.getRegistroRegresoList().remove(registroRegreso);
                registroprestamoCodigoOld = em.merge(registroprestamoCodigoOld);
            }
            if (registroprestamoCodigoNew != null && !registroprestamoCodigoNew.equals(registroprestamoCodigoOld)) {
                registroprestamoCodigoNew.getRegistroRegresoList().add(registroRegreso);
                registroprestamoCodigoNew = em.merge(registroprestamoCodigoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = registroRegreso.getCodigo();
                if (findRegistroRegreso(id) == null) {
                    throw new NonexistentEntityException("The registroRegreso with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RegistroRegreso registroRegreso;
            try {
                registroRegreso = em.getReference(RegistroRegreso.class, id);
                registroRegreso.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The registroRegreso with id " + id + " no longer exists.", enfe);
            }
            RegistroPrestamo registroprestamoCodigo = registroRegreso.getRegistroprestamoCodigo();
            if (registroprestamoCodigo != null) {
                registroprestamoCodigo.getRegistroRegresoList().remove(registroRegreso);
                registroprestamoCodigo = em.merge(registroprestamoCodigo);
            }
            em.remove(registroRegreso);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RegistroRegreso> findRegistroRegresoEntities() {
        return findRegistroRegresoEntities(true, -1, -1);
    }

    public List<RegistroRegreso> findRegistroRegresoEntities(int maxResults, int firstResult) {
        return findRegistroRegresoEntities(false, maxResults, firstResult);
    }

    private List<RegistroRegreso> findRegistroRegresoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RegistroRegreso.class));
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

    public RegistroRegreso findRegistroRegreso(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RegistroRegreso.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegistroRegresoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RegistroRegreso> rt = cq.from(RegistroRegreso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
