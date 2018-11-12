package ch.pmalek.filedb.model;

import ch.pmalek.filedb.engine.DBUtils;
import ch.pmalek.filedb.engine.TableLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class DatabaseImpl implements Database {

    private DBUtils dbUtils;

    private Map<String, AtomicInteger> sequencesMap = new ConcurrentHashMap<>();
    private Map<String, TableLock> tableLocks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Map<String, Integer> tablesWithMaxId = dbUtils.listTables();

        for(Map.Entry<String, Integer> entry : tablesWithMaxId.entrySet()){
            String tableName = entry.getKey();
            Integer maxId = entry.getValue();

            log.info("Initializing table: {} with maxId: {}", tableName, maxId);
            sequencesMap.put(tableName, new AtomicInteger(maxId));
            tableLocks.put(tableName, new TableLock());
        }
    }

    @Autowired
    public DatabaseImpl(DBUtils dbUtils){
        this.dbUtils = dbUtils;
    }

    // returns the id of the newly inserted row or throws an exception in case of error
    // once an id is returned, the data is guaranteed to be retrievable via subsequent select calls
    public int insert(String tableName, List<String> values) {

        // if that is a new table create sequence for it
        if(!tableLocks.containsKey(tableName)){
            tableLocks.put(tableName, new TableLock());
            sequencesMap.put(tableName, new AtomicInteger(0));
        }
        TableLock tl = tableLocks.get(tableName);
        int newId = -1;

        try {
            tl.acquireWriteLock();
            newId = sequencesMap.get(tableName).incrementAndGet();
            dbUtils.insert(tableName, newId, values);
        } catch(Exception e){
            throw new RuntimeException("Error during insert", e);
        } finally {
            tl.releaseWriteLock();
        }

        return newId;
    }

    // updates the data associated with the list
    // returns true if the data has changed, or throws an exception in case of error
    public boolean update(String tableName, List<String> values, int id){

        // if table does not exist -> error
        if(!tableLocks.containsKey(tableName)){
            throw new RuntimeException("No such table: " + tableName);
        }

        TableLock tl = tableLocks.get(tableName);

        try {
            tl.acquireWriteLock();
            dbUtils.update(tableName, id, values);
        } catch(Exception e){
            throw new RuntimeException("Error during update", e);
        } finally {
            tl.releaseWriteLock();
        }

        return true;
    }

    // retrieves the data associated to the id (as previously stored/updated),
    // or throws an exception in case of error or missing data
    public List<String> select(String tableName, int id) {

        // if table does not exist -> error
        if(!tableLocks.containsKey(tableName)){
            throw new RuntimeException("No such table: " + tableName);
        }

        TableLock tl = tableLocks.get(tableName);
        List<String> values = null;

        try {
            tl.acquireReadLock();
            values = dbUtils.select(tableName, id);
        } catch(Exception e){
            throw new RuntimeException("Error during select", e);
        } finally {
            tl.releaseReadLock();
        }

        return values;
    }

}
