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
import Entitdades.TipoPago;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class TipoPagoJpaController implements Serializable {

    public TipoPagoJpaController() {
        this.emf = Persistence.createEntityManagerFactory("bibliotecPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoPago tipoPago) throws PreexistingEntityException, Exception {
        if (tipoPago.getRegistroPrestamoList() == null) {
            tipoPago.setRegistroPrestamoList(new ArrayList<RegistroPrestamo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RegistroPrestamo> attachedRegistroPrestamoList = new ArrayList<RegistroPrestamo>();
            for (RegistroPrestamo registroPrestamoListRegistroPrestamoToAttach : tipoPago.getRegistroPrestamoList()) {
                registroPrestamoListRegistroPrestamoToAttach = em.getReference(registroPrestamoListRegistroPrestamoToAttach.getClass(), registroPrestamoListRegistroPrestamoToAttach.getCodigo());
                attachedRegistroPrestamoList.add(registroPrestamoListRegistroPrestamoToAttach);
            }
            tipoPago.setRegistroPrestamoList(attachedRegistroPrestamoList);
            em.persist(tipoPago);
            for (RegistroPrestamo registroPrestamoListRegistroPrestamo : tipoPago.getRegistroPrestamoList()) {
                TipoPago oldTipopagoCodigoOfRegistroPrestamoListRegistroPrestamo = registroPrestamoListRegistroPrestamo.getTipopagoCodigo();
                registroPrestamoListRegistroPrestamo.setTipopagoCodigo(tipoPago);
                registroPrestamoListRegistroPrestamo = em.merge(registroPrestamoListRegistroPrestamo);
                if (oldTipopagoCodigoOfRegistroPrestamoListRegistroPrestamo != null) {
                    oldTipopagoCodigoOfRegistroPrestamoListRegistroPrestamo.getRegistroPrestamoList().remove(registroPrestamoListRegistroPrestamo);
                    oldTipopagoCodigoOfRegistroPrestamoListRegistroPrestamo = em.merge(oldTipopagoCodigoOfRegistroPrestamoListRegistroPrestamo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTipoPago(tipoPago.getCodigo()) != null) {
                throw new PreexistingEntityException("TipoPago " + tipoPago + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TipoPago tipoPago) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoPago persistentTipoPago = em.find(TipoPago.class, tipoPago.getCodigo());
            List<RegistroPrestamo> registroPrestamoListOld = persistentTipoPago.getRegistroPrestamoList();
            List<RegistroPrestamo> registroPrestamoListNew = tipoPago.getRegistroPrestamoList();
            List<String> illegalOrphanMessages = null;
            for (RegistroPrestamo registroPrestamoListOldRegistroPrestamo : registroPrestamoListOld) {
                if (!registroPrestamoListNew.contains(registroPrestamoListOldRegistroPrestamo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RegistroPrestamo " + registroPrestamoListOldRegistroPrestamo + " since its tipopagoCodigo field is not nullable.");
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
            tipoPago.setRegistroPrestamoList(registroPrestamoListNew);
            tipoPago = em.merge(tipoPago);
            for (RegistroPrestamo registroPrestamoListNewRegistroPrestamo : registroPrestamoListNew) {
                if (!registroPrestamoListOld.contains(registroPrestamoListNewRegistroPrestamo)) {
                    TipoPago oldTipopagoCodigoOfRegistroPrestamoListNewRegistroPrestamo = registroPrestamoListNewRegistroPrestamo.getTipopagoCodigo();
                    registroPrestamoListNewRegistroPrestamo.setTipopagoCodigo(tipoPago);
                    registroPrestamoListNewRegistroPrestamo = em.merge(registroPrestamoListNewRegistroPrestamo);
                    if (oldTipopagoCodigoOfRegistroPrestamoListNewRegistroPrestamo != null && !oldTipopagoCodigoOfRegistroPrestamoListNewRegistroPrestamo.equals(tipoPago)) {
                        oldTipopagoCodigoOfRegistroPrestamoListNewRegistroPrestamo.getRegistroPrestamoList().remove(registroPrestamoListNewRegistroPrestamo);
                        oldTipopagoCodigoOfRegistroPrestamoListNewRegistroPrestamo = em.merge(oldTipopagoCodigoOfRegistroPrestamoListNewRegistroPrestamo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipoPago.getCodigo();
                if (findTipoPago(id) == null) {
                    throw new NonexistentEntityException("The tipoPago with id " + id + " no longer exists.");
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
            TipoPago tipoPago;
            try {
                tipoPago = em.getReference(TipoPago.class, id);
                tipoPago.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoPago with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RegistroPrestamo> registroPrestamoListOrphanCheck = tipoPago.getRegistroPrestamoList();
            for (RegistroPrestamo registroPrestamoListOrphanCheckRegistroPrestamo : registroPrestamoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TipoPago (" + tipoPago + ") cannot be destroyed since the RegistroPrestamo " + registroPrestamoListOrphanCheckRegistroPrestamo + " in its registroPrestamoList field has a non-nullable tipopagoCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoPago);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TipoPago> findTipoPagoEntities() {
        return findTipoPagoEntities(true, -1, -1);
    }

    public List<TipoPago> findTipoPagoEntities(int maxResults, int firstResult) {
        return findTipoPagoEntities(false, maxResults, firstResult);
    }

    private List<TipoPago> findTipoPagoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoPago.class));
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

    public TipoPago findTipoPago(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoPago.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoPagoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoPago> rt = cq.from(TipoPago.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
