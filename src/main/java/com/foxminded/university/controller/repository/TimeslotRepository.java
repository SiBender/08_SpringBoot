package com.foxminded.university.controller.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.foxminded.university.model.Timeslot;

@Repository
public class TimeslotRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @PersistenceContext 
    EntityManager entityManager;
    
    @Transactional(readOnly=true)
    public List<Timeslot> getAll() {
        if (logger.isDebugEnabled()) {
            logger.debug("Get all timeslots");
        }
        
        return entityManager
               .createQuery("from Timeslot t ORDER BY t.description", Timeslot.class).getResultList();
    }
    
    @Transactional
    public void add(Timeslot timeslot) {
        if (logger.isDebugEnabled()) {
            logger.debug("Add timeslot - {}", timeslot.getDescription());
        }
        
        entityManager.persist(timeslot);
    }
    
    @Transactional
    public void delete(Timeslot timeslot) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete timeslot with ID = {}", timeslot.getId());
        }
        
        entityManager
        .createQuery("DELETE FROM Timeslot t WHERE t.id = :timeslotId")
        .setParameter("timeslotId", timeslot.getId()).executeUpdate();
    }
    
    @Transactional
    public void update(Timeslot timeslot) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update timeslot with ID = {}", timeslot.getId());
        }
        
        entityManager.merge(timeslot);
    }
    
    @Transactional(readOnly=true)
    public Timeslot getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get timeslot by id ({})", id);
        }
        
        return entityManager.find(Timeslot.class, id);
    }
}
