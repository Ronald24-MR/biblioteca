/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.IllegalOrphanException;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import Entitdades.Libros;
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
public class LibrosJpaController implements Serializable {

    public LibrosJpaController() {
        this.emf = Persistence.createEntityManagerFactory("bibliotecPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Libros libros) throws PreexistingEntityException, Exception {
        if (libros.getRegistroPrestamoList() == null) {
            libros.setRegistroPrestamoList(new ArrayList<RegistroPrestamo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RegistroPrestamo> attachedRegistroPrestamoList = new ArrayList<RegistroPrestamo>();
            for (RegistroPrestamo registroPrestamoListRegistroPrestamoToAttach : libros.getRegistroPrestamoList()) {
                registroPrestamoListRegistroPrestamoToAttach = em.getReference(registroPrestamoListRegistroPrestamoToAttach.getClass(), registroPrestamoListRegistroPrestamoToAttach.getCodigo());
                attachedRegistroPrestamoList.add(registroPrestamoListRegistroPrestamoToAttach);
            }
            libros.setRegistroPrestamoList(attachedRegistroPrestamoList);
            em.persist(libros);
            for (RegistroPrestamo registroPrestamoListRegistroPrestamo : libros.getRegistroPrestamoList()) {
                Libros oldLibrosCodigoOfRegistroPrestamoListRegistroPrestamo = registroPrestamoListRegistroPrestamo.getLibrosCodigo();
                registroPrestamoListRegistroPrestamo.setLibrosCodigo(libros);
                registroPrestamoListRegistroPrestamo = em.merge(registroPrestamoListRegistroPrestamo);
                if (oldLibrosCodigoOfRegistroPrestamoListRegistroPrestamo != null) {
                    oldLibrosCodigoOfRegistroPrestamoListRegistroPrestamo.getRegistroPrestamoList().remove(registroPrestamoListRegistroPrestamo);
                    oldLibrosCodigoOfRegistroPrestamoListRegistroPrestamo = em.merge(oldLibrosCodigoOfRegistroPrestamoListRegistroPrestamo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLibros(libros.getCodigo()) != null) {
                throw new PreexistingEntityException("Libros " + libros + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Libros libros) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Libros persistentLibros = em.find(Libros.class, libros.getCodigo());
            List<RegistroPrestamo> registroPrestamoListOld = persistentLibros.getRegistroPrestamoList();
            List<RegistroPrestamo> registroPrestamoListNew = libros.getRegistroPrestamoList();
            List<String> illegalOrphanMessages = null;
            for (RegistroPrestamo registroPrestamoListOldRegistroPrestamo : registroPrestamoListOld) {
                if (!registroPrestamoListNew.contains(registroPrestamoListOldRegistroPrestamo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RegistroPrestamo " + registroPrestamoListOldRegistroPrestamo + " since its librosCodigo field is not nullable.");
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
            libros.setRegistroPrestamoList(registroPrestamoListNew);
            libros = em.merge(libros);
            for (RegistroPrestamo registroPrestamoListNewRegistroPrestamo : registroPrestamoListNew) {
                if (!registroPrestamoListOld.contains(registroPrestamoListNewRegistroPrestamo)) {
                    Libros oldLibrosCodigoOfRegistroPrestamoListNewRegistroPrestamo = registroPrestamoListNewRegistroPrestamo.getLibrosCodigo();
                    registroPrestamoListNewRegistroPrestamo.setLibrosCodigo(libros);
                    registroPrestamoListNewRegistroPrestamo = em.merge(registroPrestamoListNewRegistroPrestamo);
                    if (oldLibrosCodigoOfRegistroPrestamoListNewRegistroPrestamo != null && !oldLibrosCodigoOfRegistroPrestamoListNewRegistroPrestamo.equals(libros)) {
                        oldLibrosCodigoOfRegistroPrestamoListNewRegistroPrestamo.getRegistroPrestamoList().remove(registroPrestamoListNewRegistroPrestamo);
                        oldLibrosCodigoOfRegistroPrestamoListNewRegistroPrestamo = em.merge(oldLibrosCodigoOfRegistroPrestamoListNewRegistroPrestamo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = libros.getCodigo();
                if (findLibros(id) == null) {
                    throw new NonexistentEntityException("The libros with id " + id + " no longer exists.");
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
            Libros libros;
            try {
                libros = em.getReference(Libros.class, id);
                libros.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The libros with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RegistroPrestamo> registroPrestamoListOrphanCheck = libros.getRegistroPrestamoList();
            for (RegistroPrestamo registroPrestamoListOrphanCheckRegistroPrestamo : registroPrestamoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Libros (" + libros + ") cannot be destroyed since the RegistroPrestamo " + registroPrestamoListOrphanCheckRegistroPrestamo + " in its registroPrestamoList field has a non-nullable librosCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(libros);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Libros> findLibrosEntities() {
        return findLibrosEntities(true, -1, -1);
    }

    public List<Libros> findLibrosEntities(int maxResults, int firstResult) {
        return findLibrosEntities(false, maxResults, firstResult);
    }

    private List<Libros> findLibrosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Libros.class));
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

    public Libros findLibros(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Libros.class, id);
        } finally {
            em.close();
        }
    }

    public int getLibrosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Libros> rt = cq.from(Libros.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
