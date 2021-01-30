package druid;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.parser.Token;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class Main02 {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
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

        String sql = "select * from a where `--test` is not null #  这也是一个注释";
//        for (int i = 0; i < originSqlList.size(); i++) {
//            String resultSql = removeComment(originSqlList.get(i));
//            System.out.println("originSql : " + originSqlList.get(i));
//            System.out.println("expectSql : " + expectSqlList.get(i));
//            System.out.println("resultSql : " + resultSql);
//            if (!expectSqlList.get(i).equals(resultSql)) {
//                throw new RuntimeException("不一样");
//            }
//
//        }
        String sql2 = "select count(*) from test_kylin_fact /* this is test*/  where price > 10.0";
        removeComment(sql2);

    }

    private static String removeComment(String sql) throws NoSuchFieldException, IllegalAccessException {
        MySqlLexer mySqlLexer = new MySqlLexer(sql, false, false);

        int startIndex = 0;
        StringBuilder newSql = new StringBuilder();
        for (; ; ) {
            mySqlLexer.nextToken();
            Integer pos = (Integer) ReflectUtil.getValueByFieldName(mySqlLexer, "pos");
            // bufPos
            Integer bufPos = (Integer) ReflectUtil.getValueByFieldName(mySqlLexer, "bufPos");


            if (mySqlLexer.token() == Token.LINE_COMMENT) {
                System.out.println(pos);
                System.out.println(bufPos);
                newSql.append(sql, startIndex, pos - bufPos - 1);
                System.out.println("注释是 ：" + sql.substring(startIndex, pos - bufPos - 1));
                startIndex = pos;
            }
            if (mySqlLexer.token() == Token.MULTI_LINE_COMMENT) {
                System.out.println(pos);
                System.out.println(bufPos);
                System.out.println("注释是 ：" + sql.substring(startIndex, pos - bufPos - 2));
                newSql.append(sql, startIndex, pos  - bufPos);
                startIndex = pos;
            }
            if (mySqlLexer.token() == Token.EOF) {
                if (startIndex == 0) {
                    newSql.append(sql);
                }
                break;
            }
        }
        return newSql.toString();
    }
}
