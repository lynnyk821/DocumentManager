public class Main {
    public static void main(String[] args) {
        DocumentManager documentManager = new DocumentManager();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id("12414")
                .author(new DocumentManager.Author("1", "name"))
                .title("title")
                .build();

        System.out.println(documentManager.save(document));
    }
}