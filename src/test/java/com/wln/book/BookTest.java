package com.wln.book;

import com.wln.book.model.Book;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookTest {

    private static final String API_ROOT = "http://localhost:8080/api/books";

    private Book criarLivroAleatorio() {
        Book book = new Book();
        book.setTitle(randomAlphabetic(10));
        book.setAuthor(randomAlphabetic(15));
        return book;
    }

    private String criarLivroPorUri(Book book) {
        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .post(API_ROOT);
        return API_ROOT + "/" + response.jsonPath().get("id");
    }

    @Test
    public void quandoObterTodosOsLivros_entaoOk() {
        Response response = RestAssured.get(API_ROOT);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    public void quandoObterLivrosPorTitulo_entaoOk() {
        Book book = criarLivroAleatorio();
        criarLivroPorUri(book);

        Response response = RestAssured.get(
                API_ROOT + "/title/" + book.getTitle());

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertTrue(response.as(List.class).size() > 0);
    }

    @Test
    public void quandoCriarLivroPorId_entaoOk() {
        Book book = criarLivroAleatorio();
        String location = criarLivroPorUri(book);
        Response response = RestAssured.get(location);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(book.getTitle(), response.jsonPath().get("title"));
    }

    @Test
    public void quandoNaoExistirLivroPorId_entaoNaoEncontrado() {
        Response response = RestAssured.get(API_ROOT + "/" + randomNumeric(4));

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    public void quandoCriarUmNovoLivro_entaoOk() {
        Book book = criarLivroAleatorio();
        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .post(API_ROOT);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
    }

    @Test
    public void quandoLivroInvalido_entaoErro() {
        Book book = criarLivroAleatorio();
        book.setAuthor(null);
        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .post(API_ROOT);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());

    }

    @Test
    public void quandoAtualizarLivro_entaoOk() {
        Book book = criarLivroAleatorio();
        String location = criarLivroPorUri(book);
        book.setId(Long.parseLong(location.split("api/books/")[1]));
        book.setAuthor("Novo Autor");

        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .put(location);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        response = RestAssured.get(location);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Novo Autor", response.jsonPath()
                .get("author"));
    }

    @Test
    public void quandoDeletarLivro_entaoOk() {
        Book book = criarLivroAleatorio();
        String location = criarLivroPorUri(book);
        Response response = RestAssured.delete(location);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        response = RestAssured.get(location);
        assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatusCode());
    }


}
