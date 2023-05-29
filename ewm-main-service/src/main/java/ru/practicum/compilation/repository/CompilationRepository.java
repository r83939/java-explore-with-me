package ru.practicum.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.Compilation;


import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    //List<Compilation> findAllByPinned1(Boolean pinned, Pageable pageable);


    @Query("SELECT c " +
            "FROM Compilation AS c " +
            "WHERE :pinned IS NULL OR c.pinned = :pinned")
    List<Compilation> findAllByPinned(@Param("pinned") Boolean pinned, Pageable pageable);
}
