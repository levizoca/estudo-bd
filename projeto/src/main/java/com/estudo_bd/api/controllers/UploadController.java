package com.estudo_bd.api.controllers;

import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import com.estudo_bd.api.entities.Arquivo;
import com.estudo_bd.api.entities.Document;
import com.estudo_bd.api.repositories.ArquivoRepository;
import com.estudo_bd.api.repositories.DocumentRepository;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UploadController {

	@Autowired
	private DocumentRepository repo;

	@Autowired
	private ArquivoRepository repo2;

	@GetMapping("/upload")
	public String viewHomePage(Model model) {
		List<Document> listDocs = repo.findAll(); 	// para mostrar os arquivos na tela
		model.addAttribute("listDocs", listDocs);
		return "upload"; 							// direcionar para a pagina home
	}

	@GetMapping("/upload2")
	public String viewUpload(Model model) {
		List<Arquivo> listArquivos = repo2.findAll(); 	// para mostrar os arquivos na tela
		model.addAttribute("listArquivos", listArquivos);
		return "upload2"; 							// direcionar para a pagina home
	}

	@PostMapping("/upload")
	public String uploadFile(@RequestParam("document") MultipartFile multipartFile, RedirectAttributes ra) throws IOException { // redirecionar o botão, usando referencia para o atributo
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()); 	// utilizado uma classe util do spring para limpar
		
		Document document = new Document();

		document.setName(fileName);
		document.setContent(multipartFile.getBytes());
		document.setSize(multipartFile.getSize());
		document.setUploadTime(new Date());

		repo.save(document);

		ra.addFlashAttribute("message", "O arquivo foi upado com sucesso!");

		return "redirect:/upload";
	}

	@GetMapping("/download")
	public void downloadFile(@Param("id") Long id, HttpServletResponse response) throws Exception {
		Optional<Document> result = repo.findById(id);
		if (!result.isPresent()) {
			throw new Exception("Não foi possível achar documento com o ID: " + id);
		}

		Document document = result.get();

		// dentro do IF precisa colocar no "setContentType" o tipo de arquivo,
		// atualmente só está "reconhecendo" para download arquivos PNG e PDF
		if (document.getName().endsWith("png")) {
			response.setContentType("image/png"); 			// tipo de conteudo imagem png
		} else if (document.getName().endsWith("pdf")) {
			response.setContentType("application/pdf"); 	// tipo de conteudo pdf
		} else if (document.getName().endsWith("mp4")) { 		
			response.setContentType("video/mp4");			// conteudo arquivo mp4
		} else if (document.getName().endsWith("jpeg")) { 		
			response.setContentType("image/jpeg");			// conteudo arquivo imagem jpeg
		}

		String headerKey = "Content-Disponsion";
		String headerValue = "attachment; filename=" + document.getName();

		response.setHeader(headerKey, headerValue);

		ServletOutputStream outputStream = response.getOutputStream(); // para mandar um documento para a data binária

		outputStream.write(document.getContent());
		outputStream.close();
	}
}