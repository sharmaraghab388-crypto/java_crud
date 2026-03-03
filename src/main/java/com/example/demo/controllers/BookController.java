package com.example.demo.controllers;

import com.example.demo.entities.Book;
import com.example.demo.kafka.BookConsumer;
import com.example.demo.kafka.BookProducer;
import com.example.demo.redis.RedisClient;
import com.example.demo.services.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final RedisClient redisClient;
    private final BookProducer bookProducer;


    public BookController(BookService bookService, RedisClient redisClient, BookProducer bookProducer) {
        this.bookService = bookService;
        this.redisClient = redisClient;
        this.bookProducer = bookProducer;
    }
    @GetMapping("/all")
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    @PostMapping("/create")
    public Book saveBook(@RequestBody Book book)
    {
        bookService.saveBook(book);
        redisClient.delete(1,"top_rated_book");
        return book;
    }

    @GetMapping("/best/rated")
    public Book bestBook()
    {
        String key = "top_rated_book";
        Book cached = redisClient.get(1, key, Book.class);

        if (cached != null) {
            System.out.println("From Redis ");
            this.bookProducer.sendMessage(cached.getName());
            return cached;
        }
        List<Book> books = bookService.getAllBooks();
        books.sort((b1,b2)-> b2.getRating().compareTo(b1.getRating()));
        if(books.size() > 0){
            Book topBook =  books.get(0);
            this.bookProducer.sendMessage(bestBook().getName());
            redisClient.set(1,key,topBook);
            return topBook;
        }
        return null;
    }
}
