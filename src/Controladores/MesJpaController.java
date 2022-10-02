/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.IllegalOrphanException;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import Entitdades.Mes;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class MesJpaController implements Serializable {

    public MesJpaController() {
        this.emf = Persistence.createEntityManagerFactory("bibliotecPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mes mes) throws PreexistingEntityException, Exception {
        if (mes.getRegistroPrestamoList() == null) {
            mes.setRegistroPrestamoList(new ArrayList<RegistroPrestamo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RegistroPrestamo> attachedRegistroPrestamoList = new ArrayList<RegistroPrestamo>();
            for (RegistroPrestamo registroPrestamoListRegistroPrestamoToAttach : mes.getRegistroPrestamoList()) {
                registroPrestamoListRegistroPrestamoToAttach = em.getReference(registroPrestamoListRegistroPrestamoToAttach.getClass(), registroPrestamoListRegistroPrestamoToAttach.getCodigo());
                attachedRegistroPrestamoList.add(registroPrestamoListRegistroPrestamoToAttach);
            }
            mes.setRegistroPrestamoList(attachedRegistroPrestamoList);
            em.persist(mes);
            for (RegistroPrestamo registroPrestamoListRegistroPrestamo : mes.getRegistroPrestamoList()) {
                Mes oldMesCodigoOfRegistroPrestamoListRegistroPrestamo = registroPrestamoListRegistroPrestamo.getMesCodigo();
                registroPrestamoListRegistroPrestamo.setMesCodigo(mes);
                registroPrestamoListRegistroPrestamo = em.merge(registroPrestamoListRegistroPrestamo);
                if (oldMesCodigoOfRegistroPrestamoListRegistroPrestamo != null) {
                    oldMesCodigoOfRegistroPrestamoListRegistroPrestamo.getRegistroPrestamoList().remove(registroPrestamoListRegistroPrestamo);
                    oldMesCodigoOfRegistroPrestamoListRegistroPrestamo = em.merge(oldMesCodigoOfRegistroPrestamoListRegistroPrestamo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMes(mes.getCodigo()) != null) {
                throw new PreexistingEntityException("Mes " + mes + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mes mes) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Mes persistentMes = em.find(Mes.class, mes.getCodigo());
            List<RegistroPrestamo> registroPrestamoListOld = persistentMes.getRegistroPrestamoList();
            List<RegistroPrestamo> registroPrestamoListNew = mes.getRegistroPrestamoList();
            List<String> illegalOrphanMessages = null;
            for (RegistroPrestamo registroPrestamoListOldRegistroPrestamo : registroPrestamoListOld) {
                if (!registroPrestamoListNew.contains(registroPrestamoListOldRegistroPrestamo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RegistroPrestamo " + registroPrestamoListOldRegistroPrestamo + " since its mesCodigo field is not nullable.");
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
            mes.setRegistroPrestamoList(registroPrestamoListNew);
            mes = em.merge(mes);
            for (RegistroPrestamo registroPrestamoListNewRegistroPrestamo : registroPrestamoListNew) {
                if (!registroPrestamoListOld.contains(registroPrestamoListNewRegistroPrestamo)) {
                    Mes oldMesCodigoOfRegistroPrestamoListNewRegistroPrestamo = registroPrestamoListNewRegistroPrestamo.getMesCodigo();
                    registroPrestamoListNewRegistroPrestamo.setMesCodigo(mes);
                    registroPrestamoListNewRegistroPrestamo = em.merge(registroPrestamoListNewRegistroPrestamo);
                    if (oldMesCodigoOfRegistroPrestamoListNewRegistroPrestamo != null && !oldMesCodigoOfRegistroPrestamoListNewRegistroPrestamo.equals(mes)) {
                        oldMesCodigoOfRegistroPrestamoListNewRegistroPrestamo.getRegistroPrestamoList().remove(registroPrestamoListNewRegistroPrestamo);
                        oldMesCodigoOfRegistroPrestamoListNewRegistroPrestamo = em.merge(oldMesCodigoOfRegistroPrestamoListNewRegistroPrestamo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = mes.getCodigo();
                if (findMes(id) == null) {
                    throw new NonexistentEntityException("The mes with id " + id + " no longer exists.");
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
            Mes mes;
            try {
                mes = em.getReference(Mes.class, id);
                mes.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mes with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RegistroPrestamo> registroPrestamoListOrphanCheck = mes.getRegistroPrestamoList();
            for (RegistroPrestamo registroPrestamoListOrphanCheckRegistroPrestamo : registroPrestamoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Mes (" + mes + ") cannot be destroyed since the RegistroPrestamo " + registroPrestamoListOrphanCheckRegistroPrestamo + " in its registroPrestamoList field has a non-nullable mesCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(mes);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mes> findMesEntities() {
        return findMesEntities(true, -1, -1);
    }

    public List<Mes> findMesEntities(int maxResults, int firstResult) {
        return findMesEntities(false, maxResults, firstResult);
    }

    private List<Mes> findMesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mes.class));
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

    public Mes findMes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mes.class, id);
        } finally {
            em.close();
        }
    }

    public int getMesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mes> rt = cq.from(Mes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
