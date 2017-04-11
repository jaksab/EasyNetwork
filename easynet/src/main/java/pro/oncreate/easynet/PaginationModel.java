package pro.oncreate.easynet;

import java.util.HashMap;

/**
 * Created by andrej on 05.11.16.
 */

@SuppressWarnings("unused,WeakerAccess")
public class PaginationModel {

    private HashMap<String, Integer> data = new HashMap<>();
    private PaginationInterface paginationInterface;

    PaginationModel(String... keys) {
        if (keys == null || keys.length == 0)
            throw new IllegalArgumentException("Pagination keys cannot be empty");

        for (String key : keys) data.put(key, 0);
    }

    public PaginationInterface getPaginationInterface() {
        return paginationInterface;
    }

    public void setPaginationInterface(PaginationInterface paginationInterface) {
        this.paginationInterface = paginationInterface;
    }

    public HashMap<String, Integer> getData() {
        return data;
    }

    public interface PaginationInterface {
        int getPaginationValue(String key);
    }
}
