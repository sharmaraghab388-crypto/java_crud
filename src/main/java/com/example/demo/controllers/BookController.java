package com.example.demo.controllers;

import com.example.demo.entities.Book;
import com.example.demo.services.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    @GetMapping("/all")
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    @PostMapping("/create")
    public Book saveBook(@RequestBody Book book)
    {
        bookService.saveBook(book);
        return book;
    }
}
