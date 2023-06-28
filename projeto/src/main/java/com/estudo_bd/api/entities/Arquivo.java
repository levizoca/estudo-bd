package com.estudo_bd.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "arquivos")
public class Arquivo {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	

    @Column(length = 512)
	private String nome;

	@Column(length = 512)
	private String local;

	private long size;

    public Arquivo(Long id, String nome, long size, String local) { // mostrar os arquivos na tela 
		this.id = id;
		this.nome = nome;
		this.size = size;
		this.local = local;
	}
    
    public Arquivo() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}
}