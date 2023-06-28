package com.estudo_bd.api.controllers;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.estudo_bd.api.entities.Arquivo;
import com.estudo_bd.api.repositories.ArquivoRepository;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/api")
@Slf4j
@CrossOrigin("*")
public class ArquivoController {

    @Autowired
    private ArquivoRepository repo;

    private final String pathArquivos;

    public ArquivoController(@Value("${app.path.arquivos}") String pathArquivos) {
        this.pathArquivos = pathArquivos;
    }

    @GetMapping("/upload")
	public String viewUpload(Model model) {
		List<Arquivo> listArquivos = repo.findAll(); 	// para mostrar os arquivos na tela
		model.addAttribute("listArquivos", listArquivos);
		return "upload"; 							// direcionar para a pagina home
	}

    @PostMapping("/upload")
    public String salvarArquivo(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {

        Arquivo arquivo = new Arquivo();

        var path = pathArquivos;
        var uuid = UUID.randomUUID();
        var caminho = path + uuid + "." + extrairExtensão(file.getOriginalFilename());
        var local = uuid + "." + extrairExtensão(file.getOriginalFilename());

        arquivo.setNome(file.getOriginalFilename());
        arquivo.setLocal(local);
        arquivo.setSize(file.getSize());

        try {
            Files.copy(file.getInputStream(), Path.of(caminho), StandardCopyOption.REPLACE_EXISTING);
            repo.save(arquivo);
            ra.addFlashAttribute("message", "O arquivo foi upado com sucesso!");
            return "redirect:/api/upload";
        } catch (Exception e) {
            log.error("Erro ao carregar arquivo: ", e);
            ra.addFlashAttribute("message", "O arquivo não foi upado.");
            return "redirect:/api/upload";
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
        var localArquivo = arquivo1.get().getLocal();

        byte[] arquivo = Files.readAllBytes( Paths.get(pathArquivos + localArquivo) );

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");

        HttpEntity<byte[]> entity = new HttpEntity<byte[]>( arquivo, httpHeaders);

        return entity;
    }
}