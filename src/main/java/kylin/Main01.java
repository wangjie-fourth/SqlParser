package kylin;

import java.util.Arrays;
import java.util.List;

public class Main01 {
    public static String removeCommentInSql(String sql1) {
        // match two patterns, one is "-- comment", the other is "/* comment */"
        final String[] commentPatterns = new String[]{"--(?!.*\\*/).*?[\r\n]", "/\\*(.|\r|\n)*?\\*/"};
        for (int i = 0; i < commentPatterns.length; i++) {
            sql1 = sql1.replaceAll(commentPatterns[i], "");
        }
        sql1 = sql1.trim();
        return sql1;
    }

    public static void main(String[] args) {
        List<String> originSqlList = Arrays.asList(
                "select * from a where price='2012--12-14'",
                "select * from a where price=\"/* this is not comment */\"",
                "SELECT * from a WHERE `--test` is not null"
        );


    }
}
