package ch.pmalek.filedb.web;

import ch.pmalek.filedb.model.Database;
import ch.pmalek.filedb.dto.Result;
import ch.pmalek.filedb.dto.Row;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/")
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DbController {

    private final Database database;

    @GetMapping("{table}/{id}")
    public ResponseEntity<Row> select(@PathVariable("table") String table, @PathVariable("id") int id){
        log.info("invoke select from {} where id = {}", table, id);
        List<String> values = database.select(table.toLowerCase(), id);

        return ResponseEntity.ok(Row.builder().id(id).values(values).build());
    }

    @PutMapping("{table}/{id}")
    public ResponseEntity<Result> update(@PathVariable("table") String table, @PathVariable("id") int id, @Valid @RequestBody Row row){
        log.info("invoke update {} set id = {}, values = {}", table, row.getId(), row.getValues());

        boolean isUpdated = database.update(table.toLowerCase(), row.getValues(), row.getId());

        return ResponseEntity.ok(Result.builder().statusCode("OK").message("Row updated").build());
    }

    @PostMapping("{table}")
    public ResponseEntity<Row> insert(@PathVariable("table") String table, @Valid @RequestBody Row row){
        log.info("invoke insert to {} values id = {}", row.getValues());

        int id = database.insert(table.toLowerCase(), row.getValues());
        row.setId(id);

        return ResponseEntity.ok(row);
    }
}
