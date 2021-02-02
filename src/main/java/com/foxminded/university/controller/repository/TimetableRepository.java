package com.foxminded.university.controller.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.foxminded.university.model.DateInterval;
import com.foxminded.university.model.Lesson;
import com.foxminded.university.model.Student;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.model.Timetable;

@Repository
public class TimetableRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @PersistenceContext 
    EntityManager entityManager;
    
    @Transactional(readOnly=true)
    public Timetable getByStudent(Student student, DateInterval dateInterval) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get timetable by student ({}, {}, {})", 
                     student.getId(), dateInterval.getStartDate(), dateInterval.getEndDate());
        }
        
        Timetable timetable = new Timetable();
        timetable.setDateInterval(dateInterval);
        timetable.setLessons(getLessonsByStudent(student, dateInterval));
        return timetable;
    }
    
    @Transactional(readOnly=true)
    public Timetable getByTeacher(Teacher teacher, DateInterval dateInterval) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get timetable by teacher ({}, {}, {})", 
                     teacher.getId(), dateInterval.getStartDate(), dateInterval.getEndDate());
        }
        
        Timetable timetable = new Timetable();
        timetable.setDateInterval(dateInterval);
        timetable.setLessons(getLessonsByTeacher(teacher, dateInterval));
        return timetable;
    }
    
    private List<Lesson> getLessonsByStudent(Student student, DateInterval dateInterval) {       
        return entityManager
               .createQuery("SELECT l FROM Lesson l "
                          + "JOIN FETCH l.course c "
                          + "JOIN c.groups g "
                          + "JOIN g.students s "
                          + "WHERE s.id = :studentId "
                          + "AND (l.date BETWEEN :startDate AND :endDate)", Lesson.class)
               .setParameter("studentId", student.getId())
               .setParameter("startDate", dateInterval.getStartDate())
               .setParameter("endDate", dateInterval.getEndDate())
               .getResultList();
    }
    
    private List<Lesson> getLessonsByTeacher(Teacher teacher, DateInterval dateInterval) {
        return entityManager
               .createQuery("SELECT l FROM Lesson l "
                          + "JOIN FETCH l.course c "
                          + "JOIN c.teacher t "
                          + "WHERE t.id = :teacherID "
                          + "AND (l.date BETWEEN :startDate AND :endDate)", Lesson.class)
               .setParameter("teacherID", teacher.getId())
               .setParameter("startDate", dateInterval.getStartDate())
               .setParameter("endDate", dateInterval.getEndDate())
               .getResultList();
    }
}
