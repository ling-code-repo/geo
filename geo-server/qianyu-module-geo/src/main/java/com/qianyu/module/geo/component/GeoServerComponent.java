package com.qianyu.module.geo.component;


import com.qianyu.module.geo.controller.admin.word.vo.GeoWordDistillReqVO;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class GeoServerComponent {

    @Value("${ai.llm.doubao-api-key:''}")
    private String doubaoApiKey;



    /**
     * 基于AI的智能蒸馏词生成
     */
    public List<String> distillationWord(GeoWordDistillReqVO vo) {
        if (!StringUtils.hasText(doubaoApiKey)) {
            throw new RuntimeException("请配置豆包模型API密钥！");
        }
        return distillWordByDoubao(vo);
    }

    /**
     * 使用豆包模型生成蒸馏词
     */
    private List<String> distillWordByDoubao(GeoWordDistillReqVO vo) {
        ArkService service = null;
        try {
            service = ArkService.builder()
                    .apiKey(doubaoApiKey)
                    .baseUrl("https://ark.cn-beijing.volces.com/api/v3")
                    .build();

            List<ChatMessage> messages = new ArrayList<>();

            // 构建提示词
            String prompt = buildPrompt(vo.getWord(), vo.getTarget());

            ChatMessage userMessage = ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(prompt)
                    .build();
            messages.add(userMessage);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model("doubao-1-5-pro-32k-250115")
                    .messages(messages)
                    .temperature(0.7)  // 添加一些随机性，但不要太高
                    .maxTokens(2000)
                    .build();

            String response = service.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0)
                    .getMessage()
                    .getContent()
                    .toString();

            // 解析AI返回的问题列表
            return parseAIResponse(response);

        } catch (Exception e) {
            log.error("豆包模型生成蒸馏词失败", e);
            throw new RuntimeException(e);
        } finally {
            if (service != null) {
                service.shutdownExecutor();
            }
        }
    }

    /**
     * 构建AI提示词
     */
    private String buildPrompt(String word, String target) {
        return String.format(
                "你是一个专业的SEO关键词优化专家。请根据以下信息生成50个长尾问题：\n" +
                        "1. 主词：%s\n" +
                        "2. 转化目标：%s\n" +
                        "\n" +
                        "要求：\n" +
                        "- 生成的问题要围绕主词\"%s\"展开\n" +
                        "- 问题要能引导用户实现\"%s\"这个转化目标\n" +
                        "- 问题要符合用户搜索习惯，自然流畅\n" +
                        "- 每个问题都要包含主词\"%s\"\n" +
                        "- 问题类型多样化（如何、哪家、好不好、多少钱、哪里等）\n" +
                        "- 直接输出问题列表，每行一个问题，不要编号\n" +
                        "- 不要包含任何解释或其他文字\n\n" +
                        "请生成50个问题：",
                word, target, word, target, word
        );
    }

    /**
     * 解析AI返回的问题列表
     */
    private List<String> parseAIResponse(String response) {
        List<String> questions = new ArrayList<>();
        String[] lines = response.split("\n");

        for (String line : lines) {
            String question = line.trim();
            // 移除可能的编号前缀（如"1. "、"1、"等）
            question = question.replaceAll("^\\d+[.、]\\s*", "");
            // 移除引号
            question = question.replaceAll("^[\"'|«»「」『』【】]", "");
            question = question.replaceAll("[\"'|«»「」『』【】]$", "");

            if (StringUtils.hasText(question) && question.length() > 3) {
                questions.add(question);
            }
        }

        // 限制返回数量
        return questions.size() > 50 ? questions.subList(0, 50) : questions;
    }
}
