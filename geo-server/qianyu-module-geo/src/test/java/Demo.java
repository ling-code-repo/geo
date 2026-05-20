import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;

import java.util.List;

public class Demo {

    public static void main(String[] args) {
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("C:\\Users\\leaf5\\Desktop\\11");
        System.out.println(documents);
    }
}
