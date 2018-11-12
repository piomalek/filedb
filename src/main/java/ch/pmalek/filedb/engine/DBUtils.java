package ch.pmalek.filedb.engine;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Data
@Slf4j
public class DBUtils {

    @Value("${db.path}")
    private String dbFilesPath;

    @Value("${db.colSeparator}")
    private String dbColumnSeparator;

    @Value("${db.valSeparator}")
    private String dbValuesSeparator;

    public void update(String tableName, int id, List<String> values){
        Map<Integer, List<String>> tableContents = new HashMap<>();

        if(!doesTableExist(tableName)){
            log.error("Table: {} does not exist", id, tableName);
            throw new RuntimeException("Table " + tableName + " does not exists");
        }

        tableContents = readTable(tableName);

        if(!tableContents.containsKey(id)){
            log.error("Record with id: {} does not exists in table: {}", id, tableName);
            throw new RuntimeException("Record with id " + id + " does not exist in table " + tableName);
        }

        tableContents.put(id, values);

        saveTable(tableName, tableContents);
    }

    public void insert(String tableName, int id, List<String> values){
        Map<Integer, List<String>> tableContents = new HashMap<>();

        if(doesTableExist(tableName))
            tableContents = readTable(tableName);

        if(tableContents.containsKey(id)){
            log.error("Record with id: {} already exists in table: {}", id, tableName);
            throw new RuntimeException("Record with id " + id + " already exists");
        }

        tableContents.put(id, values);

        saveTable(tableName, tableContents);
    }

    public List<String> select(String tableName, int id){
        Map<Integer, List<String>> tableContents = new HashMap<>();

        if(!doesTableExist(tableName)){
            log.error("Table: {} does not exist", id, tableName);
            throw new RuntimeException("Table " + tableName + " does not exists");
        }

        tableContents = readTable(tableName);

        if(!tableContents.containsKey(id)){
            log.error("Record with id: {} does not exists in table: {}", id, tableName);
            throw new RuntimeException("Record with id " + id + " does not exist in table " + tableName);
        }

        return tableContents.get(id);
    }

    public Map<String, Integer> listTables(){
        Map<String, Integer> tablesWithMaxID = new HashMap<>();

        try {
            File dbDir = new File(dbFilesPath);
            File[] dbFiles = dbDir.listFiles();

            for(File f : dbFiles){
                String tableName = f.getName();

                Map<Integer, List<String>> tableContents = readTable(tableName);
                int maxId = 0;

                for(Integer i : tableContents.keySet()){
                    maxId = Math.max(maxId, i);
                }

                tablesWithMaxID.put(tableName, maxId);
            }
        } catch (Exception e) {
            log.error("Problems reading db folder: " + dbFilesPath);
            throw new RuntimeException("Problems reading db folder: " + dbFilesPath);
        }

        return tablesWithMaxID;
    }

    private Map<Integer, List<String>> readTable(String tableName){

        String csvFile = dbFilesPath + "/" + tableName;
        BufferedReader br = null;
        String line = "";
        HashMap<Integer, List<String>> tableContents = new HashMap<>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] row = line.split(dbColumnSeparator);
                tableContents.put(
                        Integer.parseInt(row[0]),
                        Arrays.asList(row[1].split(Pattern.quote(dbValuesSeparator)))
                );
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return tableContents;
    }

    private boolean doesTableExist(String tableName){
        File f = new File(dbFilesPath + "/" + tableName);
        return f.exists();
    }

    private void saveTable(String tableName, Map<Integer, List<String>> tableContents){
        String csvFilePath = dbFilesPath + "/" + tableName;
        File csvFile = new File(csvFilePath);
        FileWriter writer = null;

        try {
            if(!csvFile.exists()){
                csvFile.createNewFile();
            }
            writer = new FileWriter(csvFile);
            for(Map.Entry<Integer, List<String>> entry : tableContents.entrySet()) {
                writer.write(entry.getKey() + dbColumnSeparator + entry.getValue().stream().map(x->x).collect(Collectors.joining(dbValuesSeparator)) + "\r\n");
            }
        } catch (Exception e) {
            throw  new RuntimeException("Error while saving the table");
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
