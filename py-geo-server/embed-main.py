# ✅ 使用 modelscope 下载模型
# from modelscope import snapshot_download
# model_dir = snapshot_download("Xorbits/bge-m3")
import torch  # ✅ 就这一行，非常简单
import torch.nn.functional as F
from transformers import AutoTokenizer, AutoModel

model_dir = r"D:\modelscope\hub\models\Xorbits\bge-m3"

tokenizer = AutoTokenizer.from_pretrained(model_dir)
model = AutoModel.from_pretrained(
    model_dir,
    trust_remote_code=True,
).half().cuda()
model.eval()

def get_embeddings(texts, max_length=512):
    batch_dict = tokenizer(
        texts,
        max_length=max_length,
        padding=True,
        truncation=True,
        return_tensors="pt"
    ).to("cuda")
    with torch.no_grad():
        outputs = model(**batch_dict)
    embeddings = outputs.last_hidden_state[:, 0, :]
    return F.normalize(embeddings, p=2, dim=1)

# 测试
query = ["中国的首都是哪里？"]
docs = [
    "北京是中华人民共和国的首都。",
    "上海是中国最大的城市。",
]
q_emb = get_embeddings(query)
d_emb = get_embeddings(docs)
print("q_emb",q_emb)
print("d_emb",d_emb)
scores = (q_emb @ d_emb.T).squeeze()
for doc, score in zip(docs, scores):
    print(f"{score:.4f} | {doc}")

