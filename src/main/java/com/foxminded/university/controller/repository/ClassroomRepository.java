package com.foxminded.university.controller.repository;

import com.foxminded.university.model.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class ClassroomRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @PersistenceContext 
    EntityManager entityManager;
    
    @Transactional(readOnly=true) 
    public List<Classroom> getAll() {
        if (logger.isDebugEnabled()) { logger.debug("Get all classrooms"); }
        
        return entityManager
               .createQuery("FROM Classroom c ORDER BY c.number ASC", Classroom.class)
               .getResultList();
    }
    
    @Transactional(readOnly=true)
    public Classroom getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get classroom by id ({})", id);
        }
        
        return entityManager.find(Classroom.class, id);
    }
    
    public Classroom getByNumber(String number) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get classroom by number ({})", number);
        }
        
        return entityManager
               .createQuery("SELECT c FROM Classroom c WHERE c.number = ?1 ", Classroom.class)
               .setParameter(1, number).getSingleResult(); 
    }
    
    public void add(Classroom classroom) {        
        if (logger.isDebugEnabled()) {
            logger.debug("Create new classroom ({}, {})", classroom.getNumber(), classroom.getCapacity());
        }
        
        entityManager.persist(classroom);
    }
    
    public void delete(Classroom classroom) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete classroom with ID = {}", classroom.getId());
        }

        entityManager.createQuery("DELETE FROM Classroom c WHERE c.id = :classroomId")
        .setParameter("classroomId", classroom.getId())
        .executeUpdate();
    }
    
    public void update(Classroom classroom) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update classroom with ID = {}", classroom.getId());
        }
        entityManager.merge(classroom);
    }
}
