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
import com.foxminded.university.model.Group;
import com.foxminded.university.model.Student;

@Repository
public class GroupRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @PersistenceContext 
    EntityManager entityManager;
    
    @Transactional
    public void add(Group group) {
        if (logger.isDebugEnabled()) {
            logger.debug("Insert new group ({}, {})", group.getGroupName(), group.getFaculty().getId());
        }
        
        entityManager.persist(group);
    }
    
    @Transactional(readOnly=true)
    public List<Group> getAll() {
        logger.debug("Get all groups");
        
        return entityManager
               .createQuery("from Group g ORDER BY g.id", Group.class)
               .getResultList();
    }
    
    @Transactional(readOnly=true)
    public Group getById(int id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get group by id ({})", id);
        }
        
        return entityManager.find(Group.class, id);
    }
    
    @Transactional(readOnly=true)
    public List<Group> getByFaculty(Faculty faculty) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get froup by faculty (id = {})", faculty.getId());
        }
        
        return entityManager
               .createQuery("SELECT g FROM Group g WHERE g.faculty.id = :faculty_id "
                          + " ORDER BY g.id", Group.class)
               .setParameter("faculty_id", faculty.getId())
               .getResultList();
    }
    
    @Transactional(readOnly=true)
    public Group getByStudent(Student student) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get group by student (id = {})", student.getId());
        }
        
        return entityManager
               .createQuery("SELECT g FROM Group g JOIN g.students s WHERE s.id = :studentId", Group.class)
               .setParameter("studentId", student.getId())
               .getSingleResult();
    }
    
    @Transactional
    public void delete(Group group) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete group with id {}", group.getId());
        }
        
        entityManager
        .createQuery("DELETE FROM Group g WHERE g.id = :groupId")
        .setParameter("groupId", group.getId())
        .executeUpdate();
    }
    
    @Transactional
    public void update(Group group) {
        if (logger.isDebugEnabled()) {
            logger.debug("Update group with id {}", group.getId());
        }
        
        entityManager.merge(group);
    }
    
    @Transactional
    public void assignCourseToGroup(Group group, Course course) {
        if (logger.isDebugEnabled()) {
            logger.debug("Assign course (id = {}) to group (id = {})", course.getId(), group.getId());
        }
        
        Group currentGroup = getById(group.getId());
        Course currentCourse = entityManager.find(Course.class, course.getId());
        currentGroup.getCourses().add(currentCourse);
    }
    
    @Transactional
    public void deleteGroupsCourse(int groupId, int courseId) {
        if (logger.isDebugEnabled()) {
            logger.debug("Delete groups course: groupId = {}, courseId = {}", groupId, courseId);
        }
        
        Group group = getById(groupId);
        int courseIndex = getCourseIndex(group, courseId);
        if (courseIndex >= 0) {
            group.getCourses().remove(courseIndex);
        }
    }
    
    private int getCourseIndex(Group group, int courseId) {
        for (int i = 0; i < group.getCourses().size(); i++) {
            if (group.getCourses().get(i).getId() == courseId) { return i; }
        }
        return -1;
    }
}
