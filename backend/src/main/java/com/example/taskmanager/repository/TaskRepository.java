package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t where t.owner = :owner " +
            "and (:status is null or t.status = :status) " +
            "and (:priority is null or t.priority = :priority) " +
            "and (:search is null or lower(t.title) like lower(concat('%', :search, '%')))")
    Page<Task> findByOwnerWithFilters(
            @Param("owner") User owner,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("search") String search,
            Pageable pageable);

    @Query("select t from Task t where (:status is null or t.status = :status) " +
            "and (:priority is null or t.priority = :priority) " +
            "and (:search is null or lower(t.title) like lower(concat('%', :search, '%')))")
    Page<Task> findAllWithFilters(
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("search") String search,
            Pageable pageable);
}
