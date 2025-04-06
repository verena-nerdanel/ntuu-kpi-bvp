package org.vg.books.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vg.books.entities.BookOrderEntity;
import org.vg.books.repositories.BookOrderRepository;
import org.vg.books.repositories.BookRepository;

import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/orders")
@RequiredArgsConstructor
public class OrderController {

    private final BookRepository bookRepository;
    private final BookOrderRepository bookOrderRepository;

    @GetMapping(path = {""})
    public String list(Model model) {
        final List<BookOrderEntity> orders = (List<BookOrderEntity>) bookOrderRepository.findAll();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/new")
    public String createDialog(Model model) {
        model.addAttribute("order", BookOrderEntity.builder()
                .date(new Date())
                .build());
        model.addAttribute("books", bookRepository.findAllOrdered());
        return "order-edit";
    }

    @PostMapping(path = "/new", consumes = "application/x-www-form-urlencoded")
    public String create(BookOrderEntity order) {
        order.setBook(bookRepository.findById(order.getBook().getId()).get()); // reassign

        bookOrderRepository.save(order);
        return "redirect:/orders";
    }

    @GetMapping("/edit/{id}")
    public String editDialog(@PathVariable("id") long id, Model model) {
        model.addAttribute("order", bookOrderRepository.findById(id).get());
        model.addAttribute("books", bookRepository.findAllOrdered());
        return "order-edit";
    }

    @PostMapping(path = "/edit/{id}", consumes = "application/x-www-form-urlencoded")
    public String edit(@PathVariable("id") long id, BookOrderEntity order) {
        // order.setBook(bookRepository.findById(order.getBook().getId()).get()); // reassign
        bookOrderRepository.save(order);
        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteDialog(@PathVariable("id") long id, Model model) {
        model.addAttribute("order", bookOrderRepository.findById(id).get());
        return "order-delete";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        bookOrderRepository.deleteById(id);
        return "redirect:/orders";
    }
}
