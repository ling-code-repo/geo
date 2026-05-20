你看下哪里可以完善一下？比如蒸馏词，我就没写好。

● 让我先看看你当前的实现，特别是蒸馏词模块和其他核心功能，然后给出改进建议。

● 2 Explore agents finished (ctrl+o to expand)                                                                                                                                                           
├─ 探索蒸馏词模块实现 · 24 tool uses · 38.4k tokens
│  ⎿  Done                                                                                                                                                                                            
└─ 探索其他核心模块 · 50 tool uses · 62.7k tokens
⎿  Done

● 根据代码分析，我给你几个关键改进方向：

  ---
1. 蒸馏词模块 - 核心问题

当前实现的问题

你的蒸馏词只是一个简单的"主词+问题列表"结构，缺少 GEO 的核心逻辑：

-- 当前表结构太简单                                                                                                                                                                                    
geo_word: 主词、转化目标、是否优化、是否拓展
geo_question: 问题列表

建议改造

-- 1. 增加关键词类型和来源                                                                                                                                                                             
ALTER TABLE geo_word ADD COLUMN keyword_type TINYINT DEFAULT 1 COMMENT '关键词类型: 1-品牌词 2-产品词 3-问题词 4-长尾词 5-竞品词';
ALTER TABLE geo_word ADD COLUMN source TINYINT DEFAULT 1 COMMENT '来源: 1-手动录入 2-AI挖掘 3-导入 4-竞争对手分析';

-- 2. 增加搜索量和竞争度                                                                                                                                                                               
ALTER TABLE geo_word ADD COLUMN search_volume INT DEFAULT 0 COMMENT '月搜索量';
ALTER TABLE geo_word ADD COLUMN competition DECIMAL(3,2) DEFAULT 0.00 COMMENT '竞争度 0-1';
ALTER TABLE geo_word ADD COLUMN cpc DECIMAL(10,2) DEFAULT 0.00 COMMENT '竞价价格';
ALTER TABLE geo_word ADD COLUMN difficulty TINYINT DEFAULT 0 COMMENT '优化难度 1-5';

-- 3. 增加效果追踪                                                                                                                                                                                     
ALTER TABLE geo_word ADD COLUMN refer_count INT DEFAULT 0 COMMENT '被AI引用次数';
ALTER TABLE geo_word ADD COLUMN last_refer_time DATETIME COMMENT '最后被引用时间';
ALTER TABLE geo_word ADD COLUMN conversion_rate DECIMAL(3,2) DEFAULT 0.00 COMMENT '转化率';

-- 4. 增加同义词和变体                                                                                                                                                                                 
CREATE TABLE geo_word_synonym (
id BIGINT PRIMARY KEY,
word_id BIGINT NOT NULL COMMENT '主词ID',
synonym VARCHAR(128) NOT NULL COMMENT '同义词',
type TINYINT DEFAULT 1 COMMENT '类型: 1-同义 2-近义 3-变体',
INDEX idx_word_id (word_id)
);

-- 5. 增加用户意图标签                                                                                                                                                                                 
ALTER TABLE geo_word ADD COLUMN user_intent VARCHAR(32) COMMENT '用户意图: informational-信息型 navigational-导航型 transactional-交易型 commercial-商业型';

功能增强

// 1. 关键词挖掘功能                                                                                                                                                                                   
public interface KeywordMiningService {
// 基于AI生成相关词                                                                                                                                                                                
List<KeywordDTO> mineByAI(String seedKeyword, int limit);

      // 基于搜索引擎自动补全                                                                                                                                                                            
      List<String> getAutoCompleteSuggestions(String keyword);

      // 基于"人们也在问"扩展                                                                                                                                                                            
      List<String> getRelatedQuestions(String keyword);

      // 竞争对手关键词分析                                                                                                                                                                              
      List<KeywordDTO> analyzeCompetitorKeywords(String url);
}

// 2. 关键词评分系统                                                                                                                                                                                   
public interface KeywordScoringService {
// 计算关键词优先级分数                                                                                                                                                                            
double calculateScore(KeywordDO keyword);

      // 综合评分 = 搜索量 * 0.3 + 转化率 * 0.3 + 竞争度(反向) * 0.2 + 当前引用数 * 0.2                                                                                                                  
}

  ---
2. 缺少的核心 GEO 功能

A. 引用追踪系统（最重要！）

GEO 的核心是让 AI 引用你的内容，但你完全没有追踪哪些内容被引用了。

-- 引用记录表                                                                                                                                                                                          
CREATE TABLE geo_citation (
id BIGINT PRIMARY KEY,
content_id BIGINT NOT NULL COMMENT '内容ID',
ai_engine VARCHAR(32) COMMENT 'AI引擎: chatgpt|claude|perplexity|google_ai',
query_prompt TEXT COMMENT '用户查询prompt',
ai_response TEXT COMMENT 'AI回答片段',
cited_position INT COMMENT '引用位置',
refer_url VARCHAR(512) COMMENT '引用URL',
created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
INDEX idx_content_id (content_id),
INDEX idx_ai_engine (ai_engine),
INDEX idx_created_time (created_time)
);

