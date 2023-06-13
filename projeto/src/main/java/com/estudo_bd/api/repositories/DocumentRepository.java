package com.estudo_bd.api.repositories;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.estudo_bd.api.entities.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> { // repositorio de documento pela Jpa
	@Query("SELECT new Document(d.id, d.name, d.size) FROM Document d ORDER BY d.uploadTime DESC")
	List<Document> findAll(); 
}
