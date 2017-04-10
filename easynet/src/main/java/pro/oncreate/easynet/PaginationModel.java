package pro.oncreate.easynet;

/**
 * Created by andrej on 05.11.16.
 */

@SuppressWarnings("unused,WeakerAccess")
public class PaginationModel {

    // DEFAULT VALUES
    private static final int DEFAULT_ITEMS_COUNT = 20;
    private static final int DEFAULT_PAGE_NUMBER = -1;
    private static final int DEFAULT_PAGE_FROM_PRIMARY_KEY = -1;

    // KEYS FOR PAGINATION DATA
    String pageNumberKey;
    String pageFromPrimaryKey;
    String countItemsKey;

    // PAGINATION MEMBERS
    int itemsCount = DEFAULT_ITEMS_COUNT;
    int pageNumber = DEFAULT_PAGE_NUMBER;
    long pageFromPK = DEFAULT_PAGE_FROM_PRIMARY_KEY;
    private NPaginationInterface paginationInterface;

    // CONSTRUCTOR
    PaginationModel(String pageNumberKEY, String countItemsKEY, String pageFromPrimaryKEY) {
        this.pageNumberKey = pageNumberKEY;
        this.countItemsKey = countItemsKEY;
        this.pageFromPrimaryKey = pageFromPrimaryKEY;
    }

    // UTILS
    static int calculateNextPage(int itemsCountNow, int maxPageCount) {
        int page = itemsCountNow / maxPageCount + 1;
        if (itemsCountNow % maxPageCount != 0)
            page++;
        return page;
    }

    // GETTERS AND SETTERS
    public NPaginationInterface getPaginationInterface() {
        return paginationInterface;
    }

    public void setPaginationInterface(NPaginationInterface paginationInterface) {
        this.paginationInterface = paginationInterface;
    }

    // USE THIS INTERFACE IN ADAPTERS OR OTHER CLASSES TO SIMPLIFY PAGINATION FUNCTIONS
    public interface NPaginationInterface {
        int DEFAULT_ITEMS_COUNT = PaginationModel.DEFAULT_ITEMS_COUNT;
        int PAGE_NUMBER_NONE = PaginationModel.DEFAULT_PAGE_NUMBER;
        int LAST_PRIMARY_KEY_NONE = PaginationModel.DEFAULT_PAGE_FROM_PRIMARY_KEY;

        int getPaginationPageCount();

        int getPaginationPageNumber();

        long getPaginationLastPrimaryKey();
    }
}