-- 引用汇总                                                                                                                                                                                            
CREATE TABLE geo_citation_summary (
id BIGINT PRIMARY KEY,
word_id BIGINT NOT NULL COMMENT '蒸馏词ID',
total_citations INT DEFAULT 0 COMMENT '总引用次数',
chatgpt_citations INT DEFAULT 0,
claude_citations INT DEFAULT 0,
weekly_citations INT DEFAULT 0 COMMENT '本周引用',
monthly_citations INT DEFAULT 0 COMMENT '本月引用',
update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
UNIQUE KEY uk_word_id (word_id)
);

实现方式：
1. 定期用 AI 去搜索你的蒸馏词
2. 检查返回结果中是否包含你的内容
3. 记录引用情况和排名

@Service                                                                                                                                                                                               
public class CitationTrackingService {

      @Scheduled(cron = "0 0 */6 * * ?") // 每6小时检查一次                                                                                                                                              
      public void trackCitations() {
          List<GeoWordDO> words = geoWordMapper.selectList();
          for (GeoWordDO word : words) {
              // 用不同AI引擎查询                                                                                                                                                                        
              trackOnEngine(word, "chatgpt");
              trackOnEngine(word, "claude");
              trackOnEngine(word, "perplexity");
          }
      }

      private void trackOnEngine(GeoWordDO word, String engine) {
          String query = word.getTarget();
          String response = aiService.query(engine, query);

          // 检查是否引用了你的内容                                                                                                                                                                      
          if (containsMyContent(response, word)) {
              saveCitation(word, engine, query, response);
          }
      }
}

B. 内容优化建议系统

// 基于AI分析，给出优化建议                                                                                                                                                                            
public interface ContentOptimizationService {

      // 分析内容在GEO方面的表现                                                                                                                                                                         
      OptimizationReport analyzeContent(Long contentId);

      // 生成优化建议                                                                                                                                                                                    
      List<OptimizationSuggestion> generateSuggestions(ContentDO content);
}

public class OptimizationSuggestion {
private String type; // 缺失的蒸馏词、结构问题、长度问题等                                                                                                                                         
private String description;
private String suggestion;
private int priority; // 1-5                                                                                                                                                                       
}

C. A/B 测试系统

-- 内容变体表                                                                                                                                                                                          
CREATE TABLE geo_content_variant (
id BIGINT PRIMARY KEY,
original_content_id BIGINT NOT NULL,
variant_name VARCHAR(64) COMMENT '变体名称',
content TEXT COMMENT '变体内容',
strategy VARCHAR(32) COMMENT '优化策略',
citation_count INT DEFAULT 0,
conversion_rate DECIMAL(3,2) DEFAULT 0,
is_winner TINYINT DEFAULT 0 COMMENT '是否获胜版本',
created_time DATETIME
);

-- A/B测试记录                                                                                                                                                                                         
CREATE TABLE geo_ab_test (
id BIGINT PRIMARY KEY,
test_name VARCHAR(128) NOT NULL,
content_id BIGINT NOT NULL,
variants JSON COMMENT '变体ID列表',
start_time DATETIME,
end_time DATETIME,
status TINYINT DEFAULT 0 COMMENT '0-进行中 1-已完成',
winner_id BIGINT COMMENT '获胜变体ID'                                                                                                                                                              
);

  ---
3. 现有模块的改进建议

AI 创作模块

// 增加"GEO优化模式"                                                                                                                                                                                   
public class GeoOptimizedCreationStrategy {

      public ContentDO createWithGeoOptimization(GeoWordDO word) {
          // 1. 自动分析该词的最佳结构                                                                                                                                                                   
          String structure = analyzeBestStructure(word);

          // 2. 自动包含相关蒸馏词                                                                                                                                                                       
          List<String> relatedWords = getRelatedWords(word);

          // 3. 生成AI指令时注入SEO要素                                                                                                                                                                  
          String prompt = buildGeoOptimizedPrompt(word, structure, relatedWords);

          return aiService.generate(prompt);
      }
}

发布任务模块

-- 增加平台权重管理                                                                                                                                                                                    
ALTER TABLE geo_publish_task ADD COLUMN platform_authority INT DEFAULT 0 COMMENT '平台权威度 0-100';
ALTER TABLE geo_publish_task ADD COLUMN expected_citation_score INT DEFAULT 0 COMMENT '预期引用分数';

-- 发布效果追踪                                                                                                                                                                                        
CREATE TABLE geo_publish_result (
id BIGINT PRIMARY KEY,
task_id BIGINT NOT NULL,
published_url VARCHAR(512),
indexed_by_ai TINYINT DEFAULT 0 COMMENT '是否被AI索引',
citation_count INT DEFAULT 0,
publish_date DATE,
first_citation_date DATE,
INDEX idx_task_id (task_id)
);

  ---
4. 优先级建议

| 优先级 | 功能        | 价值             |
  |-----|-----------|----------------|
| P0  | 引用追踪系统    | 核心价值，没有这个不知道效果 |
| P0  | 蒸馏词评分系统   | 知道该优化哪些词       |
| P1  | 关键词自动挖掘   | 减少人工工作         |
| P1  | GEO优化内容生成 | 直接提升效果         |
| P2  | A/B测试系统   | 持续优化           |
| P2  | 竞争对手监控    | 战略参考           |

  ---
最关键的改进：你的系统现在只管"生成"和"发布"，但没有数据闭环。没有追踪哪些内容被 AI 引用了，就无法知道哪些策略有效。

建议先加上引用追踪系统，这个是 GEO 的核心指标。你要不要我帮你实现一个？