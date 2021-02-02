package com.foxminded.university.controller.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.foxminded.university.model.Course;
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Teacher;

@Transactional
@Repository
public class CourseRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @PersistenceContext 
    EntityManager entityManager;
    
    @Transactional
    public void add(Course course) {        
        if (logger.isDebugEnabled()) {
                logger.debug("Inset new course ({}, {}, {})", course.getName(), course.getDescription(), course.getTeacher().getId());
            }
        entityManager.persist(course);
    }
    
    @Transactional(readOnly=true)
    public List<Course> getByTeacher(Teacher teacher) {
        if (logger.isDebugEnabled()) { 
            logger.debug("Query courses by teacher (id = {})", teacher.getId());
        }
        
        return entityManager.
               createQuery("SELECT c FROM Course c "
                         + "WHERE c.teacher.id = :teacherId ORDER BY c.id", Course.class)
               .setParameter("teacherId", teacher.getId())
               .getResultList(); 
    }
    
    
    @Transactional(readOnly=true)
    public List<Course> getByGroup(Group group) {
        if (logger.isDebugEnabled()) { 
            logger.debug("Query courses by group (id = {})", group.getId());
        }
        
        return entityManager
               .createQuery("SELECT c FROM Course c JOIN c.groups g "
                           + "WHERE g.id = :groupId ORDER BY c.id", Course.class)
               .setParameter("groupId", group.getId())
               .getResultList();
    } 
    
    @Transactional(readOnly=true)
    public List<Course> getFreeCourses(Group group) {
        if (logger.isDebugEnabled()) { 
            logger.debug("Query unassigned courses for group (id = {})", group.getId());
        }
        
        return entityManager
               .createQuery("SELECT c FROM Course c WHERE c NOT IN "
                         + "(SELECT c FROM Course c JOIN c.groups g WHERE g.id = :groupId) "
                         + "ORDER BY c.id", Course.class)
               .setParameter("groupId", group.getId()).getResultList();
    }
    
    @Transactional(readOnly=true)
    public Course getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get course by id ({})", id);
        }

        return entityManager.find(Course.class, id);
    }

    @Transactional
    public void delete(Course course) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete course with id = {}", course.getId());
        }
        entityManager
        .createQuery("DELETE FROM Course c WHERE c.id = :courseId")
        .setParameter("courseId", course.getId())
        .executeUpdate();
    }

    @Transactional
    public void update(Course course) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete course with id = {}", course.getId());
        }
        entityManager.merge(course);
    }
}
