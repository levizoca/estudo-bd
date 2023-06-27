package com.estudo_bd.api.controllers;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.estudo_bd.api.entities.Arquivo;
import com.estudo_bd.api.repositories.ArquivoRepository;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/upload", produces = {"application/json"})
@Slf4j
@CrossOrigin("*")
public class UploadArquivoController {

    @Autowired
    private ArquivoRepository repo;

    private final String pathArquivos;

    public UploadArquivoController(@Value("${app.path.arquivos}") String pathArquivos) {
        this.pathArquivos = pathArquivos;
    }
    
    @PostMapping("/arquivo")
    public ResponseEntity<String> salvarArquivo(@RequestParam("file") MultipartFile file) {

        Arquivo arquivo = new Arquivo();

        var path = pathArquivos;
        var uuid = UUID.randomUUID();
        var caminho = path + uuid + "." + extrairExtensão(file.getOriginalFilename());
        var nome = uuid + "." + extrairExtensão(file.getOriginalFilename());

        arquivo.setNome(nome);


        try {
            Files.copy(file.getInputStream(), Path.of(caminho), StandardCopyOption.REPLACE_EXISTING);
            repo.save(arquivo);
            return new ResponseEntity<>("{ \"mensagem\": \"Arquivo salvo com sucesso!\"}", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erro ao carregar arquivo: ", e);
            return new ResponseEntity<>("{ \"mensagem\": \"Erro ao carregar arquivo!\"}", HttpStatus.OK);
        }
    }

    private String extrairExtensão(String nomeArquivo) {
        int i = nomeArquivo.lastIndexOf(".");
        return nomeArquivo.substring(i + 1);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public HttpEntity<byte[]> download(@Param("id") Long id) throws IOException {

        Optional<Arquivo> arquivo1 = repo.findById(id);

        var nomeArquivo = arquivo1.get().getNome();

        byte[] arquivo = Files.readAllBytes( Paths.get(pathArquivos + nomeArquivo) );

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");

        HttpEntity<byte[]> entity = new HttpEntity<byte[]>( arquivo, httpHeaders);

        return entity;
    }
}