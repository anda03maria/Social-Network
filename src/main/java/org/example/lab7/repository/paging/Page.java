package org.example.lab7.repository.paging;
import java.util.stream.Stream;

public interface Page<E> {
    Pageable getPageable();

    Pageable nextPageable();

    Pageable previousPageable();

    Stream<E> getContent();


}
