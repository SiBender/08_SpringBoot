package com.foxminded.university.controller.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.foxminded.university.model.Faculty;

@Repository
public class FacultyRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @PersistenceContext 
    EntityManager entityManager;
    
    @Transactional
    public void add(Faculty faculty) {        
        if (logger.isDebugEnabled()) {
            logger.debug("Create new faculty ({}, {})", faculty.getShortName(), faculty.getFullName());
        }
        entityManager.persist(faculty);
    }
    
    @Transactional(readOnly=true)
    public List<Faculty> getAll() {
        if (logger.isDebugEnabled()) { 
            logger.debug("Get all faculties");
        }
        
        return entityManager
               .createQuery("from Faculty f ORDER BY f.id", Faculty.class).getResultList();
    }
    
    @Transactional(readOnly=true)
    public Faculty getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get faculty by id ({})", id);
        }
        
        return entityManager.find(Faculty.class, id);
    }
    
    @Transactional
    public void delete(Faculty faculty) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete faculty, id = {}", faculty.getId());
        }
        
        entityManager
        .createQuery("DELETE FROM Faculty f WHERE f.id = :facultyId")
        .setParameter("facultyId", faculty.getId())
        .executeUpdate();
    }
    
    @Transactional
    public void update(Faculty faculty) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update faculty with id = {}", faculty.getId());
        } 
        
        entityManager.merge(faculty);
    }
}
