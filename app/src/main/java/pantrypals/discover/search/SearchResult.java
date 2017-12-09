package pantrypals.discover.search;

/**
 * Created by adityasrinivasan on 09/12/17.
 */

public class SearchResult {

    private SearchType type;
    private Object info;

    public SearchResult(SearchType type, Object info) {
        this.type = type;
        this.info = info;
    }

    public SearchType getType() {
        return type;
    }

    public Object getInfo() {
        return info;
    }
}
