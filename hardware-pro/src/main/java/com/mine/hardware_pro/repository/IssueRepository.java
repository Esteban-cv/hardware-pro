package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.Issue;


public interface IssueRepository extends JpaRepository<Issue,Long> {
}
