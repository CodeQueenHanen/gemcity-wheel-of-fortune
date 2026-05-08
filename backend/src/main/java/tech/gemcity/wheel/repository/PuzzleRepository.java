package tech.gemcity.wheel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.gemcity.wheel.model.Puzzle;

import java.util.Optional;

@Repository
public interface PuzzleRepository extends JpaRepository<Puzzle, Long> {

    /**
     * Returns one random puzzle from the table.
     * H2-compatible — uses RAND() which H2 supports.
     */
    @Query(value = "SELECT * FROM puzzle ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Puzzle> findRandom();
}
