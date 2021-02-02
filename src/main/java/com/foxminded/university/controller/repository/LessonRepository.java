package com.foxminded.university.controller.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.foxminded.university.model.Lesson;

@Repository
public class LessonRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @PersistenceContext 
    EntityManager entityManager;
    
    @Transactional
    public void add(Lesson lesson) {
        if (logger.isDebugEnabled()) {
            logger.debug("Insert new lesson({}, {}, {}, {})", lesson.getDate(), lesson.getTime().getId(),
                        lesson.getCourse().getId(), lesson.getClassroom().getId());
        }
        
        entityManager.persist(lesson);
    } 
    
    @Transactional(readOnly=true)
    public Lesson getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get lesson by id ({})", id);
        }
        
        return entityManager.find(Lesson.class, id);
    }
    
    @Transactional
    public void update(Lesson lesson) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update lesson with id = {}", lesson.getId());
        }
        
        entityManager.merge(lesson);
    }
    
    @Transactional
    public void delete(Lesson lesson) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update lesson with id = {}", lesson.getId());
        }

        entityManager.createQuery("DELETE FROM Lesson l WHERE l.id = :lessonId")
        .setParameter("lessonId", lesson.getId()).executeUpdate();
    }
}
