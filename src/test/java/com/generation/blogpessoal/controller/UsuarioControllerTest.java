package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll 
	void start(){
		
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Root", "root@root.com", "rootroot", " "));
	}
	@Test
	@DisplayName("😁 Cadastrar Usuario")
	public void deveCriarUmUsuario() {
		
		/* Corpo da Requição */
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
				"Gabriel Rodrigues", "gabriel@email.com.br", "12345678", " "));
		
		/* Requisição Http */
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("😁 Não Deve Duplicar Usuario")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Amanda Tsai", "amanda@email.com.br", "12345678", " "));
		
		/* Corpo da Requição */
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
				"Amanda Tsai", "amanda@email.com.br", "12345678", " "));
		
		/* Requisição Http */
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("😁 Deve Atualizar Usuario")
	public void deveAtualizarUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
				"Kendal Katherine", "kendal@email.com.br", "12345678", " "));
				
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(usuarioCadastrado.get().getId(),
				"Kendal Katherine Corria", "kendalk@email.com.br", "987654321", " "));
		
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("😁 Deve Listar Todos os Usuario")
	public void deveListarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Vitor Nascimento", "vitor@email.com.br", "12345678", " "));
					
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Samara Almeida", "samara@email.com.br", "12345678", " "));
		
		ResponseEntity<String> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("😁 Deve Buscar o Usuario pelo ID")
	public void devebuscarUsuariosPeloId() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Vitor Nascimento", "vitor@email.com.br", "12345678", " "));
					
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Samara Almeida", "samara@email.com.br", "12345678", " "));
		
		ResponseEntity<String> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/1", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("😁 Login do Usuário")
	public void deveAutenticarUsuario() {

		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Kendal Katherine", "kendal@email.com.br", "12345678", " "));
				
		HttpEntity<UsuarioLogin> corpoRequisicao = new HttpEntity<UsuarioLogin>(new UsuarioLogin(0L,
				"", "kendal@email.com.br", "12345678", " ", " "));

		ResponseEntity<UsuarioLogin> corpoResposta = testRestTemplate
			.exchange("/usuarios/logar", HttpMethod.POST, corpoRequisicao, UsuarioLogin.class);

		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

	}
	
}
