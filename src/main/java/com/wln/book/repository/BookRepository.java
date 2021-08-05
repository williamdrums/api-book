package com.wln.book.repository;

import com.wln.book.model.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookRepository extends CrudRepository<Book,Long> {

    List<Book> findByTitle(String title);
}
