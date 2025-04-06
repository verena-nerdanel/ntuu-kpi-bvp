package org.vg.books.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vg.books.dto.AverageStat;
import org.vg.books.dto.StatsDto;
import org.vg.books.repositories.BookOrderRepository;
import org.vg.books.repositories.BookRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Controller
@RequestMapping(value = "")
@RequiredArgsConstructor
public class StatController {

    private final BookRepository bookRepository;
    private final BookOrderRepository bookOrderRepository;

    @GetMapping(path = {"/stats"})
    public String stats(Model model) {

        final StatsDto stats = StatsDto.builder()
                .topBooks(bookOrderRepository.getTopBooks())
                .topAuthors(bookOrderRepository.getTopAuthors())
                .topGenres(bookOrderRepository.getTopGenres())
                .build();

        model.addAttribute("stats", stats);
        return "stats";
    }

    @GetMapping(path = {"/stats-avg"})
    public String statsAvg(Model model) {

        final Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        final Date sinceTime = c.getTime();

        final List<AverageStat> averageStats = StreamSupport.stream(bookRepository.findAll().spliterator(), false)
                .map(book -> {
                    final long avgOrdersPerYear = bookOrderRepository.countByBookIdAndDateAfter(book.getId(), sinceTime);

                    final double averageForGenre = StreamSupport.stream(bookOrderRepository.findAll().spliterator(), false)
                            .filter(o -> o.getBook().getGenre().getId().equals(book.getGenre().getId()))
                            .filter(o -> o.getDate().after(sinceTime))
                            .map(o -> o.getBook().getId())
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                            .values().stream()
                            .mapToLong(e -> e)
                            .average()
                            .orElse(0.0);


                    final double avgExceedingOrders = StreamSupport.stream(bookOrderRepository.findAll().spliterator(), false)
                            .filter(o -> o.getBook().getGenre().getId().equals(book.getGenre().getId()))
                            .filter(o -> o.getDate().after(sinceTime))
                            .map(o -> o.getBook().getId())
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                            .values().stream()
                            .mapToLong(e -> e)
                            .filter(e -> e > averageForGenre)
                            .average()
                            .orElse(0.0);

                    return AverageStat.builder()
                            .book(book)
                            .averageOrdersPerYear(avgOrdersPerYear)
                            .averageExceedingOrdersPerYear(avgExceedingOrders)
                            .build();
                })
                .toList();

//        final List<AverageStat> averageStats = bookOrderRepository.getAverageStats();

        final StatsDto stats = StatsDto.builder()
                .average(averageStats)
                .build();

        model.addAttribute("stats", stats);
        return "stats-avg";
    }
}
