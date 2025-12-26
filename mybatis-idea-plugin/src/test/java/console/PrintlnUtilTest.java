package console;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.plugins.mybaitslog.Config;
import com.plugins.mybaitslog.console.PrintlnUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * 使用 BasePlatformTestCase 以初始化 IntelliJ 测试环境
 */
@RunWith(JUnit4.class)
public class PrintlnUtilTest extends BasePlatformTestCase {

    @Test
    public void testRestoreSqlFromText_Standard() {
        String log = "2023-01-01 12:00:00 DEBUG - Preparing: SELECT * FROM user WHERE id = ? AND name = ?\n" +
                "2023-01-01 12:00:00 DEBUG - Parameters: 1(Integer), test(String)";

        String sql = PrintlnUtil.restoreSqlFromText(log);
        Assert.assertNotNull(sql);
    }

    @Test
    public void testRestoreSqlFromText_NoType() {
        String log = "Preparing: INSERT INTO user (id, name) VALUES (?, ?)\n" +
                "Parameters: 1, test";

        String sql = PrintlnUtil.restoreSqlFromText(log);
        Assert.assertEquals("INSERT INTO user (id, name) VALUES (1, test)", sql);
    }

    @Test
    public void testRestoreSqlFromText_WithNull() {
        String log = "Preparing: UPDATE user SET name = ? WHERE id = ?\n" +
                "Parameters: null, 1(Integer)";

        String sql = PrintlnUtil.restoreSqlFromText(log);
        Assert.assertEquals("UPDATE user SET name = null WHERE id = 1", sql);
    }

    @Test
    public void testRestoreSqlFromText_JsonFormat() {
        String json = "{\"id\":\"1\",\"originalSql\":\"select * from t\",\"completeSql\":\"select * from t\",\"parameter\":\"\"}";
        String log = Config.Idea.getParameters() + json;

        String sql = PrintlnUtil.restoreSqlFromText(log);
        Assert.assertEquals("select * from t", sql);
    }

    @Test
    public void testRestoreSqlFromText_Invalid() {
        String log = "Just some random text";
        String sql = PrintlnUtil.restoreSqlFromText(log);
        Assert.assertNull(sql);
    }
}

