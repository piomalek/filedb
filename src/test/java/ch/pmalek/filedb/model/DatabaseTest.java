package ch.pmalek.filedb.model;

import ch.pmalek.filedb.engine.DBUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class DatabaseTest {

    @Mock
    private DBUtils dbUtils;

    private Database db;

    @Before
    public void init(){
        db =  new DatabaseImpl(dbUtils);
        doNothing().when(dbUtils).insert(any(), anyInt(), anyList());
    }

    @Test
    public void testInsert() {
        int id = db.insert("asd", Arrays.asList("1","2","3"));
        assertEquals(id, 1);

        id = db.insert("asd", Arrays.asList("2","3","4"));
        assertEquals(id, 2);

        id = db.insert("other", Arrays.asList("2","3","4"));
        assertEquals(id, 1);
    }

}
