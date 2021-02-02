package com.foxminded.university.controller.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.foxminded.university.model.Student;

@Repository
public class StudentRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @PersistenceContext 
    EntityManager entityManager;
    
    @Transactional(readOnly=true)
    public int getCount() {
        if (logger.isDebugEnabled()) {
            logger.debug("Get count of students");
        }
        return (int)entityManager
               .createQuery("SELECT COUNT(*) FROM Student s").getSingleResult();
    }
    
    @Transactional
    public void add(Student student) {
        if (logger.isDebugEnabled()) {
            logger.debug("Insert new student ({}, {}, {})", student.getFirstName(), student.getLastName(), student.getGroup().getId());
        }
        
        entityManager.persist(student);
    }
    
    @Transactional(readOnly=true)
    public Student getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get student by id ({})", id);
        }

        return entityManager.find(Student.class, id);
    }
    
    @Transactional
    public void delete(Student student) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete student with id = {}", student.getId());
        }

        entityManager
        .createQuery("DELETE Student s WHERE s.id = :studentId")
        .setParameter("studentId", student.getId())
        .executeUpdate();
    }
    
    @Transactional
    public void update(Student student) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update student with id = {}", student.getId());
        }
        
        entityManager.merge(student);
    }
}
