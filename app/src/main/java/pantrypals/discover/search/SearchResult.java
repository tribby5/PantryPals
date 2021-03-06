package pantrypals.discover.search;

import java.io.Serializable;

/**
 * Created by adityasrinivasan on 09/12/17.
 */

public class SearchResult implements Serializable {

    private SearchType type;
    private Object info;
    private String id;

    public SearchResult(SearchType type, Object info, String id) {
        this.type = type;
        this.info = info;
        this.id = id;
    }

    public SearchType getType() {
        return type;
    }

    public Object getInfo() {
        return info;
    }

    public String getId() {
        return id;
    }
}
