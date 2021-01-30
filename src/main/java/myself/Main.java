package myself;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> originSqlList = Arrays.asList(
                "select count(*) from test_kylin_fact WHERE column_name = '--this is not comment'\n " +
                        "LIMIT 100 offset 0",
                "select count(*) from test_kylin_fact WHERE column_name = '/*--this is not comment*/'",
                "select count(*) from test_kylin_fact /* this is test*/  where price > 10.0 -- comment \n" +
                        ";" + "insert into test_kylin_fact(id) values(?); -- comment \n",
                "select * from test_kylin_fact where price='2012--12-14'",
                "select * from test_kylin_fact where price=\"/* this is not comment */\"",
                "SELECT * from App WHERE `--test` is not null"
        );
        List<String> expectSqlList = Arrays.asList(
                "select count(*) from test_kylin_fact WHERE column_name = '--this is not comment'\n" +
                        " LIMIT 100 offset 0",
                "select count(*) from test_kylin_fact WHERE column_name = '/*--this is not comment*/'",
                "select count(*) from test_kylin_fact   where price > 10.0 \n" +
                        ";insert into test_kylin_fact(id) values(?);\n",
                "select * from test_kylin_fact where price='2012--12-14'",
                "select * from test_kylin_fact where price=\"/* this is not comment */\"",
                "SELECT * from App WHERE `--test` is not null"
        );

        originSqlList.forEach(i -> {
            String newSql = new SqlCommentParser(i).removeCommentSql();
            System.out.println(newSql);
        });
    }
}
