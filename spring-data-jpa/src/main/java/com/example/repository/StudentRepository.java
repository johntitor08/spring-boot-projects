package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

	// HQL: Sınıf ve değişken isimleriyle sorgular yazılır.
	// SQL: Veritabanındaki tablo ve sütun isimleriyle sorgular yazılır.
	// 'nativeQuery' parametresi false ise HQL, true ise SQL kullanılır. Parametrenin default değeri false'dur.
	
	@Query(value = "from Student", nativeQuery = false)
	List<Student> findAllStudents();
	
	@Query(value = "from Student s WHERE s.id = :studentId")
	Student findStudentById(Integer studentId);
	
}
