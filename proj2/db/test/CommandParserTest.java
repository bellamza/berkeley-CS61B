import db.CommandParser;
import db.Database;
import db.Table;
import db.Type;
import javafx.scene.control.Tab;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandParserTest {
    @Test
    public void createTableInsertRowAndDropTable() {
        Database db = new Database();

        String createTableCmd = "create table people (name string, age int, height float)";
        assertEquals("", CommandParser.eval(createTableCmd, db));

        assertEquals("", CommandParser.eval("insert into people values 'James', 19, 1.85", db));
        assertEquals("", CommandParser.eval("insert into people values 'Jide', 29, 1.65", db));
        assertEquals("", CommandParser.eval("insert into people values 'Jin', 34, 1.93", db));

        Table expected = new Table("people");
        expected.addColumn("name", Type.STRING);
        expected.addColumn("age", Type.INT);
        expected.addColumn("height", Type.FLOAT);
        expected.addRow(Arrays.asList("James", "19", "1.85"));
        expected.addRow(Arrays.asList("Jide", "29", "1.65"));
        expected.addRow(Arrays.asList("Jin", "34", "1.93"));

        assertEquals(expected.toString(), CommandParser.eval("print people", db));

        assertEquals("", CommandParser.eval("drop table people", db));
        assertEquals("ERROR: Table people could not be found",
                CommandParser.eval("print people", db));
    }

    @Test
    public void loadTable() {
        Database db = new Database();

        assertEquals("", CommandParser.eval("load t1", db));

        Table expected = new Table("t1");
        expected.addColumn("x", Type.INT);
        expected.addColumn("y", Type.INT);
        expected.addRow(Arrays.asList("2", "5"));
        expected.addRow(Arrays.asList("8", "3"));
        expected.addRow(Arrays.asList("13", "7"));

        assertEquals(expected.toString(), CommandParser.eval("print t1", db));

        expected = new Table("fans");
        expected.addColumn("Lastname", Type.STRING);
        expected.addColumn("Firstname", Type.STRING);
        expected.addColumn("TeamName", Type.STRING);
        expected.addRow(Arrays.asList("Lee", "Maurice", "Mets"));
        expected.addRow(Arrays.asList("Lee", "Maurice", "Steelers"));
        expected.addRow(Arrays.asList("Ray", "Mitas", "Patriots"));
        expected.addRow(Arrays.asList("Hwang", "Alex", "Cloud9"));
        expected.addRow(Arrays.asList("Rulison", "Jared", "EnVyUs"));
        expected.addRow(Arrays.asList("Fang", "Vivian", "Golden Bears"));


        assertEquals("", CommandParser.eval("load fans", db));
        assertEquals(expected.toString(), CommandParser.eval("print fans", db));
    }

    @Test
    public void storeTable() {
        Database db = new Database();

        CommandParser.eval("create table people (name string, age int, height float)", db);
        CommandParser.eval("insert into people values 'James', 19, 1.85", db);
        CommandParser.eval("insert into people values 'Jide', 29, 1.65", db);
        CommandParser.eval("insert into people values 'Jin', 34, 1.93", db);

        assertEquals("", CommandParser.eval("store people", db));
        CommandParser.eval("drop table people", db);
        assertEquals("", CommandParser.eval("load people", db));

        Table expected = new Table("people");
        expected.addColumn("name", Type.STRING);
        expected.addColumn("age", Type.INT);
        expected.addColumn("height", Type.FLOAT);
        expected.addRow(Arrays.asList("James", "19", "1.85"));
        expected.addRow(Arrays.asList("Jide", "29", "1.65"));
        expected.addRow(Arrays.asList("Jin", "34", "1.93"));

        assertEquals(expected.toString(), CommandParser.eval("print people", db));

        File tblFile = new File("people.tbl");

        if (tblFile.exists()) {
            assertTrue(tblFile.delete());
        }
    }

    @Test
    public void select() {
        Database db = new Database();

        CommandParser.eval("load fans", db);
        CommandParser.eval("load teams", db);
        CommandParser.eval("load records", db);

        String selectCmd = "select Firstname,Lastname,TeamName from fans where Lastname >= 'Lee'";
        Table expected = new Table("");
        expected.addColumn("Firstname", Type.STRING);
        expected.addColumn("Lastname", Type.STRING);
        expected.addColumn("TeamName", Type.STRING);
        expected.addRow(Arrays.asList("Maurice","Lee","Mets"));
        expected.addRow(Arrays.asList("Maurice","Lee","Steelers"));
        expected.addRow(Arrays.asList("Mitas","Ray","Patriots"));
        expected.addRow(Arrays.asList("Jared","Rulison","EnVyUs"));

        assertEquals(expected.toString(), CommandParser.eval(selectCmd, db));

        selectCmd = "select City,Season,Wins / Losses as Ratio from teams,records";
        expected = new Table("");
        expected.addColumn("City", Type.STRING);
        expected.addColumn("Season", Type.INT);
        expected.addColumn("Ratio", Type.INT);
        expected.addRow(Arrays.asList("New York", "2015", "1"));
        expected.addRow(Arrays.asList("New York", "2014", "0"));
        expected.addRow(Arrays.asList("New York", "2013", "0"));
        expected.addRow(Arrays.asList("Pittsburgh", "2015", "1"));
        expected.addRow(Arrays.asList("Pittsburgh", "2014", "2"));
        expected.addRow(Arrays.asList("Pittsburgh", "2013", "1"));
        expected.addRow(Arrays.asList("New England", "2015", "3"));
        expected.addRow(Arrays.asList("New England", "2014", "3"));
        expected.addRow(Arrays.asList("New England", "2013", "3"));
        expected.addRow(Arrays.asList("Berkeley", "2016", "0"));
        expected.addRow(Arrays.asList("Berkeley", "2015", "1"));
        expected.addRow(Arrays.asList("Berkeley", "2014", "0"));

        assertEquals(expected.toString(), CommandParser.eval(selectCmd, db));
    }

    @Test
    public void createSelectedTable() {
        Database db = new Database();

        CommandParser.eval("load teams", db);
        CommandParser.eval("load records", db);

        String cmd = "create table seasonRatios "
                + "as select City,Season,Wins / Losses as Ratio from teams,records";
        assertEquals("", CommandParser.eval(cmd, db));

        Table expected = new Table("");
        expected.addColumn("City", Type.STRING);
        expected.addColumn("Season", Type.INT);
        expected.addColumn("Ratio", Type.INT);
        expected.addRow(Arrays.asList("New York", "2015", "1"));
        expected.addRow(Arrays.asList("New York", "2014", "0"));
        expected.addRow(Arrays.asList("New York", "2013", "0"));
        expected.addRow(Arrays.asList("Pittsburgh", "2015", "1"));
        expected.addRow(Arrays.asList("Pittsburgh", "2014", "2"));
        expected.addRow(Arrays.asList("Pittsburgh", "2013", "1"));
        expected.addRow(Arrays.asList("New England", "2015", "3"));
        expected.addRow(Arrays.asList("New England", "2014", "3"));
        expected.addRow(Arrays.asList("New England", "2013", "3"));
        expected.addRow(Arrays.asList("Berkeley", "2016", "0"));
        expected.addRow(Arrays.asList("Berkeley", "2015", "1"));
        expected.addRow(Arrays.asList("Berkeley", "2014", "0"));

        assertEquals(expected.toString(), CommandParser.eval("print seasonRatios", db));
    }
}
