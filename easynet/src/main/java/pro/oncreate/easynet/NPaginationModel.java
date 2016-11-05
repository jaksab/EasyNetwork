package pro.oncreate.easynet;

/**
 * Created by andrej on 05.11.16.
 */

public class NPaginationModel {

    static final int DEFAULT_ITEMS_COUNT = 20;
    static final int DEFAULT_PAGE_NUMBER = 1;

    String pageNumberKey;
    String countItemsKey;

    int itemsCount = DEFAULT_ITEMS_COUNT;
    int pageNumber = DEFAULT_PAGE_NUMBER;

    public NPaginationModel(String pageNumber, String countItems) {
        this.pageNumberKey = pageNumber;
        this.countItemsKey = countItems;
    }

    // Utils
    public static int calculateNextPage(int itemsCountNow, int maxPageCount) {
        int page = itemsCountNow / maxPageCount + 1;
        if (itemsCountNow % maxPageCount != 0)
            page++;
        return page;
    }
}
