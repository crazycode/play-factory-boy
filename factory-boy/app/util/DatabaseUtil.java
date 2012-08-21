package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import play.Logger;
import play.db.DB;
import play.db.DBPlugin;
import play.exceptions.DatabaseException;

public class DatabaseUtil {

    public static void disableForeignKeyConstraints() {
        if (DBPlugin.url.startsWith("jdbc:oracle:")) {
            DB.execute("begin\n"
                    + "for i in (select constraint_name, table_name from user_constraints where constraint_type ='R'\n"
                    + "and status = 'ENABLED') LOOP\n"
                    + "execute immediate 'alter table '||i.table_name||' disable constraint '||i.constraint_name||'';\n"
                    + "end loop;\n"
                    + "end;"
            );
            return;
        }

        if (DBPlugin.url.startsWith("jdbc:hsqldb:")) {
            DB.execute("SET REFERENTIAL_INTEGRITY FALSE");
            return;
        }

        if (DBPlugin.url.startsWith("jdbc:h2:")) {
            DB.execute("SET REFERENTIAL_INTEGRITY FALSE");
            return;
        }

        if (DBPlugin.url.startsWith("jdbc:mysql:")) {
            DB.execute("SET foreign_key_checks = 0;");
            return;
        }

        if (DBPlugin.url.startsWith("jdbc:postgresql:")) {
            DB.execute("SET CONSTRAINTS ALL DEFERRED");
            return;
        }

        if (DBPlugin.url.startsWith("jdbc:sqlserver:")) {
            Statement exec=null;

            try {
                List<String> names = new ArrayList<String>();
                Connection connection = DB.getConnection();

                ResultSet rs = connection.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                while (rs.next()) {
                    String name = rs.getString("TABLE_NAME");
                    names.add(name);
                }

                    // Then we disable all foreign keys
                exec = connection.createStatement();
                for (String tableName:names)
                    exec.addBatch("ALTER TABLE " + tableName+" NOCHECK CONSTRAINT ALL");
                exec.executeBatch();
                exec.close();

                return;
            } catch (SQLException ex) {
                throw new DatabaseException("Error while disabling foreign keys", ex);
            }
        }

        // Maybe Log a WARN for unsupported DB ?
        Logger.warn("Fixtures : unable to disable constraints, unsupported database : " + DBPlugin.url);
    }

    public static void enableForeignKeyConstraints() {
        if (DBPlugin.url.startsWith("jdbc:oracle:")) {
            DB.execute("begin\n"
                    + "for i in (select constraint_name, table_name from user_constraints where constraint_type ='R'\n"
                    + "and status = 'DISABLED') LOOP\n"
                    + "execute immediate 'alter table '||i.table_name||' enable constraint '||i.constraint_name||'';\n"
                    + "end loop;\n"
                    + "end;"
            );
            return;
        }

        if (DBPlugin.url.startsWith("jdbc:hsqldb:")) {
            DB.execute("SET REFERENTIAL_INTEGRITY TRUE");
            return;
        }

        if (DBPlugin.url.startsWith("jdbc:h2:")) {
            DB.execute("SET REFERENTIAL_INTEGRITY TRUE");
            return;
        }

        if (DBPlugin.url.startsWith("jdbc:mysql:")) {
            DB.execute("SET foreign_key_checks = 1;");
            return;
        }

        if (DBPlugin.url.startsWith("jdbc:postgresql:")) {
            return;
        }

        if (DBPlugin.url.startsWith("jdbc:sqlserver:")) {
           Connection connect = null;
            Statement exec=null;
            try {
                connect = DB.getConnection();
                // We must first drop all foreign keys
                ArrayList<String> checkFKCommands=new ArrayList<String>();
                exec=connect.createStatement();
                ResultSet rs=exec.executeQuery("SELECT 'ALTER TABLE ' + TABLE_SCHEMA + '.[' + TABLE_NAME +'] WITH CHECK CHECK CONSTRAINT [' + CONSTRAINT_NAME + ']' FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE CONSTRAINT_TYPE = 'FOREIGN KEY'");
                while (rs.next())
                {
                    checkFKCommands.add(rs.getString(1));
                }
                exec.close();
                exec=null;

                 // Now we have the drop commands, let's execute them
                exec=connect.createStatement();
                for (String sql:checkFKCommands)
                    exec.addBatch(sql);
                exec.executeBatch();
                exec.close();
            } catch (SQLException ex) {
                throw new DatabaseException("Cannot enable foreign keys", ex);
            }
            return;
          }

        Logger.warn("Fixtures : unable to enable constraints, unsupported database : " + DBPlugin.url);
    }

}
