import com.qianyu.module.geo.component.GeoServerComponent;
import com.qianyu.module.geo.controller.admin.word.vo.GeoWordDistillReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * 蒸馏词AI模型测试（支持豆包和阿里云）
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AliyunAITest {

    @Autowired
    private GeoServerComponent geoServerComponent;

    @Test
    public void testDistillWord() {
        GeoWordDistillReqVO vo = new GeoWordDistillReqVO();
        vo.setWord("软件开发");
        vo.setTarget("获取企业客户");

        List<String> questions = geoServerComponent.distillationWord(vo);

        System.out.println("=== 生成的问题列表 ===");
        for (int i = 0; i < Math.min(10, questions.size()); i++) {
            System.out.println((i + 1) + ". " + questions.get(i));
        }

        System.out.println("总共生成: " + questions.size() + " 个问题");
    }

    @Test
    public void testDifferentWords() {
        String[][] testCases = {
                {"网站建设", "获取中小企业客户"},
                {"APP开发", "提高转化率"},
                {"小程序制作", "获取本地商家客户"}
        };

        for (String[] testCase : testCases) {
            GeoWordDistillReqVO vo = new GeoWordDistillReqVO();
            vo.setWord(testCase[0]);
            vo.setTarget(testCase[1]);

            System.out.println("\n=== 主词: " + testCase[0] + ", 转化目标: " + testCase[1] + " ===");
            List<String> questions = geoServerComponent.distillationWord(vo);

            if (questions.isEmpty()) {
                System.out.println("❌ 生成失败，请检查API配置");
            } else {
                System.out.println("✅ 成功生成 " + questions.size() + " 个问题");
                System.out.println("示例问题:");
                for (int i = 0; i < Math.min(3, questions.size()); i++) {
                    System.out.println("  - " + questions.get(i));
                }
            }
        }
    }
}