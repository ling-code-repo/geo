package com.qianyu.module.geo.component.article;

import com.qianyu.module.geo.utils.GeoUtils;
import com.qianyu.module.infra.dal.dataobject.file.FileDO;
import com.volcengine.ark.runtime.model.completion.chat.*;
import com.volcengine.ark.runtime.service.ArkService;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChatCompletions {

    @Value("${ai.llm.doubao-api-key:''}")
    private String apikey;
    @Autowired
    private BgeEmbeddingModel embeddingModel;


    public String generateArticle(String content, String title, FileDO knowledge, List<String> images) {
        ArkService service = ArkService.builder().apiKey(apikey).baseUrl("https://ark.cn-beijing.volces.com/api/v3").build();
        final List<ChatMessage> messages = new ArrayList<>();
        String userMessage = "1、"+content;
//        final ChatMessage articleMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(content).build();

        String imageArrStr = Arrays.toString(images.toArray(new String[]{}));
        userMessage += "\n2、这里有几张图片：" + imageArrStr + "，待会你在文章合适的位置里插入.";
//        final ChatMessage systemImageMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content().build();
//        messages.add(systemImageMessage);
        File parentFile = null;
        String knowledgeStr = null;
        if (!ObjectUtils.isEmpty(knowledge)) {
            try {
                File tempDirectory = FileUtils
                        .getTempDirectory();
                File file = new File(tempDirectory, "knowledge_" + System.currentTimeMillis()+"/"+knowledge.getName());
                if ((parentFile = file.getParentFile()).exists()){
                    parentFile.mkdirs();
                }
                FileUtils.copyURLToFile(new URL(knowledge.getUrl()), file);
                String canonicalPath = parentFile.getCanonicalPath();
                List<TextSegment> textSegment = embeddingModel.getTextSegment(content, canonicalPath);
                knowledgeStr = textSegment.stream().map(TextSegment::text).collect(Collectors.joining(GeoUtils.COMMA));

            } catch (IOException e) {
                log.error("知识库使用错误！", e);
            }finally {
                FileUtils.deleteQuietly(parentFile);
            }
        }
        int number = 3;
        if (StringUtils.hasText(knowledgeStr)){
            userMessage+= "\n"+number+"、这里有一些可参考的文档：【"+knowledgeStr+"】。你结合这些文档一起生成文章。";
            number++;
        }

        userMessage += "\n"+number+"、文章以markdown的形式输出，文章字数在850~950字之间，不能低于850字，你仔细检查,不能超出字数，也不能少于字数，必须使用图片";
        number++;

//        final ChatMessage systemArticleMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("文章以markdown的形式输出，文章字数在850~950字之间，不能低于850字").build();

//        final ChatMessage systemTitleMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("文章生成以后，结合文章再根据该要求"+title+",生成文章标题，将标题放在文章的最开头").build();
        final ChatMessage articleMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(userMessage).build();
        messages.add(articleMessage);

        final ChatMessage titleMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("文章生成以后，结合文章再根据该要求"+title+",再生成一个文章标题,作为文章标题").build();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("生成文章用<article></article>包起来,生成标题用<title></title>包起来").build();
        messages.add(titleMessage);
        messages.add(systemMessage);




        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("doubao-1-5-pro-32k-250115")//Replace with Model ID
//                .model("deepseek-r1-250528")//Replace with Model ID
                .messages(messages)
                .thinking(new ChatCompletionRequest.ChatCompletionRequestThinking("disabled")) // Manually disable deep thinking
                .build();
        List<ChatCompletionChoice> choices = service.createChatCompletion(chatCompletionRequest).getChoices();
        String article = choices.stream().map(c -> {
            return c.getMessage().getContent().toString();
        }).collect(Collectors.joining("\n"));

        // shutdown service
        service.shutdownExecutor();
        return article;
    }

}