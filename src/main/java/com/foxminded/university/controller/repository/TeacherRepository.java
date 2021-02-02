package com.foxminded.university.controller.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.foxminded.university.model.Course;
import com.foxminded.university.model.Faculty;
import com.foxminded.university.model.Teacher;

@Transactional
@Repository
public class TeacherRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @PersistenceContext 
    EntityManager entityManager;
    
    @Transactional(readOnly=true)
    public Teacher getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get teacher by id ({})", id);
        }
        
        return entityManager.find(Teacher.class, id);
    }
    
    @Transactional
    public void add(Teacher teacher) {
        if (logger.isDebugEnabled()) {
            logger.debug("Insert new teacher ({}, {}, {})", 
            teacher.getFirstName(), teacher.getLastName(), teacher.getFaculty().getId());
        }
        entityManager.persist(teacher);
    }
    
    @Transactional(readOnly=true)
    public List<Teacher> getByFaculty(Faculty faculty) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get teacher by faculty (id = {})", faculty.getId());
        }
        
        return entityManager
               .createQuery("SELECT t FROM Teacher t WHERE t.faculty.id = ?1 "
                          + "ORDER BY t.id", Teacher.class)
               .setParameter(1, faculty.getId()).getResultList();
    }
    
    @Transactional(readOnly=true)
    public List<Teacher> getAll() {
        if (logger.isDebugEnabled()) {
            logger.debug("Get all teachers list");
        }
        
        return entityManager
               .createQuery("FROM Teacher t ORDER BY t.id", Teacher.class)
               .getResultList();
    }
    
    @Transactional(readOnly=true)
    private List<Course> getCoursesByTeacher(Teacher teacher) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get cources by teacher. teacher_id = {}", teacher.getId());
        }
        
        return entityManager
               .createQuery("SELECT c FROM Course c WHERE c.teacher.id = :teacher_id", Course.class)
               .setParameter("teacher_id", teacher.getId())
               .getResultList();
    }  
    
    @Transactional
    public void delete(Teacher teacher) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete teacher with id ({})", teacher.getId());
        }
        
        entityManager
        .createQuery("DELETE FROM Teacher t WHERE t.id = :teacherId")
        .setParameter("teacherId", teacher.getId())
        .executeUpdate();
    }
    
    @Transactional
    public void update(Teacher teacher) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update teacher with id ({})", teacher.getId());
        }
        
        entityManager.merge(teacher);
    }
}
