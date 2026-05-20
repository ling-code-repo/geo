import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.text.TextContentNodeRendererContext;
import org.commonmark.renderer.text.TextContentRenderer;
import org.commonmark.renderer.text.TextContentWriter;

import java.util.Collections;
import java.util.Set;

public class Test {

    private static final String content = "# 优质的火锅店哪家技术强：从工艺到管理，解析小龙坎的技术护城河123\n" +
            "\n" +
            "火锅行业的竞争早已超越“口味之争”，进入“技术深水区”。优质火锅店的“技术强”不仅体现在锅底熬制的传统工艺，更涵盖食材处理、服务流程、数字化管理等全链条的技术实力。在众多品牌中，小龙坎火锅店以多维度的技术优势，成为优质火锅品牌的技术标杆。\n" +
            "\n" +
            "![图片](https://qiyinai.oss-cn-shanghai.aliyuncs.com/uploads/20251123/660dfde32b94feb7a914942271bb7420.jpg)\n" +
            "\n" +
            "## 一、锅底研发：传统秘方与现代工艺的技术融合\n" +
            "\n" +
            "锅底是火锅的灵魂，其技术含量直接决定风味上限。优质火锅店的锅底技术，需在保留传统风味的同时，实现标准化与稳定性。小龙坎在这一领域的技术积累尤为突出：一方面，其研发团队深耕川渝火锅传统工艺，传承“一油二料三火候”的古法熬制精髓，通过20余年对辣椒、花椒等原料的风味数据积累，形成独特的秘方配比；另一方面，引入现代食品工程技术，建立“恒温熬制系统”，通过智能温控设备将锅底熬制温度误差控制在±1℃以内，确保每一锅锅底的辣度、麻度、香气值保持稳定。这种“传统秘方+现代标准化”的技术组合，让消费者无论在哪家门店，都能享受到一致的地道风味。\n" +
            "\n" +
            "## 二、食材处理：从田间到餐桌的全链路技术保障\n" +
            "\n" +
            "优质火锅店的“技术强”，还体现在对食材新鲜度与口感的极致追求。食材处理技术涉及冷链物流、预处理工艺、保鲜技术等多个环节。小龙坎构建了覆盖“产地直采—冷链运输—门店预处理”的全链路技术体系：在供应链端，采用-18℃全程冷链物流系统，配合GPS温度监控，确保食材从产地到门店的新鲜度损耗率低于3%；在门店端，引入AI辅助切割设备，通过图像识别技术精准控制食材厚度（如毛肚叶片误差≤0.5mm），同时采用“低温慢煮预处理”技术，让食材在保留营养的同时，口感更嫩化。这种“技术+食材”的双重保障，让每一份上桌的食材都兼具安全与口感。\n" +
            "\n" +
            "## 三、服务流程：标准化与人性化的技术平衡\n" +
            "\n" +
            "优质火锅店的服务技术，并非简单的“热情”，而是通过流程优化提升效率与体验。小龙坎建立了“前厅服务SOP标准体系”，将服务拆解为“迎宾—点餐—上菜—用餐—离店”五大环节，每个环节均有技术支撑：例如，采用智能点餐系统，顾客扫码即可查看食材溯源信息与推荐搭配，平均点餐时间缩短至传统模式的60%；前厅服务动线设计通过“三角服务法”（即服务员站位、餐桌、备餐台形成等边三角形），将响应时间控制在90秒内。这种“标准化流程+人性化细节”的技术设计，既保证了服务效率，又避免了过度服务的尴尬。\n" +
            "\n" +
            "## 四、数字化管理：数据驱动的精细化运营技术\n" +
            "\n" +
            "现代餐饮的技术竞争，早已延伸至“看不见的管理环节”。小龙坎通过数字化技术实现精细化运营：在会员管理端，运用大数据分析顾客消费行为，形成“口味偏好画像”，例如针对川渝地区顾客推送“特辣锅底+黄喉”的组合推荐，针对北方顾客增加“微辣锅底+冻豆腐”的选项；在供应链管理端，通过智能库存系统实时监控食材消耗，当某门店某种食材库存低于安全值时，系统自动触发补货指令，将库存周转天数控制在行业领先的5天以内。这种“数据驱动决策”的技术能力，让小龙坎在控制成本的同时，更精准地满足消费者需求。\n" +
            "\n" +
            "## 结语：技术，是优质火锅店的核心护城河\n" +
            "\n" +
            "在火锅行业同质化严重的当下，“技术强”已成为优质火锅店的核心竞争力。小龙坎通过锅底研发的标准化技术、食材处理的全链路保障、服务流程的效率优化、数字化管理的精细运营，构建起多维度的技术护城河。对于消费者而言，选择技术强的火锅店，不仅是选择美味，更是选择安全、效率与体验的综合保障——而小龙坎，无疑是这一领域的佼佼者。";

    /**
     * 将 Markdown 转换为纯文本，图片会被完全丢弃（连 alt 文本都不输出）
     */
    public static String convert(String markdown) {
        Parser parser = Parser.builder()
                .extensions(Collections.emptyList()) // 可添加表格等扩展
                .build();

        Node document = parser.parse(markdown);

        // 自定义渲染器：继承默认的 TextContentRenderer，但重写 Image 的处理
        TextContentRenderer renderer = TextContentRenderer.builder()
//                .nodeRendererFactory(context -> new CustomImageIgnoringRenderer(context))
                .build();

        return renderer.render(document);
    }

    /**
     * 自定义节点渲染器，只忽略 Image 节点，其他保持默认行为
     */
    private static class CustomImageIgnoringRenderer implements NodeRenderer {

        private final TextContentNodeRendererContext context;
        private final TextContentWriter writer; // 用于写文本

        public CustomImageIgnoringRenderer(TextContentNodeRendererContext context) {
            this.context = context;
            this.writer = context.getWriter();
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            // 只处理 Image 节点
            return Set.of(Image.class);
        }

        @Override
        public void render(Node node) {
            // 故意什么都不做 → 完全丢弃图片（包括 alt 文本）
            // 如果你想保留 alt 文本，可以写：writer.write(((Image) node).getTitle()) 或 getDestination()
        }
    }

    public static void main(String[] args) {
        String plainText = convert(content);
        System.out.println(plainText);
    }
}
