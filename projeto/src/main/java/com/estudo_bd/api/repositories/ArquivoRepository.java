package com.estudo_bd.api.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.estudo_bd.api.entities.Arquivo;

@Repository
public interface ArquivoRepository extends JpaRepository<Arquivo, Long> {                           // interface para o repositorio do arquivo
    @Query("SELECT new Arquivo(a.id, a.nome, a.size, a.local) FROM Arquivo a ORDER BY a.nome DESC") // puxar dados e mostrá-los na tela
    List<Arquivo> findAll();                                                                        // lista de arquivos
}