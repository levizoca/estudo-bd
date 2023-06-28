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
    private ArquivoRepository repo;     // injeção de dependencia do repositorio

    private final String pathArquivos;  // string para o caminho dos arquivos

    public ArquivoController(@Value("${app.path.arquivos}") String pathArquivos) {  // construtor para o caminho dos arquivos
        this.pathArquivos = pathArquivos;
    }

    @GetMapping("/upload")
	public String viewUpload(Model model) {
		List<Arquivo> listArquivos = repo.findAll(); 	                    // para mostrar os arquivos na tela
		model.addAttribute("listArquivos", listArquivos);     
		return "upload"; 							                        // direcionar para a pagina home
	}

    @PostMapping("/upload")
    public String salvarArquivo(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {

        Arquivo arquivo = new Arquivo();    // instancia do arquivo

        var path = pathArquivos;            // caminho dos arquivos
        var uuid = UUID.randomUUID();       
        var caminho = path + uuid + "." + extrairExtensão(file.getOriginalFilename());  
        var local = uuid + "." + extrairExtensão(file.getOriginalFilename());           // variável para gravar local do arquivo          

        arquivo.setNome(file.getOriginalFilename());    // setar nome do arquivo
        arquivo.setLocal(local);                        // setar local do arquivo
        arquivo.setSize(file.getSize());                // setar tamanho do arquivo

        try {
            Files.copy(file.getInputStream(), Path.of(caminho), StandardCopyOption.REPLACE_EXISTING);       // copiar arquivo para o caminho
            repo.save(arquivo);
            log.info("Arquivo salvo com sucesso: " + arquivo);                                              // mensagem de sucesso
            ra.addFlashAttribute("message", "O arquivo foi upado com sucesso!");                          // mensagem de sucesso
            return "redirect:/api/upload";                                                                  // redirecionar para a pagina home
        } catch (Exception e) {
            log.error("Erro ao carregar arquivo: ", e);                                                 // mensagem de erro
            ra.addFlashAttribute("message", "O arquivo não foi upado.");                              // mensagem de erro
            return "redirect:/api/upload";
        }
    }

    private String extrairExtensão(String nomeArquivo) {    // função para extrair a extensão do arquivo
        int i = nomeArquivo.lastIndexOf(".");               // variável para pegar a extensão do arquivo
        return nomeArquivo.substring(i + 1);                // retornar a extensão do arquivo
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public HttpEntity<byte[]> download(@Param("id") Long id) throws IOException {       // função para fazer o download do arquivo

        Optional<Arquivo> arquivo1 = repo.findById(id);                                 // pegar o arquivo pelo id

        var nomeArquivo = arquivo1.get().getNome();                                     // variável para pegar o nome do arquivo
        var localArquivo = arquivo1.get().getLocal();                                   // variável para pegar o local do arquivo

        byte[] arquivo = Files.readAllBytes( Paths.get(pathArquivos + localArquivo) );  // variável para ler o arquivo

        HttpHeaders httpHeaders = new HttpHeaders();                                    // variável para setar o cabeçalho do arquivo

        httpHeaders.add("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\""); // setar o cabeçalho do arquivo

        HttpEntity<byte[]> entity = new HttpEntity<byte[]>( arquivo, httpHeaders);      // variável para retornar o arquivo

        return entity;                                                                  // retornar o arquivo
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String delete(@Param("id") Long id, RedirectAttributes ra) throws IOException {  // função para deletar o arquivo

        Optional<Arquivo> arquivo1 = repo.findById(id);                                     // pegar o arquivo pelo id

        var localArquivo = arquivo1.get().getLocal();                                       // variável para pegar o local do arquivo

        Files.deleteIfExists( Paths.get(pathArquivos + localArquivo) );                     // deletar o arquivo

        repo.deleteById(id);                                                                // deletar o arquivo pelo id

        ra.addFlashAttribute("message", "O arquivo foi deletado com sucesso!");             // mensagem de sucesso
        return "redirect:/api/upload";                                                      // redirecionar para a pagina home
    }
}