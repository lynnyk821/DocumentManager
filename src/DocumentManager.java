import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {
    private final Map<String, Document> storage = new LinkedHashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if(document.getId() == null) {
            document.setId(UUID.randomUUID().toString());
        }

        storage.put(document.getId(), document);

        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        List<Document> result = new ArrayList<>();
        for(Map.Entry<String, Document> entry : storage.entrySet()) {
            String title = entry.getValue().getTitle();
            String content = entry.getValue().getContent();
            Author author = entry.getValue().getAuthor();

            Instant createdAt = entry.getValue().getCreated();
            Instant from = request.getCreatedFrom(), to = request.getCreatedTo();

            if(isAnyPrefixesStartWithTitle(title, request.getTitlePrefixes())           &&
               isAnyContentsContainsContent(content, request.getContainsContents())     &&
               isAnyAuthorsIdsEqualsToAuthorId(author.getId(), request.getAuthorIds())  &&
               isCreatedDateOfFileBetweenFromAndTo(createdAt, from, to)) {
                result.add(entry.getValue());
            }
        }

        return result;
    }

    private Boolean isAnyPrefixesStartWithTitle(String title, List<String> titlePrefixes) {
        if(title == null || titlePrefixes == null) {
            return false;
        }
        return titlePrefixes.stream().anyMatch(title::startsWith);
    }

    private Boolean isAnyContentsContainsContent(String content, List<String> containsContents) {
        if(content == null || containsContents == null) {
            return false;
        }
        return containsContents.stream().anyMatch(content::contains);
    }

    private Boolean isAnyAuthorsIdsEqualsToAuthorId(String thisAuthorId, List<String> authorIds) {
        if(thisAuthorId == null || authorIds == null) {
            return false;
        }
        return authorIds.stream().anyMatch(thisAuthorId::equals);
    }

    private Boolean isCreatedDateOfFileBetweenFromAndTo(Instant created, Instant to, Instant from) {
        if (created == null) return false;
        if (from != null && created.isBefore(from)) return false;
        return to == null || !created.isAfter(to);
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
